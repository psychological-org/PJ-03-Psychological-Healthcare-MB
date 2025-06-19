package com.example.beaceful.core.util

import com.example.beaceful.domain.model.UserNotification
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object NotificationEventBus {
    private val _notificationEvents = MutableSharedFlow<UserNotification>(replay = 0)
    val notificationEvents = _notificationEvents.asSharedFlow()

    suspend fun emitNotification(notification: UserNotification) {
        _notificationEvents.emit(notification)
    }
}