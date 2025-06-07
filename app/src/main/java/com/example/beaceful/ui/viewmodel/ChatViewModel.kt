package com.example.beaceful.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beaceful.domain.firebase.FirebaseMessage
import com.example.beaceful.domain.firebase.FirebaseUser
import com.example.beaceful.domain.firebase.toMessage
import com.example.beaceful.domain.firebase.toUser
import com.example.beaceful.domain.model.Message
import com.example.beaceful.domain.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.launch

data class ChatPreview(
    val userId: String,
    val lastMessage: String,
    val createdAt: String,
    val isNewMessage: Boolean
)

class ChatViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance("https://chatapplication-a7712-default-rtdb.asia-southeast1.firebasedatabase.app")
    val users = mutableStateOf<List<User>>(emptyList())
    val chatPreviews = mutableStateOf<Map<String, ChatPreview>>(emptyMap())
    private var currentUserId: String? = null
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadCurrentUserId()
        loadUsers()
    }

    private fun loadCurrentUserId() {
        val currentUserUid = auth.currentUser?.uid ?: run {
            _error.value = "Người dùng chưa đăng nhập"
            return
        }
        database.reference.child("users").child(currentUserUid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        if (snapshot.exists()) {
                            val firebaseUser = snapshot.getValue(FirebaseUser::class.java)
                            if (firebaseUser != null) {
                                currentUserId = firebaseUser.toUser().id // uid
                                println("Current user ID: $currentUserId")
                            } else {
                                _error.value = "Dữ liệu người dùng không hợp lệ"
                            }
                        } else {
                            _error.value = "Không tìm thấy thông tin người dùng"
                        }
                    } catch (e: Exception) {
                        _error.value = "Lỗi tải thông tin người dùng: ${e.message}"
                        println("Load current user ID error: ${e.message}")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    _error.value = "Lỗi tải thông tin người dùng: ${error.message}"
                    println("Load current user ID error: ${error.message}")
                }
            })
    }

    fun loadUsers() {
        viewModelScope.launch {
            database.reference.child("users").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        val currentUserUid = auth.currentUser?.uid
                        println("Current user UID: $currentUserUid")
                        println("Users snapshot: $snapshot")
                        val userList = snapshot.children.mapNotNull { snap ->
                            try {
                                val firebaseUser = snap.getValue(FirebaseUser::class.java)
                                if (firebaseUser?.fullName != null && snap.key != currentUserUid) {
                                    firebaseUser.toUser()
                                } else {
                                    null
                                }
                            } catch (e: Exception) {
                                println("Error parsing user ${snap.key}: ${e.message}")
                                null
                            }
                        }
                        println("Loaded users: $userList")
                        users.value = userList
                        loadChatPreviews()
                    } catch (e: Exception) {
                        _error.value = "Lỗi tải danh sách người dùng: ${e.message}"
                        println("Load users error: ${e.message}")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    _error.value = "Lỗi tải danh sách người dùng: ${error.message}"
                    println("Load users error: ${error.message}")
                }
            })
        }
    }

    fun loadChatPreviews() {
        database.reference.child("messages").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    println("Messages snapshot: $snapshot")
                    val previews = mutableMapOf<String, ChatPreview>()
                    val messages = snapshot.children.mapNotNull { snap ->
                        try {
                            val firebaseMessage = snap.getValue(FirebaseMessage::class.java)
                            if (firebaseMessage?.senderId != null && firebaseMessage.receiverId != null && firebaseMessage.createdAt != null) {
                                firebaseMessage.toMessage()
                            } else {
                                null
                            }
                        } catch (e: Exception) {
                            println("Error parsing message ${snap.key}: ${e.message}")
                            null
                        }
                    }
                    val userIds = users.value.map { it.id } // uid
                    println("User IDs: $userIds")

                    val groupedMessages = mutableMapOf<String, MutableList<Message>>()
                    messages.forEach { message ->
                        val otherUserId = if (message.senderId == currentUserId) message.receiverId else message.senderId
                        if (otherUserId in userIds) {
                            groupedMessages.getOrPut(otherUserId) { mutableListOf() }.add(message)
                        }
                    }

                    groupedMessages.forEach { (otherUserId, msgList) ->
                        val latestMessage = msgList.maxByOrNull { it.createdAt }
                        if (latestMessage != null) {
                            println("Processing message: senderId=${latestMessage.senderId}, receiverId=${latestMessage.receiverId}, otherUserId=$otherUserId, content=${latestMessage.content}, createdAt=${latestMessage.createdAt}")
                            previews[otherUserId] = ChatPreview(
                                userId = otherUserId,
                                lastMessage = if (latestMessage.senderId == currentUserId) {
                                    "Bạn: ${latestMessage.content ?: "Tin nhắn mới"}"
                                } else {
                                    latestMessage.content ?: "Tin nhắn mới"
                                },
                                createdAt = latestMessage.createdAt.format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                                isNewMessage = latestMessage.senderId != currentUserId && !latestMessage.isRead
                            )
                        }
                    }
                    println("Chat previews: $previews")
                    chatPreviews.value = previews
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
}