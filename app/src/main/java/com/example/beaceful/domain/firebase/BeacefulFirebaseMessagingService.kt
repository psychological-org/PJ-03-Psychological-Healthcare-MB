package com.example.beaceful.domain.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.beaceful.R
import com.example.beaceful.core.network.fcm_token.FcmTokenApiService
import com.example.beaceful.core.network.fcm_token.FcmTokenRequest
import com.example.beaceful.core.util.NotificationEventBus
import com.example.beaceful.core.util.UserSession
import com.example.beaceful.domain.local.NotificationDao
import com.example.beaceful.domain.model.NotificationEntity
import com.example.beaceful.domain.model.UserNotification
import com.example.beaceful.ui.MainActivity
import com.example.beaceful.ui.viewmodel.NotificationViewModel
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@AndroidEntryPoint
class BeacefulFirebaseMessagingService : FirebaseMessagingService() {

    @Inject lateinit var fcmTokenApiService: FcmTokenApiService

    private val TAG = "BeacefulFCM"
    private val CHANNEL_ID = "beaceful_notifications"
    private val CHANNEL_NAME = "Beaceful Notifications"

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Beaceful Notifications"
                enableLights(true)
                enableVibration(true)
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            Log.d(TAG, "Created notification channel: $CHANNEL_ID")
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "Received FCM message: ${remoteMessage.messageId}")
        Log.d(TAG, "From: ${remoteMessage.from}")
        Log.d(TAG, "Data: ${remoteMessage.data}")
        Log.d(TAG, "Notification: title=${remoteMessage.notification?.title}, body=${remoteMessage.notification?.body}")

        val currentUserRole = UserSession.getCurrentUserRole()
        val notificationRole = remoteMessage.data["role"]
        Log.d(TAG, "Current user role: $currentUserRole, Notification role: $notificationRole")

        // Lọc thông báo: chỉ xử lý nếu role khớp hoặc không có role
        if (notificationRole == null || notificationRole == currentUserRole) {
            val title = remoteMessage.notification?.title ?: remoteMessage.data["title"] ?: "Thông báo mới"
            val body = remoteMessage.notification?.body ?: remoteMessage.data["body"] ?: "Bạn có thông báo mới"
            val appointmentId = remoteMessage.data["appointment_id"]?.toIntOrNull()
            val userNotificationId = remoteMessage.data["userNotificationId"] ?: ""

            Log.d(TAG, "Parsed notification: title=$title, body=$body, appointmentId=$appointmentId, userRole=$currentUserRole, userNotificationId=$userNotificationId")

            // Tạo UserNotification
            val notification = UserNotification(
                id = userNotificationId,
                userId = UserSession.getCurrentUserId() ?: "",
                notificationId = "",
                content = body,
                isRead = false,
            )

            // Phát sự kiện qua NotificationEventBus
            CoroutineScope(Dispatchers.IO).launch {
                NotificationEventBus.emitNotification(notification)
                Log.d(TAG, "Emitted notification event: $body")
            }

            // Hiển thị thông báo
            sendNotification(title, body, appointmentId)
        } else {
            Log.d(TAG, "Ignoring notification: role $notificationRole does not match current user role $currentUserRole")
        }
    }

    private fun sendNotification(title: String, messageBody: String, appointmentId: Int?) {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("navigate_to", "appointment_details")
            putExtra("appointment_id", appointmentId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            System.currentTimeMillis().toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.chat_bg) // Thay bằng icon của bạn
            .setContentTitle(title)
            .setContentText(messageBody)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = System.currentTimeMillis().toInt()
        notificationManager.notify(notificationId, notificationBuilder.build())
        Log.d(TAG, "Notification sent with ID: $notificationId, title: $title, body: $messageBody")
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New FCM token: $token")
        sendFcmTokenToServer(token)
    }

    private fun sendFcmTokenToServer(token: String) {
        try {
            val userId = UserSession.getCurrentUserId()
            val deviceId = android.provider.Settings.Secure.getString(
                contentResolver,
                android.provider.Settings.Secure.ANDROID_ID
            )
            Log.d(TAG, "Saving FCM token for userId: $userId, deviceId: $deviceId")
            val request = FcmTokenRequest(userId, token, deviceId, "ANDROID")

            fcmTokenApiService.saveFcmToken(request).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        val tokenId = response.body()?.string() ?: "Unknown"
                        Log.d(TAG, "FCM token saved successfully: tokenId=$tokenId, token=$token")
                    } else {
                        Log.e(TAG, "Failed to save FCM token: code=${response.code()}, message=${response.message()}")
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e(TAG, "Failed to save FCM token: ${t.message}", t)
                }
            })
        } catch (e: IllegalStateException) {
            Log.e(TAG, "User not logged in, cannot save token: ${e.message}", e)
        }
    }
}