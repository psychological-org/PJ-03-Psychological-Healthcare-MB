package com.example.beaceful.core.network.notification

import com.example.beaceful.domain.model.PagedResponse
import com.example.beaceful.domain.model.UserNotification
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface UserNotificationApiService {
    @GET("notifications/user-notification/user/{user-id}")
    suspend fun getUserNotifications(
        @Path("user-id") userId: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): PagedResponse<UserNotification>
}