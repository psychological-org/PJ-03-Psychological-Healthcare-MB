package com.example.beaceful.domain.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.beaceful.domain.model.NotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
    @Insert
    suspend fun insert(notification: NotificationEntity)

    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Query("SELECT * FROM notifications WHERE userRole = :role ORDER BY timestamp DESC")
    fun getNotificationsByRole(role: String): Flow<List<NotificationEntity>>
}