package com.example.beaceful.core.network.community

import com.google.gson.annotations.SerializedName

data class CommunityResponse(
    val id: Int,
    val name: String,
    val content: String,
    @SerializedName("avatarUrl") val avatarUrl: String?,
    @SerializedName("adminId") val adminId: String
)