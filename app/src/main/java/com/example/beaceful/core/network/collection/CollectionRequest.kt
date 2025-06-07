package com.example.beaceful.core.network.collection

import com.google.gson.annotations.SerializedName

data class CollectionRequest(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("name")
    val name: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("resourceUrl")
    val resourceUrl: String? = null,
    @SerializedName("topicId")
    val topicId: Int
)