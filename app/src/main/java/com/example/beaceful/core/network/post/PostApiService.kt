package com.example.beaceful.core.network.post

import com.example.beaceful.core.network.comment.CommentResponse
import com.example.beaceful.domain.model.PagedResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface PostApiService {
    @GET("posts")
    suspend fun getAllPosts(
        @Query("page") page: Int = 0,
        @Query("limit") limit: Int = 10
    ): PagedResponse<PostResponse>

    @GET("posts/{post-id}")
    suspend fun getPostById(@Path("post-id") postId: Int): PostResponse

    @POST("posts")
    suspend fun createPost(@Body postRequest: PostRequest): Int

    @PUT("posts/{post-id}")
    suspend fun updatePost(
        @Path("post-id") postId: Int,
        @Body postRequest: PostRequest
    ): Unit

    @DELETE("posts/{post-id}")
    suspend fun deletePost(@Path("post-id") postId: Int): Unit

    @GET("posts/exists/{post-id}")
    suspend fun existsById(@Path("post-id") postId: Int): Boolean

    @GET("posts/user/{userId}")
    suspend fun getPostsByUserId(
        @Path("userId") userId: String,
        @Query("page") page: Int = 0,
        @Query("limit") limit: Int = 10
    ): PagedResponse<PostResponse>

    @GET("posts/community/{communityId}")
    suspend fun getPostsByCommunityId(
        @Path("communityId") communityId: Int,
        @Query("page") page: Int = 0,
        @Query("limit") limit: Int = 10
    ): PagedResponse<PostResponse>

    @GET("posts/like-post/{postId}/user/{userId}")
    suspend fun isPostLiked(
        @Path("postId") postId: Int,
        @Path("userId") userId: String
    ): Boolean

    @POST("posts/like-post")
    suspend fun createLikePost(
        @Body request: LikePostRequest
    ): Int

    @DELETE("posts/like-post/{id}")
    suspend fun deleteLikePost(
        @Path("id") likePostId: Int
    ): Unit

    @GET("comments/post/{postId}")
    suspend fun getCommentsByPostId(
        @Path("postId") postId: Int,
        @Query("page") page: Int = 0,
        @Query("limit") limit: Int = 10
    ): PagedResponse<CommentResponse>

    @GET("posts/like-post/postId/{postId}")
    suspend fun getLikePostByPostId(
        @Path("postId") postId: Int,
        @Query("page") page: Int = 0,
        @Query("limit") limit: Int = 10
    ): PagedResponse<LikePostResponse>
}