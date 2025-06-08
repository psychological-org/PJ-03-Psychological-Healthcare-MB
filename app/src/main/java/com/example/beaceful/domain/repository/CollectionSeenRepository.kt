package com.example.beaceful.domain.repository


import com.example.beaceful.core.network.collection.CollectionSeenApiService
import com.example.beaceful.core.network.collection.CollectionSeenRequest
import com.example.beaceful.domain.model.CollectionSeen
import com.example.beaceful.domain.model.PagedResponse
import javax.inject.Inject

class CollectionSeenRepository @Inject constructor(
    private val collectionSeenApiService: CollectionSeenApiService
) {
    suspend fun createCollectionSeen(request: CollectionSeenRequest): Result<Int> {
        return try {
            val response = collectionSeenApiService.createCollectionSeen(request)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("No data returned"))
            } else {
                Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCollectionSeenByUserId(userId: String, page: Int, limit: Int): Result<PagedResponse<CollectionSeen>> {
        return try {
            val response = collectionSeenApiService.getCollectionSeenByUserId(userId, page, limit)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("No data returned"))
            } else {
                Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCollectionSeenById(id: Int): Result<CollectionSeen> {
        return try {
            val response = collectionSeenApiService.getCollectionSeenById(id)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("No data returned"))
            } else {
                Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllCollectionSeen(page: Int, limit: Int): Result<PagedResponse<CollectionSeen>> {
        return try {
            val response = collectionSeenApiService.getAllCollectionSeen(page, limit)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("No data returned"))
            } else {
                Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteCollectionSeen(id: Int): Result<Unit> {
        return try {
            val response = collectionSeenApiService.deleteCollectionSeen(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateCollectionSeen(request: CollectionSeenRequest): Result<Unit> {
        return try {
            val response = collectionSeenApiService.updateCollectionSeen(request)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun existsById(id: Int): Result<Boolean> {
        return try {
            val response = collectionSeenApiService.existsById(id)
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