package com.example.beaceful.domain.model

import com.google.gson.annotations.SerializedName
import androidx.compose.ui.graphics.vector.ImageVector
import java.time.*
import java.time.format.DateTimeFormatter
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.beaceful.core.network.recommended.Emotion
import com.example.beaceful.core.network.recommended.SerializableEmotion


data class User(
    val id: String,
    val fullName: String,
    val roleId: Int? = null,
    val biography: String? = null,
    val yearOfBirth: Int? = null,
    val yearOfExperience: Int? = null,
    val avatarUrl: String? = null,
    val backgroundUrl: String? = null,
    val email: String,
    val phone: String? = null,
    val password: String? = null,
    val headline: String? = null,
    val uid: String? = null
)

data class Role(
    val id: Int,
    val name: RoleType
)

data class Follower(
    val id: Int,
    val status: FriendStatus = FriendStatus.PENDING,
    val senderId: String,
    val receiverId: String
)

data class Message(
    val id: Int,
    val content: String? = null,
    val videoUrl: String? = null,
    val imageUrl: String? = null,
    val voiceUrl: String? = null,
    val senderId: String,
    val receiverId: String,
    val isRead: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now()
)

data class Notification(
    val id: Long,
    val title: String,
    val body: String,
    val timestamp: Long,
    val appointmentId: Int?,
    val userRole: String
)

data class UserNotification(
    val id: Int,
    val receiverId: String,
    val notificationId: Int,
    val isRead: Boolean = false,
    val content: String
)

data class Appointment(
    val id: Int,
    val status: AppointmentStatus = AppointmentStatus.PENDING,
    val patientId: String,
    val doctorId: String,
    val appointmentDate: LocalDateTime,
    val note: String? = null,
    val rating: Int? = null,
    val review: String? = null
) {
    companion object {
        fun fromApiResponse(
            id: Int,
            status: String,
            appointmentDate: String,
            appointmentTime: String?,
            patientId: String,
            doctorId: String,
            note: String?,
            rating: Double?,
            review: String?
        ): Appointment {
            // Parse appointmentDate (chuỗi "YYYY-MM-DD")
            val date = LocalDate.parse(appointmentDate, DateTimeFormatter.ISO_LOCAL_DATE)
            // Parse appointmentTime (chuỗi "HH:mm:ss" hoặc null)
            val time = if (appointmentTime != null) {
                LocalTime.parse(appointmentTime, DateTimeFormatter.ISO_LOCAL_TIME)
            } else {
                LocalTime.of(0, 0)
            }
            val dateTime = date.atTime(time)
            return Appointment(
                id = id,
                status = AppointmentStatus.valueOf(status.uppercase()),
                patientId = patientId,
                doctorId = doctorId,
                appointmentDate = dateTime,
                note = note,
                rating = rating?.toInt(),
                review = review
            )
        }
    }
}

data class Diary(
    val id: Int,
    val emotion: Emotions,
    val title: String = "No title",
    val content: String? = null,
    val imageUrl: String? = null,
    val voiceUrl: String? = null,
    val posterId: String,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val emotions: List<SerializableEmotion>? = null,
    val negativityScore: Float? = null
)

data class Topic(
    val id: Int,
    val name: String,
    val content: String,
    val avatarUrl: String? = null
)

data class Collection(
    val id: Int,
    val name: String,
    val resourceUrl: String,
    val topicId: Int,
    val type: CollectionType = CollectionType.MUSIC
)

data class CollectionSeen(
    val id: Int,
    val collectionId: Int,
    val userId: String
)

data class Community(
    val id: Int,
    val name: String,
    val content: String,
    val adminId: String,
    val imageUrl: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
)

data class ParticipantCommunity(
    val userId: String,
    val communityId: Int
)

data class Post(
    val id: Int,
    val content: String,
    val posterId: String,
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
    val userId: String,
    val postId: Int,
    val reactCount: Int = 0,
    val createdAt: LocalDateTime = LocalDateTime.now()
)

data class DoctorExpertise(
    val id: Int,
    val title: String,
    val content: String,
    val doctorId: String
)

data class TimeSlot(
    val time: LocalDateTime,
    val isBooked: Boolean
)

data class SearchItem<T>(
    val id: T,
    val name: String
)
data class PagedResponse<T>(
    @SerializedName("content")
    val content: List<T>,
    @SerializedName("totalElements")
    val totalElements: Long,
    @SerializedName("totalPages")
    val totalPages: Int,
    @SerializedName("size")
    val size: Int,
    @SerializedName("number")
    val number: Int
)
data class ProfileSelection(
    val title: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)


@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val body: String,
    val timestamp: Long,
    val appointmentId: Int?,
    val userRole: String
)
