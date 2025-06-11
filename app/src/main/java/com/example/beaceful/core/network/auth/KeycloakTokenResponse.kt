package com.example.beaceful.core.network.auth

data class KeycloakTokenResponse(
    val access_token: String,
    val refresh_token: String? = null,
    val expires_in: Int,
    val token_type: String
)