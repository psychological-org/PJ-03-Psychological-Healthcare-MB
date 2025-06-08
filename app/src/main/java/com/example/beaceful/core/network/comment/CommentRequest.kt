package com.example.beaceful.core.network.comment

data class CommentRequest(
    val id: Int? = null,
    val content: String,
    val imageUrl: String? = null,
    val userId: String,
    val postId: Int,
    val reactCount: Int? = 0
)