package com.example.beaceful.core.network.comment

import com.example.beaceful.domain.model.PagedResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface CommentApiService {
    @POST("comments")
    suspend fun createComment(
        @Body request: CommentRequest
    ): Int

    @PUT("comments")
    suspend fun updateComment(
        @Body request: CommentRequest
    ): Unit

    @DELETE("comments/{comment-id}")
    suspend fun deleteComment(
        @Path("comment-id") commentId: Int
    ): Unit

    @GET("comments/{comment-id}")
    suspend fun findById(
        @Path("comment-id") commentId: Int
    ): CommentResponse

    @GET("comments/post/{postId}")
    suspend fun getCommentsByPostId(
        @Path("postId") postId: Int,
        @Query("page") page: Int = 0,
        @Query("limit") limit: Int = 10
    ): PagedResponse<CommentResponse>

    @POST("comments/{comment-id}/like")
    suspend fun toggleLikeComment(
        @Path("comment-id") commentId: Int,
        @Query("userId") userId: String
    ): Unit

    @GET("comments/{comment-id}/is-liked")
    suspend fun isCommentLiked(
        @Path("comment-id") commentId: Int,
        @Query("userId") userId: String
    ): Boolean


}