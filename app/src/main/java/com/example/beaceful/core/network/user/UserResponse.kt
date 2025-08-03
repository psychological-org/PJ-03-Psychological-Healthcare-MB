package com.example.beaceful.core.network.user

import com.example.beaceful.domain.model.User
import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("id") val id: String,
    @SerializedName("username") val username: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("fullName") val fullName: String?,
    @SerializedName("role") val role: String?,
    @SerializedName("biography") val biography: String?,
    @SerializedName("yearOfBirth") val yearOfBirth: String?,
    @SerializedName("yearOfExperience") val yearOfExperience: String?,
    @SerializedName("avatarUrl") val avatarUrl: String?,
    @SerializedName("backgroundUrl") val backgroundUrl: String?,
    @SerializedName("phone") val phone: String?,
    @SerializedName("content") val content: String?
) {
    fun toUser(): User {
        return User(
            id = id ?: "",
            fullName = fullName ?: "Unknown",
            roleId = when (role?.lowercase()) {
                "admin" -> 1
                "doctor" -> 2
                "patient" -> 3
                else -> 3
            },
            biography = biography,
            yearOfBirth = yearOfBirth?.toIntOrNull(),
            yearOfExperience = yearOfExperience?.toIntOrNull(),
            avatarUrl = avatarUrl,
            backgroundUrl = backgroundUrl,
            email = email ?: "",
            phone = phone,
            password = "",
            headline = content,
            uid = null
        )
    }
}