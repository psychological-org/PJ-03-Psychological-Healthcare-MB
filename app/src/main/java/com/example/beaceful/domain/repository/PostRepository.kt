package com.example.beaceful.domain.repository

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import com.example.beaceful.core.network.comment.CommentApiService
import com.example.beaceful.core.network.comment.CommentRequest
import com.example.beaceful.core.network.post.LikePostRequest
import com.example.beaceful.core.network.post.PostApiService
import com.example.beaceful.core.network.post.PostRequest
import com.example.beaceful.core.network.post.PostResponse
import com.example.beaceful.domain.model.Comment
import com.example.beaceful.domain.model.DumpDataProvider
import com.example.beaceful.domain.model.Post
import com.example.beaceful.domain.model.PostVisibility
import com.example.beaceful.domain.model.User
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRepository @Inject constructor(
    private val userRepository: UserRepository,
    private val postApiService: PostApiService,
    private val commentApiService: CommentApiService
) {
    suspend fun getAllUsers(): List<User> = userRepository.getAllUsers()

    suspend fun getUserById(userId: String): User? = userRepository.getUserById(userId)

    suspend fun getAllPosts(page: Int = 0, limit: Int = 10): List<Post> {
        return postApiService.getAllPosts(page, limit).content.map { it.toPost() }
    }

    suspend fun updatePost(postId: Int, postRequest: PostRequest) {
        postApiService.updatePost(postRequest)
    }

    suspend fun deletePost(postId: Int) {
        postApiService.deletePost(postId)
    }

    suspend fun getPostById(postId: Int): Post? {
        return try {
            postApiService.getPostById(postId).toPost()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun createPost(postRequest: PostRequest): Int {
        return postApiService.createPost(postRequest)
    }

    suspend fun getPostsByCommunity(communityId: Int, page: Int = 0, limit: Int = 10): List<Post> {
        return postApiService.getPostsByCommunityId(
            communityId,
            page,
            limit
        ).content.map { it.toPost() }
    }

    suspend fun getPostsByUser(userId: String, page: Int = 0, limit: Int = 10): List<Post> {
        return postApiService.getPostsByUserId(userId, page, limit).content.map { it.toPost() }
    }

    suspend fun getAuthorOfPost(post: Post?): User? =
        post?.posterId?.let { userRepository.getUserById(it) }

    suspend fun getAuthorOfPostById(postId: Int): User? =
        getPostById(postId)?.posterId?.let { userRepository.getUserById(it) }

    suspend fun getCommentCountForPost(postId: Int): Int {
        return try {
            commentApiService.getCommentsByPostId(postId).totalElements.toInt()
        } catch (e: Exception) {
            0
        }
    }

    suspend fun getCommentsForPost(postId: Int, page: Int = 0, limit: Int = 10): List<Comment> {
        return try {
            commentApiService.getCommentsByPostId(
                postId,
                page,
                limit
            ).content.map { it.toComment() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getCommenter(comment: Comment): User? =
        userRepository.getUserById(comment.userId)

    suspend fun createComment(postId: Int, userId: String, content: String): Comment {
        val request = CommentRequest(
            content = content,
            userId = userId,
            postId = postId
        )
        val commentId = commentApiService.createComment(request)
        return Comment(
            id = commentId,
            content = content,
            imageUrl = null,
            userId = userId,
            postId = postId,
            reactCount = 0,
            createdAt = LocalDateTime.now()
        )
    }

    suspend fun isPostLiked(postId: Int, userId: String): Boolean {
        return try {
            postApiService.isPostLiked(postId, userId)
        } catch (e: Exception) {
            false
        }
    }

    suspend fun toggleLike(postId: Int, userId: String): Pair<Boolean, Int> {
        return try {
            val isLiked = postApiService.isPostLiked(postId, userId)
            if (isLiked) {
                val likePosts = postApiService.getLikePostByPostId(postId).content
                    .filter { it.userId == userId }
                if (likePosts.isNotEmpty()) {
                    postApiService.deleteLikePost(likePosts.first().id)
                    Log.d("PostRepository", "Unliked post $postId")
                }
            } else {
                val request = LikePostRequest(postId = postId, userId = userId)
                postApiService.createLikePost(request)
                Log.d("PostRepository", "Liked post $postId")
            }
            val newReactCount = postApiService.getPostById(postId).reactCount
            Log.d("PostRepository", "Post $postId new reactCount: $newReactCount")
            Pair(!isLiked, newReactCount)
        } catch (e: Exception) {
            Log.e("PostRepository", "Error toggling like: ${e.message}", e)
            Pair(false, -1)
        }
    }

    suspend fun existsById(postId: Int): Boolean {
        return try {
            postApiService.existsById(postId)
        } catch (e: Exception) {
            false
        }
    }
}

fun PostResponse.toPost(): Post {
    return Post(
        id = id,
        content = content,
        posterId = userId,
        communityId = communityId,
        visibility = toPostVisibility(visibility),
        imageUrl = imageUrl,
        reactCount = reactCount,
        createdAt = LocalDateTime.parse(createdAt, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    )
}

fun toPostVisibility(visibility: String): PostVisibility {
    return when (visibility.lowercase()) {
        "public" -> PostVisibility.PUBLIC
        "private" -> PostVisibility.PRIVATE
        "friend" -> PostVisibility.FRIEND
        else -> PostVisibility.PUBLIC
    }
}