package com.example.beaceful.core.network.post

data class PostRequest(
    val id: Int? = null,
    val content: String,
    val imageUrl: String? = null,
    val visibility: String = "PUBLIC",
    val reactCount: Int? = 0,
    val communityId: Int?,
    val userId: String
)