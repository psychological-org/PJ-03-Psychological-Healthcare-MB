package com.example.beaceful.domain.firebase

import com.example.beaceful.domain.model.Message
import com.example.beaceful.domain.model.User
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

data class FirebaseUser(
    var uid: String? = null,
    var fullName: String? = null,
    var roleId: Int? = null,
    var biography: String? = null,
    var yearOfBirth: Int? = null,
    var yearOfExperience: Int? = null,
    var avatarUrl: String? = null,
    var backgroundUrl: String? = null,
    var email: String? = null,
    var phone: String? = null,
    var password: String? = null,
    var headline: String? = null
) {
    constructor() : this(null, null, null, null, null, null, null, null, null, null, null, null)
}

data class FirebaseMessage(
    var id: Int? = null,
    var content: String? = null,
    var videoUrl: String? = null,
    var imageUrl: String? = null,
    var voiceUrl: String? = null,
    var senderId: String? = null,
    var receiverId: String? = null,
    var read: Boolean? = null,
    var createdAt: Long? = null
) {
    constructor() : this(null, null, null, null, null, null, null, null, null)

    override fun toString(): String {
        return "FirebaseMessage(id=$id, content=$content, read=$read, senderId=$senderId, receiverId=$receiverId)"
    }
}

fun FirebaseUser.toUser(): User {
    println("FirebaseUser.toUser: uid=$uid, fullName=$fullName")
    return User(
        id = uid ?: "0", // Dùng uid làm id
        fullName = fullName ?: "",
        roleId = roleId ?: 0,
        biography = biography,
        yearOfBirth = yearOfBirth,
        yearOfExperience = yearOfExperience,
        avatarUrl = avatarUrl,
        backgroundUrl = backgroundUrl,
        email = email ?: "",
        phone = phone,
        password = password ?: "",
        headline = headline,
        uid = uid
    )
}

fun FirebaseMessage.toMessage(): Message {
    println("FirebaseMessage.toMessage: $this")
    return Message(
        id = id ?: 0,
        content = content,
        videoUrl = videoUrl,
        imageUrl = imageUrl,
        voiceUrl = voiceUrl,
        senderId = senderId ?: "0",
        receiverId = receiverId ?: "0",
        isRead = read ?: false,
        createdAt = createdAt?.let {
            Instant.ofEpochMilli(it).atZone(ZoneId.of("UTC+7")).toLocalDateTime()
        } ?: LocalDateTime.now()
    )
}

fun Message.toFirebaseMessage(): FirebaseMessage {
    val firebaseMessage = FirebaseMessage(
        id = id,
        content = content,
        videoUrl = videoUrl,
        imageUrl = imageUrl,
        voiceUrl = voiceUrl,
        senderId = senderId,
        receiverId = receiverId,
        read = isRead,
        createdAt = createdAt.atZone(ZoneId.of("UTC+7")).toInstant().toEpochMilli()
    )
    println("Message.toFirebaseMessage: $firebaseMessage")
    return firebaseMessage
}