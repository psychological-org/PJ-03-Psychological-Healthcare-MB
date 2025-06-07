package com.example.beaceful.core.network.collection

import com.example.beaceful.domain.model.Collection
import com.example.beaceful.domain.model.PagedResponse
import retrofit2.Response
import retrofit2.http.*

interface CollectionApiService {
    @POST("collections")
    suspend fun createCollection(@Body request: CollectionRequest): Response<Int>

    @PUT("collections")
    suspend fun updateCollection(@Body request: CollectionRequest): Response<Unit>

    @GET("collections")
    suspend fun getAllCollections(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Response<PagedResponse<Collection>>

    @GET("collections/exists/{collection-id}")
    suspend fun existsById(@Path("collection-id") collectionId: Int): Response<Boolean>

    @GET("collections/{collection-id}")
    suspend fun getCollectionById(@Path("collection-id") collectionId: Int): Response<Collection>

    @DELETE("collections/{collection-id}")
    suspend fun deleteCollection(@Path("collection-id") collectionId: Int): Response<Unit>

    @GET("collections/topic/{topic-id}")
    suspend fun getCollectionsByTopicId(@Path("topic-id") topicId: Int): Response<List<Collection>>
}