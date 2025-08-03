package com.example.beaceful.core.network.collection

import com.example.beaceful.domain.model.CollectionSeen
import com.example.beaceful.domain.model.PagedResponse
import retrofit2.Response
import retrofit2.http.*

interface CollectionSeenApiService {
    @POST("collections/collection-seen")
    suspend fun createCollectionSeen(@Body request: CollectionSeenRequest): Response<Int>

    @GET("collections/collection-seen/users/{userId}")
    suspend fun getCollectionSeenByUserId(
        @Path("userId") userId: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Response<PagedResponse<CollectionSeen>>

    @GET("collections/collection-seen/{id}")
    suspend fun getCollectionSeenById(@Path("id") id: Int): Response<CollectionSeen>

    @GET("collections/collection-seen")
    suspend fun getAllCollectionSeen(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Response<PagedResponse<CollectionSeen>>

    @DELETE("collections/collection-seen/{id}")
    suspend fun deleteCollectionSeen(@Path("id") id: Int): Response<Unit>

    @PUT("collections/collection-seen")
    suspend fun updateCollectionSeen(@Body request: CollectionSeenRequest): Response<Unit>

    @GET("collections/collection-seen/exists/{id}")
    suspend fun existsById(@Path("id") id: Int): Response<Boolean>
}