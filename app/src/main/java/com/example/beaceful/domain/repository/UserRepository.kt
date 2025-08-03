package com.example.beaceful.domain.repository

import android.util.Log
import com.example.beaceful.BuildConfig
import com.example.beaceful.core.network.auth.AuthApiService
import com.example.beaceful.core.network.user.UserApiService
import com.example.beaceful.core.network.user.UserRequest
import com.example.beaceful.domain.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userApiService: UserApiService,
    private val authApiService: AuthApiService
) {
    suspend fun createUser(request: UserRequest): String = withContext(Dispatchers.IO) {
        try {
            val tokenResponse = authApiService.getClientToken(
                clientId = "authservice",
                clientSecret = BuildConfig.CLIENT_SECRET,
                grantType = "client_credentials"
            )
            val authHeader = "Bearer ${tokenResponse.access_token}"
            Log.d("UserRepository", "Obtained token: ${tokenResponse.access_token}")

            val response = userApiService.createUser(authHeader, request)
            response.body()?.id ?: throw IllegalStateException("Response body is null")
        } catch (e: Exception) {
            Log.e("UserRepository", "Error creating user: ${e.message}", e)
            throw e
        }
    }

    suspend fun getUserById(userId: String): User? = withContext(Dispatchers.IO) {
        try {
            val response = userApiService.getUserById(userId)
            response.toUser()
        } catch (e: Exception) {
            Log.e("UserRepository", "Error fetching user $userId: ${e.message}", e)
            null
        }
    }

    suspend fun getAllUsers(): List<User> = withContext(Dispatchers.IO) {
        try {
            val response = userApiService.getUsers(page = 0, size = 100)
            response.content.map { it.toUser() }
        } catch (e: Exception) {
            Log.e("UserRepository", "Error fetching all users: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun getUserByKeycloakId(keycloakId: String): User {
        val response = userApiService.getUserByKeycloakId(keycloakId)
        return User(
            id = response.id, // mongoId
            fullName = response.fullName ?: "",
            email = response.email ?: "",
            roleId = when (response.role?.lowercase()) {
                "admin" -> 1
                "doctor" -> 2
                "patient" -> 3
                else -> 3
            },
            biography = response.biography,
            yearOfBirth = response.yearOfBirth?.toIntOrNull(),
            yearOfExperience = response.yearOfExperience?.toIntOrNull(),
            avatarUrl = response.avatarUrl,
            backgroundUrl = response.backgroundUrl,
            phone = response.phone,
            headline = response.content,
        )
    }

    suspend fun updateUser(request: UserRequest) = withContext(Dispatchers.IO) {
        try {
            userApiService.updateUser(request)
        } catch (e: Exception) {
            Log.e("UserRepository", "Error updating user: ${e.message}", e)
            throw e // Throw để ViewModel xử lý lỗi
        }
    }

    suspend fun deleteUser(userId: String) = withContext(Dispatchers.IO) {
        try {
            userApiService.deleteUser(userId)
        } catch (e: Exception) {
            Log.e("UserRepository", "Error deleting user $userId: ${e.message}", e)
            throw e // Throw để ViewModel xử lý lỗi
        }
    }
}