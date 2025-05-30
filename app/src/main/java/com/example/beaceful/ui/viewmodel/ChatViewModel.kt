package com.example.beaceful.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beaceful.domain.firebase.FirebaseMessage
import com.example.beaceful.domain.firebase.FirebaseUser
import com.example.beaceful.domain.firebase.toMessage
import com.example.beaceful.domain.firebase.toUser
import com.example.beaceful.domain.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.launch

data class ChatPreview(
    val userId: Int,
    val lastMessage: String,
    val createdAt: String,
    val isNewMessage: Boolean
)

class ChatViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance("https://chatapplication-a7712-default-rtdb.asia-southeast1.firebasedatabase.app")
    val users = mutableStateOf<List<User>>(emptyList())
    val chatPreviews = mutableStateOf<Map<Int, ChatPreview>>(emptyMap())
    private var currentUserId: Int? = null

    init {
        loadCurrentUserId()
        loadUsers()
    }

    private fun loadCurrentUserId() {
        val currentUserUid = auth.currentUser?.uid ?: return
        database.reference.child("users").child(currentUserUid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val firebaseUser = snapshot.getValue(FirebaseUser::class.java)
                    currentUserId = firebaseUser?.toUser()?.id
                    println("Current user ID: $currentUserId")
                }

                override fun onCancelled(error: DatabaseError) {
                    println("Load current user ID error: ${error.message}")
                }
            })
    }

    fun loadUsers() {
        viewModelScope.launch {
            database.reference.child("users").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val currentUserUid = auth.currentUser?.uid
                    println("Current user UID: $currentUserUid")
                    println("Users snapshot: $snapshot")
                    val userList = snapshot.children.mapNotNull { snap ->
                        val firebaseUser = snap.getValue(FirebaseUser::class.java)
                        firebaseUser?.toUser()?.takeIf { snap.key != currentUserUid }
                    }
                    println("Loaded users: $userList")
                    users.value = userList
                    loadChatPreviews()
                }

                override fun onCancelled(error: DatabaseError) {
                    println("Load users error: ${error.message}")
                }
            })
        }
    }

    fun loadChatPreviews() {
        database.reference.child("messages").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                println("Messages snapshot: $snapshot")
                val previews = mutableMapOf<Int, ChatPreview>()
                val messages = snapshot.children.mapNotNull { snap ->
                    val firebaseMessage = snap.getValue(FirebaseMessage::class.java)
                    firebaseMessage?.toMessage()
                }
                val userIds = users.value.map { it.id }
                println("User IDs: $userIds")

                // Nhóm tin nhắn theo otherUserId (người liên quan)
                val groupedMessages = mutableMapOf<Int, MutableList<com.example.beaceful.domain.model.Message>>()
                messages.forEach { message ->
                    val otherUserId = if (message.senderId == currentUserId) message.receiverId else message.senderId
                    if (otherUserId in userIds) {
                        groupedMessages.getOrPut(otherUserId) { mutableListOf() }.add(message)
                    }
                }

                // Xử lý từng nhóm để lấy tin nhắn mới nhất
                groupedMessages.forEach { (otherUserId, msgList) ->
                    // Lấy tin nhắn mới nhất bất kể gửi hay nhận
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
                            createdAt = latestMessage.createdAt.format(DateTimeFormatter.ofPattern("HH:mm")),
                            isNewMessage = latestMessage.senderId != currentUserId && !latestMessage.isRead
                        )
                    }
                }
                println("Chat previews: $previews")
                chatPreviews.value = previews
            }

            override fun onCancelled(error: DatabaseError) {
                println("Load messages error: ${error.message}")
            }
        })
    }
}