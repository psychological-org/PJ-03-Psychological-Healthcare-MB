package com.example.beaceful.domain.repository


import com.example.beaceful.core.network.collection.CollectionApiService
import com.example.beaceful.core.network.collection.CollectionRequest
import com.example.beaceful.domain.model.Collection
import com.example.beaceful.domain.model.PagedResponse
import javax.inject.Inject

class CollectionRepository @Inject constructor(
    private val collectionApiService: CollectionApiService
) {
    suspend fun createCollection(request: CollectionRequest): Result<Int> {
        return try {
            val response = collectionApiService.createCollection(request)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("No data returned"))
            } else {
                Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateCollection(request: CollectionRequest): Result<Unit> {
        return try {
            val response = collectionApiService.updateCollection(request)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllCollections(page: Int, limit: Int): Result<PagedResponse<Collection>> {
        return try {
            val response = collectionApiService.getAllCollections(page, limit)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("No data returned"))
            } else {
                Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun existsById(collectionId: Int): Result<Boolean> {
        return try {
            val response = collectionApiService.existsById(collectionId)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("No data returned"))
            } else {
                Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCollectionById(collectionId: Int): Result<Collection> {
        return try {
            val response = collectionApiService.getCollectionById(collectionId)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("No data returned"))
            } else {
                Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteCollection(collectionId: Int): Result<Unit> {
        return try {
            val response = collectionApiService.deleteCollection(collectionId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCollectionsByTopicId(topicId: Int): Result<List<Collection>> {
        return try {
            val response = collectionApiService.getCollectionsByTopicId(topicId)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("No data returned"))
            } else {
                Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}