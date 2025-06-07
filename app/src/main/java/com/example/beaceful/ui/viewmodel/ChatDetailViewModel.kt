package com.example.beaceful.ui.viewmodel

import android.content.Context
import android.media.MediaRecorder
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beaceful.domain.amazon.S3Manager
import com.example.beaceful.domain.firebase.FirebaseMessage
import com.example.beaceful.domain.firebase.toMessage
import com.example.beaceful.domain.model.Message
import com.example.beaceful.domain.firebase.toFirebaseMessage
import com.example.beaceful.domain.firebase.toUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import java.time.ZoneId
import java.io.File
import java.util.UUID

class ChatDetailViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance("https://chatapplication-a7712-default-rtdb.asia-southeast1.firebasedatabase.app")
    val messages = mutableStateOf<List<Message>>(emptyList())
    val selectedImageUri = mutableStateOf<Uri?>(null)
    val isRecording = mutableStateOf(false)
    val recordedVoiceUri = mutableStateOf<Uri?>(null)
    private var mediaRecorder: MediaRecorder? = null
    private var tempVoiceFile: File? = null
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    var currentUserId: String? = null
    var otherUserId: String? = null
    var otherUserName: String? = null

    init {
        viewModelScope.launch {
            loadCurrentUserId()
        }
    }

    private suspend fun loadCurrentUserId() {
        val currentUserUid = auth.currentUser?.uid ?: run {
            _error.value = "Người dùng chưa đăng nhập"
            return
        }
        try {
            val snapshot = database.reference.child("users").child(currentUserUid).get().await()
            println("loadCurrentUserId snapshot: $snapshot")
            val firebaseUser = snapshot.getValue(com.example.beaceful.domain.firebase.FirebaseUser::class.java)
            currentUserId = firebaseUser?.toUser()?.id // uid
            println("Loaded currentUserId: $currentUserId")
            if (currentUserId == null) {
                _error.value = "Không tìm thấy thông tin người dùng"
            }
        } catch (e: Exception) {
            _error.value = "Lỗi tải thông tin người dùng: ${e.message}"
            println("Load current user ID error: ${e.message}")
        }
    }

    fun setChatPartner(userId: String, userName: String) {
        otherUserId = userId
        otherUserName = userName
        println("setChatPartner: userId=$userId, userName=$userName")
        loadMessages()
    }

    fun loadMessages() {
        val currentId = currentUserId ?: run {
            _error.value = "Người dùng chưa đăng nhập"
            return
        }
        val otherId = otherUserId ?: run {
            _error.value = "Không tìm thấy người nhận"
            return
        }
        database.reference.child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        println("loadMessages snapshot: ${snapshot.childrenCount} messages found")
                        val messageList = snapshot.children.mapNotNull { snap ->
                            try {
                                val firebaseMessage = snap.getValue(FirebaseMessage::class.java)
                                firebaseMessage?.toMessage()?.takeIf {
                                    (it.senderId == currentId && it.receiverId == otherId) ||
                                            (it.senderId == otherId && it.receiverId == currentId)
                                }
                            } catch (e: Exception) {
                                println("Error parsing message ${snap.key}: ${e.message}")
                                null
                            }
                        }.sortedBy { it.createdAt }
                        messages.value = messageList
                        println("Loaded ${messageList.size} messages between $currentId and $otherId")
                    } catch (e: Exception) {
                        _error.value = "Lỗi tải tin nhắn: ${e.message}"
                        println("Load messages error: ${e.message}")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    _error.value = "Lỗi tải tin nhắn: ${error.message}"
                    println("Load messages error: ${error.message}")
                }
            })
    }

    fun sendMessage(content: String) {
        val currentId = currentUserId ?: run {
            _error.value = "Người dùng chưa đăng nhập"
            return
        }
        val otherId = otherUserId ?: run {
            _error.value = "Không tìm thấy người nhận"
            return
        }
        val message = Message(
            id = 0,
            content = content,
            videoUrl = null,
            imageUrl = null,
            voiceUrl = null,
            senderId = currentId,
            receiverId = otherId,
            isRead = false,
            createdAt = LocalDateTime.now(ZoneId.of("UTC+7"))
        )
        database.reference.child("messages").push()
            .setValue(message.toFirebaseMessage())
            .addOnSuccessListener {
                println("Message sent: $content")
            }
            .addOnFailureListener { e ->
                _error.value = "Lỗi gửi tin nhắn: ${e.message}"
                println("Send message error: ${e.message}")
            }
    }

    fun sendImageMessage(context: Context) {
        val uri = selectedImageUri.value ?: run {
            _error.value = "Chưa chọn hình ảnh"
            return
        }
        viewModelScope.launch {
            val currentId = currentUserId ?: run {
                _error.value = "Người dùng chưa đăng nhập"
                return@launch
            }
            val otherId = otherUserId ?: run {
                _error.value = "Không tìm thấy người nhận"
                return@launch
            }
            try {
                // Tạo file tạm từ URI
                val inputStream = context.contentResolver.openInputStream(uri)
                val tempFile = File(context.cacheDir, "image_${UUID.randomUUID()}.jpg")
                inputStream?.use { input ->
                    tempFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }

                // Tải lên S3
                val key = "images/${UUID.randomUUID()}.jpg"
                val imageUrl = S3Manager.uploadFile(tempFile, key)

                // Tạo và gửi tin nhắn với imageUrl
                val message = Message(
                    id = 0, // Firebase sẽ tạo key
                    content = null,
                    videoUrl = null,
                    imageUrl = imageUrl,
                    voiceUrl = null,
                    senderId = currentId,
                    receiverId = otherId,
                    isRead = false,
                    createdAt = LocalDateTime.now(ZoneId.of("UTC+7"))
                )
                database.reference.child("messages").push()
                    .setValue(message.toFirebaseMessage())
                    .addOnSuccessListener {
                        println("Image message sent successfully: $imageUrl")
                        selectedImageUri.value = null // Reset sau khi gửi
                    }
                    .addOnFailureListener { e ->
                        _error.value = "Lỗi gửi tin nhắn hình ảnh: ${e.message}"
                        println("Send image message error: ${e.message}")
                    }

                // Xóa file tạm
                tempFile.delete()
            } catch (e: Exception) {
                _error.value = "Lỗi tải lên hình ảnh: ${e.message}"
                println("Error uploading image: ${e.message}")
            }
        }
    }

    fun setSelectedImage(uri: Uri?) {
        selectedImageUri.value = uri
    }

    fun clearSelectedImage() {
        selectedImageUri.value = null
    }

    fun startRecording(context: Context) {
        try {
            // Kiểm tra xem có đang ghi âm không
            if (isRecording.value) {
                _error.value = "Đang ghi âm, vui lòng dừng trước khi bắt đầu lại"
                return
            }
            // Xóa file tạm cũ nếu có
            tempVoiceFile?.delete()
            tempVoiceFile = File(context.cacheDir, "voice_${UUID.randomUUID()}.m4a")
            mediaRecorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(tempVoiceFile!!.absolutePath)
                prepare()
                start()
            }
            isRecording.value = true
            println("Started recording: ${tempVoiceFile!!.absolutePath}")
        } catch (e: Exception) {
            _error.value = "Lỗi bắt đầu ghi âm: ${e.message}"
            println("Error starting recording: ${e.message}")
            isRecording.value = false
            mediaRecorder?.release()
            mediaRecorder = null
            tempVoiceFile?.delete()
            tempVoiceFile = null
        }
    }

    fun stopRecording(context: Context) {
        try {
            if (!isRecording.value) {
                _error.value = "Không có bản ghi âm đang thực hiện"
                return
            }
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            isRecording.value = false
            tempVoiceFile?.let { file ->
                if (file.exists()) {
                    recordedVoiceUri.value = Uri.fromFile(file)
                    println("Stopped recording: ${file.absolutePath}")
                } else {
                    _error.value = "File ghi âm không tồn tại"
                    println("Error: Recording file does not exist")
                }
            } ?: run {
                _error.value = "Không tìm thấy file ghi âm"
                println("Error: No recording file")
            }
        } catch (e: Exception) {
            _error.value = "Lỗi dừng ghi âm: ${e.message}"
            println("Error stopping recording: ${e.message}")
            isRecording.value = false
            mediaRecorder?.release()
            mediaRecorder = null
            tempVoiceFile?.delete()
            tempVoiceFile = null
        }
    }

    fun sendVoiceMessage(context: Context) {
        val uri = recordedVoiceUri.value ?: run {
            _error.value = "Chưa có bản ghi âm"
            return
        }
        viewModelScope.launch {
            val currentId = currentUserId ?: run {
                _error.value = "Người dùng chưa đăng nhập"
                return@launch
            }
            val otherId = otherUserId ?: run {
                _error.value = "Không tìm thấy người nhận"
                return@launch
            }
            try {
                val file = File(uri.path!!)
                // Tải lên S3
                val key = "voices/${UUID.randomUUID()}.m4a"
                val voiceUrl = S3Manager.uploadFile(file, key)

                // Tạo và gửi tin nhắn với voiceUrl
                val message = Message(
                    id = 0,
                    content = null,
                    videoUrl = null,
                    imageUrl = null,
                    voiceUrl = voiceUrl,
                    senderId = currentId,
                    receiverId = otherId,
                    isRead = false,
                    createdAt = LocalDateTime.now(ZoneId.of("UTC+7"))
                )
                database.reference.child("messages").push()
                    .setValue(message.toFirebaseMessage())
                    .addOnSuccessListener {
                        println("Voice message sent successfully: $voiceUrl")
                        recordedVoiceUri.value = null // Reset sau khi gửi
                        tempVoiceFile?.delete()
                        tempVoiceFile = null
                    }
                    .addOnFailureListener { e ->
                        _error.value = "Lỗi gửi tin nhắn thoại: ${e.message}"
                        println("Send voice message error: ${e.message}")
                    }
            } catch (e: Exception) {
                _error.value = "Lỗi tải lên bản ghi âm: ${e.message}"
                println("Error uploading voice: ${e.message}")
            }
        }
    }

    fun clearRecordedVoice() {
        try {
            recordedVoiceUri.value = null
            tempVoiceFile?.delete()
            tempVoiceFile = null
            println("Cleared recorded voice")
        } catch (e: Exception) {
            _error.value = "Lỗi xóa bản ghi âm: ${e.message}"
            println("Error clearing recorded voice: ${e.message}")
        }
    }

    fun markMessagesAsRead() {
        viewModelScope.launch {
            val currentId = currentUserId ?: run {
                _error.value = "Người dùng chưa đăng nhập"
                return@launch
            }
            val otherId = otherUserId ?: run {
                _error.value = "Không tìm thấy người nhận"
                return@launch
            }
            println("markMessagesAsRead: currentId=$currentId, otherId=$otherId")
            try {
                database.reference.child("messages")
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            try {
                                println("markMessagesAsRead snapshot: ${snapshot.childrenCount} messages found")
                                var updatedCount = 0
                                snapshot.children.forEach { snap ->
                                    val firebaseMessage = snap.getValue(FirebaseMessage::class.java)
                                    val message = firebaseMessage?.toMessage()
                                    if (message != null && message.senderId == otherId && message.receiverId == currentId && !message.isRead) {
                                        val updatedMessage = message.copy(isRead = true)
                                        database.reference.child("messages").child(snap.key!!)
                                            .setValue(updatedMessage.toFirebaseMessage())
                                            .addOnSuccessListener {
                                                println("Updated read status for message ${snap.key} to true")
                                                updatedCount++
                                            }
                                            .addOnFailureListener { e ->
                                                println("Failed to update read status for ${snap.key}: ${e.message}")
                                            }
                                    }
                                }
                                println("Total messages updated: $updatedCount")
                            } catch (e: Exception) {
                                _error.value = "Lỗi đánh dấu tin nhắn đã đọc: ${e.message}"
                                println("Mark messages as read error: ${e.message}")
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            _error.value = "Lỗi đánh dấu tin nhắn đã đọc: ${error.message}"
                            println("Mark messages as read error: ${error.message}")
                        }
                    })
            } catch (e: Exception) {
                _error.value = "Lỗi đánh dấu tin nhắn đã đọc: ${e.message}"
                println("Mark messages as read error: ${e.message}")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            tempVoiceFile?.delete()
            tempVoiceFile = null
            recordedVoiceUri.value = null
            isRecording.value = false
            println("Cleared MediaRecorder and temp file")
        } catch (e: Exception) {
            println("Error in onCleared: ${e.message}")
        }
    }
}