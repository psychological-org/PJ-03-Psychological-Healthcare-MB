package com.example.beaceful.domain.repository

import android.util.Log
import com.auth0.jwt.JWT
import com.example.beaceful.core.network.auth.AuthApiService
import com.example.beaceful.core.network.auth.AuthResponse
import com.example.beaceful.domain.model.User
import javax.inject.Inject

data class LoginResponse(
    val token: String,
    val refreshToken: String? = null,
    val user: User
)

class AuthRepository @Inject constructor(
    private val authApiService: AuthApiService
) {
    private val TAG = "AuthRepository"

    suspend fun login(clientId: String, username: String, password: String, clientSecret: String): LoginResponse {
        try {
            val response = authApiService.login(
                clientId = clientId,
                username = username,
                password = password,
                clientSecret = clientSecret
            )
            Log.d(TAG, "Login response: $response")

            // Giải mã access_token
            val decodedJWT = JWT.decode(response.accessToken)
            val keycloakId = decodedJWT.getClaim("sub").asString()
                ?: throw IllegalStateException("No user ID found in access token")
            val fullName = decodedJWT.getClaim("name").asString() ?: ""
            val email = decodedJWT.getClaim("email").asString() ?: ""

            Log.d(TAG, "Decoded JWT: keycloakId=$keycloakId, fullName=$fullName, email=$email")

            return LoginResponse(
                token = response.accessToken,
                refreshToken = response.refreshToken,
                user = User(
                    id = keycloakId,
                    fullName = fullName,
                    email = email
                )
            )
        } catch (e: Exception) {
            Log.e(TAG, "Login failed: ${e.message}", e)
            throw e
        }
    }

    suspend fun refreshToken(clientId: String, refreshToken: String, clientSecret: String): LoginResponse {
        try {
            Log.d(TAG, "Attempting to refresh with token: $refreshToken")
            val response = authApiService.refreshToken(
                clientId = clientId,
                clientSecret = clientSecret,
                refreshToken = refreshToken
            )
            Log.d(TAG, "Refresh token response: $response")

            val decodedJWT = JWT.decode(response.accessToken)
            val keycloakId = decodedJWT.getClaim("sub").asString()
                ?: throw IllegalStateException("No user ID found in access token")
            val fullName = decodedJWT.getClaim("name").asString() ?: ""
            val email = decodedJWT.getClaim("email").asString() ?: ""

            Log.d(TAG, "Decoded JWT: keycloakId=$keycloakId, fullName=$fullName, email=$email")

            return LoginResponse(
                token = response.accessToken,
                refreshToken = response.refreshToken,
                user = User(
                    id = keycloakId,
                    fullName = fullName,
                    email = email
                )
            )
        } catch (e: Exception) {
            Log.e(TAG, "Refresh token failed: ${e.message}", e)
            if (e is retrofit2.HttpException && e.code() == 400) {
                throw IllegalStateException("Refresh token is invalid. Please login again.")
            }
            throw e
        }
    }
}