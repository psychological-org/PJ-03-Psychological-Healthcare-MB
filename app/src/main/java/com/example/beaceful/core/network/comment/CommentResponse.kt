package com.example.beaceful.core.network.comment

import com.example.beaceful.domain.model.Comment
import java.time.LocalDateTime
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
        return Comment(
            id = id,
            content = content,
            imageUrl = imageUrl,
            userId = userId,
            postId = postId,
            reactCount = reactCount,
            createdAt = LocalDateTime.parse(createdAt, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        )
    }
}