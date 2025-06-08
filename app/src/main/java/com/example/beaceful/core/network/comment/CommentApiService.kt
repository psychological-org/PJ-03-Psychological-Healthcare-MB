package com.example.beaceful.core.network.comment

import com.example.beaceful.domain.model.PagedResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CommentApiService {
    @POST("comments")
    suspend fun createComment(
        @Body request: CommentRequest
    ): Int

    @GET("comments/post/{postId}")
    suspend fun getCommentsByPostId(
        @Path("postId") postId: Int,
        @Query("page") page: Int = 0,
        @Query("limit") limit: Int = 10
    ): PagedResponse<CommentResponse>
}