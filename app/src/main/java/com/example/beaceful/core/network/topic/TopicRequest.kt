package com.example.beaceful.core.network.topic

import com.google.gson.annotations.SerializedName

data class TopicRequest(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("name")
    val name: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("avatarUrl")
    val avatarUrl: String? = null
)