package com.example.beaceful.domain.repository

import com.example.beaceful.domain.local.NotificationDao
import com.example.beaceful.domain.model.Notification;
import kotlinx.coroutines.flow.Flow;
import kotlinx.coroutines.flow.map;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
class NotificationRepository @Inject constructor(
    private val notificationDao: NotificationDao
) {
    fun getNotificationsByRole(role: String): Flow<List<Notification>> {
        return notificationDao.getNotificationsByRole(role).map { entities ->
            entities.map {
                Notification(
                    id = it.id,
                    title = it.title,
                    body = it.body,
                    timestamp = it.timestamp,
                    appointmentId = it.appointmentId,
                    userRole = it.userRole
                )
            }
        }
    }
}