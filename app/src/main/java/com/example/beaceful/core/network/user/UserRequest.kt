package com.example.beaceful.core.network.user

data class UserRequest(
    val keycloakId: String,
    val fullName: String?,
    val biography: String?,
    val headline: String?,
    val yearOfBirth: String?,
    val avatarUrl: String?,
    val backgroundUrl: String?
)