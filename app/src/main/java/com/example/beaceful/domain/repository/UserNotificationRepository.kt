package com.example.beaceful.domain.repository

import com.example.beaceful.core.network.notification.UserNotificationApiService
import com.example.beaceful.domain.model.PagedResponse
import com.example.beaceful.domain.model.UserNotification
import javax.inject.Inject

interface UserNotificationRepository {
    suspend fun getUserNotifications(userId: String, page: Int, limit: Int): PagedResponse<UserNotification>
}

class UserNotificationRepositoryImpl @Inject constructor(
    private val apiService: UserNotificationApiService
) : UserNotificationRepository {
    override suspend fun getUserNotifications(userId: String, page: Int, limit: Int): PagedResponse<UserNotification> {
        return apiService.getUserNotifications(userId, page, limit)
    }
}