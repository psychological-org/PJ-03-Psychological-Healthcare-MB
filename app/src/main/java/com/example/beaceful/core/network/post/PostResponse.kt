package com.example.beaceful.core.network.post

data class PostResponse(
    val id: Int,
    val content: String,
    val imageUrl: String?,
    val visibility: String,
    val reactCount: Int,
    val communityId: Int?,
    val userId: String
)