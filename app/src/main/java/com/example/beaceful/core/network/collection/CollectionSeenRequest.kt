package com.example.beaceful.core.network.collection

import com.google.gson.annotations.SerializedName

data class CollectionSeenRequest(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("userId")
    val userId: String,
    @SerializedName("collectionId")
    val collectionId: Int
)