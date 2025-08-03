package com.example.beaceful.core.network.user

import com.example.beaceful.domain.model.PagedResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

data class UserCreateResponse(
    val id: String
)

interface UserApiService {

    @POST("users")
    suspend fun createUser(
        @Header("Authorization") token: String,
        @Body request: UserRequest
    ): Response<UserCreateResponse>

    @GET("users/{user-id}")
    suspend fun getUserById(@Path("user-id") userId: String): UserResponse

    @GET("users")
    suspend fun getUsers(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): PagedResponse<UserResponse>

    @GET("users/{keycloakId}")
    suspend fun getUserByKeycloakId(@Path("keycloakId") keycloakId: String): UserResponse

    @PUT("users")
    suspend fun updateUser(@Body request: UserRequest): Unit

    @DELETE("users/{user-id}")
    suspend fun deleteUser(@Path("user-id") userId: String)
}