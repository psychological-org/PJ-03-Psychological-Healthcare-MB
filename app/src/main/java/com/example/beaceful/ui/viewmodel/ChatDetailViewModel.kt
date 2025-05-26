package com.example.beaceful.ui.viewmodel

import android.content.Context
import android.media.MediaRecorder
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beaceful.domain.amazon.S3Manager
import com.example.beaceful.domain.firebase.toMessage
import com.example.beaceful.domain.model.Message
import com.example.beaceful.domain.firebase.toFirebaseMessage
import com.example.beaceful.domain.firebase.toUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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

    var currentUserId: Int? = null
    var otherUserId: Int? = null
    var otherUserName: String? = null

    init {
        viewModelScope.launch {
            loadCurrentUserId()
        }
    }

    private suspend fun loadCurrentUserId() {
        val currentUserUid = auth.currentUser?.uid ?: return
        val snapshot = database.reference.child("users").child(currentUserUid)
            .get().await()
        println("loadCurrentUserId snapshot: $snapshot")
        val firebaseUser = snapshot.getValue(com.example.beaceful.domain.firebase.FirebaseUser::class.java)
        currentUserId = firebaseUser?.toUser()?.id
        println("Loaded currentUserId: $currentUserId")
    }

    fun setChatPartner(userId: Int, userName: String) {
        otherUserId = userId
        otherUserName = userName
        println("setChatPartner: userId=$userId, userName=$userName")
        loadMessages()
    }

    fun loadMessages() {
        val currentId = currentUserId ?: return
        val otherId = otherUserId ?: return
        database.reference.child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    println("loadMessages snapshot: ${snapshot.childrenCount} messages found")
                    val messageList = snapshot.children.mapNotNull { snap ->
                        val firebaseMessage = snap.getValue(com.example.beaceful.domain.firebase.FirebaseMessage::class.java)
                        firebaseMessage?.toMessage()?.takeIf {
                            (it.senderId == currentId && it.receiverId == otherId) ||
                                    (it.senderId == otherId && it.receiverId == currentId)
                        }
                    }.sortedBy { it.createdAt }
                    messages.value = messageList
                    println("Loaded ${messageList.size} messages between $currentId and $otherId")
                }

                override fun onCancelled(error: DatabaseError) {
                    println("Load messages error: ${error.message}")
                }
            })
    }

    fun sendMessage(content: String) {
        val currentId = currentUserId ?: return
        val otherId = otherUserId ?: return
        val newMessage = Message(
            id = (messages.value.maxOfOrNull { it.id } ?: 0) + 1,
            content = content,
            videoUrl = null,
            imageUrl = null,
            voiceUrl = null,
            senderId = currentId,
            receiverId = otherId,
            isRead = false,
            createdAt = LocalDateTime.now(ZoneId.of("UTC+7"))
        )
        database.reference.child("messages").child("msg${newMessage.id}")
            .setValue(newMessage.toFirebaseMessage())
    }

    fun sendImageMessage(context: Context) {
        val uri = selectedImageUri.value ?: return
        viewModelScope.launch {
            val currentId = currentUserId ?: return@launch
            val otherId = otherUserId ?: return@launch
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
                val newMessage = Message(
                    id = (messages.value.maxOfOrNull { it.id } ?: 0) + 1,
                    content = null,
                    videoUrl = null,
                    imageUrl = imageUrl,
                    voiceUrl = null,
                    senderId = currentId,
                    receiverId = otherId,
                    isRead = false,
                    createdAt = LocalDateTime.now(ZoneId.of("UTC+7"))
                )
                database.reference.child("messages").child("msg${newMessage.id}")
                    .setValue(newMessage.toFirebaseMessage())
                    .addOnSuccessListener {
                        println("Image message sent successfully: $imageUrl")
                        selectedImageUri.value = null // Reset sau khi gửi
                    }
                    .addOnFailureListener { e ->
                        println("Failed to send image message: ${e.message}")
                    }

                // Xóa file tạm
                tempFile.delete()
            } catch (e: Exception) {
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
            println("Error starting recording: ${e.message}")
            isRecording.value = false
        }
    }

    fun stopRecording(context: Context) {
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            isRecording.value = false
            tempVoiceFile?.let { file ->
                recordedVoiceUri.value = Uri.fromFile(file)
                println("Stopped recording: ${file.absolutePath}")
            }
        } catch (e: Exception) {
            println("Error stopping recording: ${e.message}")
            isRecording.value = false
        }
    }

    fun sendVoiceMessage(context: Context) {
        val uri = recordedVoiceUri.value ?: return
        viewModelScope.launch {
            val currentId = currentUserId ?: return@launch
            val otherId = otherUserId ?: return@launch
            try {
                val file = File(uri.path!!)
                // Tải lên S3
                val key = "voices/${UUID.randomUUID()}.m4a"
                val voiceUrl = S3Manager.uploadFile(file, key)

                // Tạo và gửi tin nhắn với voiceUrl
                val newMessage = Message(
                    id = (messages.value.maxOfOrNull { it.id } ?: 0) + 1,
                    content = null,
                    videoUrl = null,
                    imageUrl = null,
                    voiceUrl = voiceUrl,
                    senderId = currentId,
                    receiverId = otherId,
                    isRead = false,
                    createdAt = LocalDateTime.now(ZoneId.of("UTC+7"))
                )
                database.reference.child("messages").child("msg${newMessage.id}")
                    .setValue(newMessage.toFirebaseMessage())
                    .addOnSuccessListener {
                        println("Voice message sent successfully: $voiceUrl")
                        recordedVoiceUri.value = null // Reset sau khi gửi
                        tempVoiceFile?.delete()
                        tempVoiceFile = null
                    }
                    .addOnFailureListener { e ->
                        println("Failed to send voice message: ${e.message}")
                    }
            } catch (e: Exception) {
                println("Error uploading voice: ${e.message}")
            }
        }
    }

    fun clearRecordedVoice() {
        recordedVoiceUri.value = null
        tempVoiceFile?.delete()
        tempVoiceFile = null
    }

    fun markMessagesAsRead() {
        viewModelScope.launch {
            println("markMessagesAsRead called")
            if (currentUserId == null) {
                loadCurrentUserId()
                if (currentUserId == null) {
                    println("currentUserId still null after reload")
                    return@launch
                }
            }
            val currentId = currentUserId ?: return@launch println("currentUserId is null")
            val otherId = otherUserId ?: return@launch println("otherUserId is null")
            println("Filtering messages: receiverId=$currentId, senderId=$otherId")
            database.reference.child("messages")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        println("markMessagesAsRead snapshot: ${snapshot.childrenCount} messages found")
                        var updatedCount = 0
                        snapshot.children.forEach { snap ->
                            val firebaseMessage = snap.getValue(com.example.beaceful.domain.firebase.FirebaseMessage::class.java)
                            println("Processing message: $firebaseMessage")
                            val message = firebaseMessage?.toMessage()
                            if (message != null) {
                                println("Message after mapping: $message")
                                if (message.senderId == otherId && message.receiverId == currentId && !message.isRead) {
                                    val updatedMessage = message.copy(isRead = true)
                                    database.reference.child("messages").child(snap.key!!)
                                        .setValue(updatedMessage.toFirebaseMessage())
                                        .addOnSuccessListener {
                                            println("Updated read status for message ${snap.key} to true")
                                            updatedCount++
                                        }
                                        .addOnFailureListener {
                                            println("Failed to update read status for ${snap.key}: ${it.message}")
                                        }
                                } else {
                                    println("Message skipped: senderId=${message.senderId}, receiverId=${message.receiverId}, isRead=${message.isRead}")
                                }
                            } else {
                                println("Failed to map FirebaseMessage to Message: $firebaseMessage")
                            }
                        }
                        println("Total messages updated: $updatedCount")
                    }

                    override fun onCancelled(error: DatabaseError) {
                        println("Mark messages as read error: ${error.message}")
                    }
                })
        }
    }
    override fun onCleared() {
        super.onCleared()
        mediaRecorder?.release()
        tempVoiceFile?.delete()
    }
}