package com.example.beaceful.core.util

import androidx.compose.runtime.Composable
import com.example.beaceful.ui.viewmodel.AuthViewModel

object UserSession {
//    private const val TEMP_USER_ID = "68401da39148fa4dbbb6d25a"
//
//    fun getCurrentUserId(): String {
//        // Hiện tại trả về userId cố định
//        return TEMP_USER_ID
//
//        // Sau này, khi có AuthViewModel:
//        /*
//        val authViewModel: AuthViewModel = hiltViewModel()
//        val userId by authViewModel.currentUser.collectAsState()
//        return userId ?: TEMP_USER_ID // Hoặc xử lý trường hợp chưa đăng nhập
//        */
//    }
    private var currentUserId: String? = null

    fun setCurrentUserId(userId: String) {
        currentUserId = userId
    }

    fun getCurrentUserId(): String {
        return currentUserId
            ?: throw IllegalStateException("User not logged in")
    }

    fun clearUserId() {
        currentUserId = null
    }
}