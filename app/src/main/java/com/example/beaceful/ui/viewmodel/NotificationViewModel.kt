package com.example.beaceful.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beaceful.core.util.NotificationEventBus
import com.example.beaceful.core.util.UserSession
import com.example.beaceful.domain.model.UserNotification
import com.example.beaceful.domain.repository.UserNotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val repository: UserNotificationRepository
) : ViewModel() {
    private val _notifications = MutableStateFlow<List<UserNotification>>(emptyList())
    val notifications: StateFlow<List<UserNotification>> = _notifications

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _currentPage = MutableStateFlow(0)
    private val _totalPages = MutableStateFlow(1)
    private val _limit = 10

    private val TAG = "NotificationViewModel"

    init {
        fetchNotifications()
        // Thu thập sự kiện thông báo đẩy
        viewModelScope.launch {
            NotificationEventBus.notificationEvents.collect { notification ->
                addPushNotification(notification)
            }
        }
    }

    fun fetchNotifications() {
        val userId = try {
            UserSession.getCurrentUserId() ?: run {
                _error.value = "Người dùng chưa đăng nhập"
                Log.e(TAG, "User not logged in")
                return
            }
        } catch (e: IllegalStateException) {
            _error.value = "Người dùng chưa đăng nhập"
            Log.e(TAG, "User not logged in", e)
            return
        }

        viewModelScope.launch {
            if (_currentPage.value >= _totalPages.value || _isLoading.value) return@launch
            try {
                _isLoading.value = true
                val response = repository.getUserNotifications(userId, _currentPage.value, _limit)
                _notifications.update { current ->
                    (current + response.content).distinctBy { it.id }
                }
                _totalPages.value = response.totalPages
                _currentPage.value += 1
                Log.d(TAG, "Fetched notifications for user $userId: ${response.content.size}")
            } catch (e: Exception) {
                _error.value = "Lỗi khi tải thông báo: ${e.message}"
                Log.e(TAG, "Error loading notifications: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addPushNotification(notification: UserNotification) {
        _notifications.update { current ->
            val exists = current.any { it.id == notification.id }
            if (!exists) {
                listOf(notification) + current
            } else {
                current
            }
        }
        Log.d(TAG, "Added push notification: ${notification.content}, id=${notification.id}")
    }

    fun clearError() {
        _error.value = null
    }
}