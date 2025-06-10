package com.example.beaceful.core.network.user

import com.example.beaceful.domain.model.PagedResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface UserApiService {
    @GET("users/{user-id}")
    suspend fun getUserById(@Path("user-id") userId: String): UserResponse

    @GET("users")
    suspend fun getUsers(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): PagedResponse<UserResponse>

    @GET("users/{keycloakId}")
    suspend fun getUserByKeycloakId(@Path("keycloakId") keycloakId: String): UserResponse
}