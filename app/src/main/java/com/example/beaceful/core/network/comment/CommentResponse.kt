package com.example.beaceful.core.network.comment

import com.example.beaceful.domain.model.Comment
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class CommentResponse(
    val id: Int,
    val content: String,
    val imageUrl: String?,
    val userId: String,
    val postId: Int,
    val reactCount: Int,
    val createdAt: String
) {
    fun toComment(): Comment {
        val parsedDateTime = try {
            OffsetDateTime.parse(createdAt, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                .atZoneSameInstant(ZoneId.of("Asia/Ho_Chi_Minh"))
                .toLocalDateTime()
        } catch (e: Exception) {
            // Fallback cho định dạng không có Z (2025-06-06T08:34:33.230751)
            LocalDateTime.parse(createdAt, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                .atZone(ZoneId.of("UTC"))
                .withZoneSameInstant(ZoneId.of("Asia/Ho_Chi_Minh"))
                .toLocalDateTime()
        }
        return Comment(
            id = id,
            content = content,
            imageUrl = imageUrl,
            userId = userId,
            postId = postId,
            reactCount = reactCount,
            createdAt = parsedDateTime
        )
    }
}