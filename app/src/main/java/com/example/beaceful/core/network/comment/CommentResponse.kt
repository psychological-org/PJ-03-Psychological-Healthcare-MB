package com.example.beaceful.core.network.comment

import com.example.beaceful.domain.model.Comment
import java.time.LocalDateTime

data class CommentResponse(
    val id: Int,
    val content: String,
    val imageUrl: String?,
    val userId: String,
    val postId: Int,
    val reactCount: Int
) {
    fun toComment(): Comment {
        return Comment(
            id = id,
            content = content,
            imageUrl = imageUrl,
            userId = userId,
            postId = postId,
            reactCount = reactCount,
            createdAt = LocalDateTime.now() // Backend không trả createdAt, dùng tạm
        )
    }
}