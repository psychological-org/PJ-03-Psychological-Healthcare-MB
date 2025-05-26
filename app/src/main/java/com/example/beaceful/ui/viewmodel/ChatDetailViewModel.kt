package com.example.beaceful.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

class ChatDetailViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance("https://chatapplication-a7712-default-rtdb.asia-southeast1.firebasedatabase.app")
    val messages = mutableStateOf<List<Message>>(emptyList())
    var currentUserId: Int? = null
    var otherUserId: Int? = null
    var otherUserName: String? = null

    init {
        viewModelScope.launch {
            loadCurrentUserId()
        }
    }

    // Sử dụng coroutine để tải currentUserId đồng bộ
    private suspend fun loadCurrentUserId() {
        val currentUserUid = auth.currentUser?.uid ?: return
        val snapshot = database.reference.child("users").child(currentUserUid)
            .get().await()
        println("loadCurrentUserId snapshot: $snapshot") // Debug
        val firebaseUser = snapshot.getValue(com.example.beaceful.domain.firebase.FirebaseUser::class.java)
        currentUserId = firebaseUser?.toUser()?.id
        println("Loaded currentUserId: $currentUserId")
    }

    fun setChatPartner(userId: Int, userName: String) {
        otherUserId = userId
        otherUserName = userName
        println("setChatPartner: userId=$userId, userName=$userName") // Debug
        loadMessages()
    }

    fun loadMessages() {
        val currentId = currentUserId ?: return
        val otherId = otherUserId ?: return
        database.reference.child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    println("loadMessages snapshot: ${snapshot.childrenCount} messages found") // Debug
                    val messageList = snapshot.children.mapNotNull { snap ->
                        val firebaseMessage = snap.getValue(com.example.beaceful.domain.firebase.FirebaseMessage::class.java)
                        firebaseMessage?.toMessage()?.takeIf {
                            (it.senderId == currentId && it.receiverId == otherId) ||
                                    (it.senderId == otherId && it.receiverId == currentId)
                        }
                    }.sortedBy { it.createdAt }
                    messages.value = messageList
                    println("Loaded ${messageList.size} messages between $currentId and $otherId") // Debug
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

    fun markMessagesAsRead() {
        viewModelScope.launch {
            println("markMessagesAsRead called") // Debug
            if (currentUserId == null) {
                loadCurrentUserId() // Tải lại nếu null
                if (currentUserId == null) {
                    println("currentUserId still null after reload")
                    return@launch
                }
            }
            val currentId = currentUserId ?: return@launch println("currentUserId is null")
            val otherId = otherUserId ?: return@launch println("otherUserId is null")
            println("Filtering messages: receiverId=$currentId, senderId=$otherId") // Debug
            database.reference.child("messages")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        println("markMessagesAsRead snapshot: ${snapshot.childrenCount} messages found") // Debug
                        var updatedCount = 0
                        snapshot.children.forEach { snap ->
                            val firebaseMessage = snap.getValue(com.example.beaceful.domain.firebase.FirebaseMessage::class.java)
                            println("Processing message: $firebaseMessage") // Debug
                            val message = firebaseMessage?.toMessage()
                            if (message != null) {
                                println("Message after mapping: $message") // Debug
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
}