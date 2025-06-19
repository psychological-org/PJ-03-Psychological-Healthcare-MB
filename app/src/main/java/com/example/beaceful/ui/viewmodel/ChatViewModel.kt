package com.example.beaceful.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beaceful.core.util.UserSession
import com.example.beaceful.domain.firebase.FirebaseMessage
import com.example.beaceful.domain.firebase.FirebaseUser
import com.example.beaceful.domain.firebase.toMessage
import com.example.beaceful.domain.firebase.toUser
import com.example.beaceful.domain.model.Message
import com.example.beaceful.domain.model.User
import com.example.beaceful.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import javax.inject.Inject

data class ChatPreview(
    val userId: String,
    val lastMessage: String,
    val createdAt: LocalDateTime,
    val isNewMessage: Boolean
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private val database = FirebaseDatabase.getInstance("https://chatapplication-a7712-default-rtdb.asia-southeast1.firebasedatabase.app")
    val users = mutableStateOf<List<User>>(emptyList())
    val chatPreviews = mutableStateOf<Map<String, ChatPreview>>(emptyMap())
    private var currentUserId: String? = null
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    private var messagesListener: ValueEventListener? = null

    init {
        loadCurrentUserId()
    }

    private fun loadCurrentUserId() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                currentUserId = UserSession.getCurrentUserId()
                println("Current user ID (mongoId): $currentUserId")
                if (currentUserId != null) {
                    ensureUserExistsInFirebase(currentUserId!!)
                    loadUsers()
                } else {
                    _error.value = "Người dùng chưa đăng nhập"
                    println("Load current user ID error: User not logged in")
                }
            } catch (e: IllegalStateException) {
                _error.value = "Người dùng chưa đăng nhập"
                println("Load current user ID error: ${e.message}")
            } catch (e: Exception) {
                _error.value = "Lỗi tải thông tin người dùng: ${e.message}"
                println("Load current user ID error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun ensureUserExistsInFirebase(userId: String) {
        try {
            _isLoading.value = true
            val snapshot = database.reference.child("users").child(userId).get().await()
            if (snapshot.exists()) {
                println("User $userId already exists in Firebase")
                return
            }
            var currentUser: User? = null
            repeat(3) { attempt ->
                try {
                    currentUser = userRepository.getUserById(userId)
                    if (currentUser != null) return@repeat
                } catch (e: Exception) {
                    println("Attempt ${attempt + 1} failed to fetch user $userId: ${e.message}")
                    if (attempt == 2) throw e
                    delay(1000)
                }
            }
            if (currentUser == null) {
                _error.value = "Không tìm thấy thông tin người dùng"
                println("No user data found for $userId")
                return
            }
            val firebaseUser = FirebaseUser(
                uid = currentUser!!.id,
                fullName = currentUser!!.fullName,
                roleId = currentUser!!.roleId,
                biography = currentUser!!.biography,
                yearOfBirth = currentUser!!.yearOfBirth,
                yearOfExperience = currentUser!!.yearOfExperience,
                avatarUrl = currentUser!!.avatarUrl,
                backgroundUrl = currentUser!!.backgroundUrl,
                email = currentUser!!.email,
                phone = currentUser!!.phone,
                password = null,
                headline = currentUser!!.headline
            )
            repeat(3) { attempt ->
                try {
                    database.reference.child("users").child(userId).setValue(firebaseUser).await()
                    println("Created user in Firebase for mongoId: $userId")
                    return
                } catch (e: Exception) {
                    println("Attempt ${attempt + 1} failed to create user $userId in Firebase: ${e.message}")
                    if (attempt == 2) {
                        _error.value = "Lỗi tạo user trong Firebase: ${e.message}"
                        println("Failed after 3 attempts: ${e.message}")
                    }
                    delay(100)
                }
            }
        } catch (e: Exception) {
            _error.value = "Lỗi đảm bảo user tồn tại: ${e.message}"
            println("Ensure user error: ${e.message}")
        } finally {
            _isLoading.value = false
        }
    }

    fun loadUsers() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val snapshot = database.reference.child("users").get().await()
                println("Users snapshot: ${snapshot.childrenCount} users found")
                val currentUserId = UserSession.getCurrentUserId()
                val userList = snapshot.children.mapNotNull { snap ->
                    try {
                        val firebaseUser = snap.getValue(FirebaseUser::class.java)
                        if (firebaseUser?.fullName != null && snap.key != currentUserId) {
                            firebaseUser.toUser().also {
                                println("Loaded user: id=${it.id}, name=${it.fullName}")
                            }
                        } else {
                            null
                        }
                    } catch (e: Exception) {
                        println("Error parsing user ${snap.key}: ${e.message}")
                        null
                    }
                }
                println("Loaded ${userList.size} users")
                users.value = userList
                loadChatPreviews()
            } catch (e: Exception) {
                _error.value = "Lỗi tải danh sách người dùng: ${e.message}"
                println("Load users error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadChatPreviews() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val currentUserId = currentUserId ?: run {
                    _error.value = "Người dùng chưa đăng nhập"
                    println("Load chat previews error: User not logged in")
                    return@launch
                }
                println("Loading chat previews for currentUserId: $currentUserId")
                // Gỡ listener cũ nếu có
                messagesListener?.let {
                    database.reference.child("messages").removeEventListener(it)
                    println("Removed previous messages listener")
                }
                messagesListener = object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        viewModelScope.launch {
                            _isLoading.value = true
                            try {
                                println("Messages snapshot: ${snapshot.childrenCount} messages found")
                                val previews = mutableMapOf<String, ChatPreview>()
                                val messages = snapshot.children.mapNotNull { snap ->
                                    try {
                                        val firebaseMessage = snap.getValue(FirebaseMessage::class.java)
                                        firebaseMessage?.toMessage()?.takeIf {
                                            it.senderId == currentUserId || it.receiverId == currentUserId
                                        }?.also {
                                            println("Valid message: senderId=${it.senderId}, receiverId=${it.receiverId}, content=${it.content}")
                                        }
                                    } catch (e: Exception) {
                                        println("Error parsing message ${snap.key}: ${e.message}")
                                        null
                                    }
                                }

                                if (messages.isEmpty()) {
                                    println("No messages found for user $currentUserId")
                                    chatPreviews.value = emptyMap()
                                    return@launch
                                }

                                val userIds = users.value.map { it.id }
                                println("Available user IDs: $userIds")

                                val groupedMessages = mutableMapOf<String, MutableList<Message>>()
                                messages.forEach { message ->
                                    val otherUserId = if (message.senderId == currentUserId) {
                                        message.receiverId
                                    } else {
                                        message.senderId
                                    }
                                    println("Message: senderId=${message.senderId}, receiverId=${message.receiverId}, otherUserId=$otherUserId")
                                    if (otherUserId in userIds) {
                                        groupedMessages.getOrPut(otherUserId) { mutableListOf() }.add(message)
                                    }
                                }

                                if (groupedMessages.isEmpty()) {
                                    println("No valid chat sessions found for user $currentUserId")
                                    chatPreviews.value = emptyMap()
                                    return@launch
                                }

                                groupedMessages.forEach { (otherUserId, msgList) ->
                                    val latestMessage = msgList.maxByOrNull { it.createdAt }
                                    if (latestMessage != null) {
                                        println("Creating preview for $otherUserId: senderId=${latestMessage.senderId}, receiverId=${latestMessage.receiverId}, content=${latestMessage.content}")
                                        previews[otherUserId] = ChatPreview(
                                            userId = otherUserId,
                                            lastMessage = if (latestMessage.senderId == currentUserId) {
                                                "Bạn: ${latestMessage.content ?: "[Media]"}"
                                            } else {
                                                latestMessage.content ?: "[Media]"
                                            },
                                            createdAt = latestMessage.createdAt,
                                            isNewMessage = latestMessage.senderId != currentUserId && !latestMessage.isRead
                                        )
                                    }
                                }

                                println("Loaded ${previews.size} chat previews")
                                chatPreviews.value = previews
                            } finally {
                                _isLoading.value = false
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        _error.value = "Lỗi tải tin nhắn: ${error.message}"
                        println("Load messages error: ${error.message}")
                        _isLoading.value = false
                    }
                }
                database.reference.child("messages").addValueEventListener(messagesListener!!)
            } catch (e: Exception) {
                _error.value = "Lỗi tải tin nhắn: ${e.message}"
                println("Load chat previews error: ${e.message}")
                _isLoading.value = false
            }
        }
    }

    fun clearDataOnLogout() {
        viewModelScope.launch {
            println("Clearing ChatViewModel data on logout")
            currentUserId = null
            users.value = emptyList()
            chatPreviews.value = emptyMap()
            _error.value = null
            messagesListener?.let {
                database.reference.child("messages").removeEventListener(it)
                println("Removed messages listener on logout")
            }
            messagesListener = null
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            println("Refreshing ChatViewModel data")
            _isLoading.value = true
            try {
                clearDataOnLogout()
                loadCurrentUserId()
            } finally {
                _isLoading.value = false
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        messagesListener?.let {
            database.reference.child("messages").removeEventListener(it)
            println("Removed messages listener on ViewModel cleared")
        }
    }
}