package com.example.beaceful.core.network.post

data class LikePostRequest(
    val id: Int? = null,
    val postId: Int,
    val userId: String
)