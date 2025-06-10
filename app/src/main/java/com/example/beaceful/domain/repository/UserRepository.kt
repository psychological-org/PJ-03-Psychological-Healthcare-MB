package com.example.beaceful.domain.repository

import android.util.Log
import com.example.beaceful.core.network.user.UserApiService
import com.example.beaceful.domain.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userApiService: UserApiService
) {
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
            email = response.email ?: ""
        )
    }
}