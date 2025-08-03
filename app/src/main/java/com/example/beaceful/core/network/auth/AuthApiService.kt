package com.example.beaceful.core.network.auth

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AuthApiService {
    @FormUrlEncoded
    @POST("realms/micro-services/protocol/openid-connect/token")
    suspend fun login(
        @Field("client_id") clientId: String,
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("client_secret") clientSecret: String,
        @Field("grant_type") grantType: String = "password"
    ): AuthResponse

    @FormUrlEncoded
    @POST("realms/micro-services/protocol/openid-connect/token")
    suspend fun refreshToken(
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("refresh_token") refreshToken: String,
        @Field("grant_type") grantType: String = "refresh_token"
    ): AuthResponse

    @FormUrlEncoded
    @POST("realms/micro-services/protocol/openid-connect/token")
    suspend fun getClientToken(
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("grant_type") grantType: String = "client_credentials"
    ): KeycloakTokenResponse
}
