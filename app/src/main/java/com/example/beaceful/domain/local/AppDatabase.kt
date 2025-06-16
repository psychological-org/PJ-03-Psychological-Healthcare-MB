package com.example.beaceful.domain.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.beaceful.domain.model.NotificationEntity

@Database(entities = [NotificationEntity::class], version = 2, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun notificationDao(): NotificationDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Thêm cột userRole vào bảng notifications, mặc định là "unknown"
                database.execSQL("ALTER TABLE notifications ADD COLUMN userRole TEXT NOT NULL DEFAULT 'unknown'")
            }
        }
    }
}