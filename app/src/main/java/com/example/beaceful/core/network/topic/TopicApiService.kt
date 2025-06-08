package com.example.beaceful.core.network.topic

import com.example.beaceful.domain.model.PagedResponse
import com.example.beaceful.domain.model.Topic
import retrofit2.Response
import retrofit2.http.*

interface TopicApiService {
    @POST("topics")
    suspend fun createTopic(@Body request: TopicRequest): Response<Int>

    @PUT("topics")
    suspend fun updateTopic(@Body request: TopicRequest): Response<Unit>

    @GET("topics")
    suspend fun getAllTopics(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Response<PagedResponse<Topic>>

    @GET("topics/exists/{topic-id}")
    suspend fun existsById(@Path("topic-id") topicId: Int): Response<Boolean>

    @GET("topics/{topic-id}")
    suspend fun getTopicById(@Path("topic-id") topicId: Int): Response<Topic>

    @DELETE("topics/{topic-id}")
    suspend fun deleteTopic(@Path("topic-id") topicId: Int): Response<Unit>
}