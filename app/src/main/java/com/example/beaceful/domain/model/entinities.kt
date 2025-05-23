package com.example.beaceful.domain.model

import java.time.*

data class User(
    val id: Int,
    val fullName: String,
    val roleId: Int,
    val biography: String? = null,
    val yearOfBirth: Int? = null,
    val yearOfExperience: Int? = null,
    val avatarUrl: String? = null,
    val backgroundUrl: String? = null,
    val email: String,
    val phone: String? = null,
    val password: String,
    val headline: String? = null
)

data class Role(
    val id: Int,
    val name: RoleType
)

data class Follower(
    val id: Int,
    val status: FriendStatus = FriendStatus.PENDING,
    val senderId: Int,
    val receiverId: Int
)

data class Message(
    val id: Int,
    val content: String? = null,
    val videoUrl: String? = null,
    val imageUrl: String? = null,
    val voiceUrl: String? = null,
    val senderId: Int,
    val receiverId: Int,
    val isRead: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now()
)

data class Notification(
    val id: Int,
    val content: String,
    val link: String? = null,
    val type: NotificationType = NotificationType.SYSTEM
)

data class UserNotification(
    val id: Int,
    val receiverId: Int,
    val notificationId: Int,
    val isRead: Boolean = false,
    val content: String
)

data class Appointment(
    val id: Int,
    val status: AppointmentStatus = AppointmentStatus.PENDING,
    val patientId: Int,
    val doctorId: Int,
    val appointmentDate: LocalDate,
    val appointmentTime: LocalTime,
    val rating: Int? = null,          // 1–5 sao
    val review: String? = null
)

data class Diary(
    val id: Int,
    val emotion: Emotions,
    val title: String = "No title",
    val content: String? = null,
    val imageUrl: String? = null,
    val voiceUrl: String? = null,
    val posterId: Int,
    val createdAt: LocalDateTime = LocalDateTime.now()
)

data class Topic(
    val id: Int,
    val content: String,
    val avatarUrl: String? = null
)

data class Collection(
    val id: Int,
    val content: String,
    val resourceId: String,
    val topicId: Int,
    val type: CollectionType = CollectionType.MUSIC
)

data class CollectionSeen(
    val collectionId: Int,
    val userId: Int
)

data class Community(
    val id: Int,
    val name: String,
    val content: String,
    val adminId: Int,
    val avatarUrl: String? = null
)

data class ParticipantCommunity(
    val userId: Int,
    val communityId: Int
)

data class Post(
    val id: Int,
    val content: String,
    val posterId: Int,
    val communityId: Int? = null,
    val visibility: PostVisibility = PostVisibility.PUBLIC,
    val imageUrl: String? = null,
    val reactCount: Int = 0,
    val createdAt: LocalDateTime = LocalDateTime.now()
)

data class Comment(
    val id: Int,
    val content: String,
    val imageUrl: String? = null,
    val userId: Int,
    val postId: Int,
    val reactCount: Int = 0,
    val createdAt: LocalDateTime = LocalDateTime.now()
)

data class DoctorExpertise(
    val id: Int,
    val title: String,
    val content: String,
    val doctorId: Int
)
