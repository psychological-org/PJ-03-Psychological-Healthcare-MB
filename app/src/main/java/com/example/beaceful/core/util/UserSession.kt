package com.example.beaceful.core.util

import androidx.compose.runtime.Composable

object UserSession {
    private const val TEMP_USER_ID = "68401da39148fa4dbbb6d25a"

    fun getCurrentUserId(): String {
        // Hiện tại trả về userId cố định
        return TEMP_USER_ID

        // Sau này, khi có AuthViewModel:
        /*
        val authViewModel: AuthViewModel = hiltViewModel()
        val userId by authViewModel.currentUser.collectAsState()
        return userId ?: TEMP_USER_ID // Hoặc xử lý trường hợp chưa đăng nhập
        */
    }
}