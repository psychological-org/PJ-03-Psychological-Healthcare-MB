package com.example.beaceful.core.network.user

data class UserRequest(
    val id: String?,
    val username: String? = null,
    val password: String? = null,
    val email: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val role: String? = null,
    val biography: String? = null,
    val yearOfBirth: String? = null,
    val yearOfExperience: String? = null,
    val avatarUrl: String? = null,
    val backgroundUrl: String? = null,
    val phone: String? = null,
    val content: String? = null
)