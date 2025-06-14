package com.example.beaceful.core.util

import androidx.compose.runtime.Composable
import com.example.beaceful.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object UserSession {
//    private const val TEMP_USER_ID = "68401da39148fa4dbbb6d25a"
private var currentUserId: String? = null
    private val _currentUserRole = MutableStateFlow<String?>(null)
    val currentUserRole = _currentUserRole.asStateFlow()

    fun setCurrentUserId(userId: String) {
        currentUserId = userId
    }

    fun setCurrentUserRole(role: String) {
        _currentUserRole.value = role
    }

    fun getCurrentUserId(): String {
        return currentUserId
            ?: throw IllegalStateException("User not logged in")
    }

    fun getCurrentUserRole(): String {
        return _currentUserRole.value
            ?: throw IllegalStateException("User role not set")
    }

    fun clearUserId() {
        currentUserId = null
        _currentUserRole.value = null
    }
}