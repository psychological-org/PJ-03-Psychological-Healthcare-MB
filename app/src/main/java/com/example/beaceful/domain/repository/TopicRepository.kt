package com.example.beaceful.domain.repository

import com.example.beaceful.core.network.topic.TopicApiService
import com.example.beaceful.core.network.topic.TopicRequest
import com.example.beaceful.domain.model.PagedResponse
import com.example.beaceful.domain.model.Topic
import javax.inject.Inject

class TopicRepository @Inject constructor(
    private val topicApiService: TopicApiService
) {
    suspend fun createTopic(request: TopicRequest): Result<Int> {
        return try {
            val response = topicApiService.createTopic(request)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("No data returned"))
            } else {
                Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateTopic(request: TopicRequest): Result<Unit> {
        return try {
            val response = topicApiService.updateTopic(request)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllTopics(page: Int, limit: Int): Result<PagedResponse<Topic>> {
        return try {
            val response = topicApiService.getAllTopics(page, limit)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("No data returned"))
            } else {
                Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun existsById(topicId: Int): Result<Boolean> {
        return try {
            val response = topicApiService.existsById(topicId)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("No data returned"))
            } else {
                Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTopicById(topicId: Int): Result<Topic> {
        return try {
            val response = topicApiService.getTopicById(topicId)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("No data returned"))
            } else {
                Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteTopic(topicId: Int): Result<Unit> {
        return try {
            val response = topicApiService.deleteTopic(topicId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}