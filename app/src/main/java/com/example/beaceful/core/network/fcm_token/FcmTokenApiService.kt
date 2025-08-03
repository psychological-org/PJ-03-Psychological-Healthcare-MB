package com.example.beaceful.core.network.fcm_token

import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Call

interface FcmTokenApiService {
    @POST("users/fcm-token")
    fun saveFcmToken(@Body request: FcmTokenRequest): Call<ResponseBody>
}

data class FcmTokenRequest(
    val userId: String,
    val fcmToken: String,
    val deviceId: String,
    val deviceType: String
)