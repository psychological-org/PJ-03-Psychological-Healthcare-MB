package com.example.beaceful.domain.repository

import androidx.compose.runtime.mutableStateListOf
import com.example.beaceful.domain.model.Comment
import com.example.beaceful.domain.model.DumpDataProvider
import com.example.beaceful.domain.model.Post
import com.example.beaceful.domain.model.User
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRepository @Inject constructor(){
    fun getAllUsers(): List<User> = DumpDataProvider.listUser
    fun getUserById(userId: Int): User? =
        DumpDataProvider.listUser.find { it.id == userId }

    fun getAllPosts(): List<Post> = DumpDataProvider.posts
    fun getPostById(postId: Int): Post? =
        DumpDataProvider.posts.find { it.id == postId }
    fun getPostsByCommunity(communityId: Int): List<Post> =
        DumpDataProvider.posts.filter { it.communityId == communityId }
    fun getPostsByUser(userId: Int): List<Post> =
        DumpDataProvider.posts.filter { it.posterId == userId }
    fun getAuthorOfPost(post: Post?): User? =
        post?.posterId?.let { getUserById(it) }
    fun getAuthorOfPostById(postId: Int): User? =
        getPostById(postId)?.posterId?.let { getUserById(it) }

    fun getCommentCountForPost(postId: Int): Int =
        DumpDataProvider.comments.count { it.postId == postId }
    fun getCommentsForPost(postId: Int): List<Comment> =
        DumpDataProvider.comments.filter { it.postId == postId }
    fun getCommenter(comment: Comment): User? =
        getUserById(comment.userId)
    fun createComment(postId: Int, userId: Int, content: String): Comment {
        return Comment(
            id = DumpDataProvider.comments.size + 1,
            postId = postId,
            userId = userId,
            content = content,
            createdAt = LocalDateTime.now()
        )
    }

    private val likedPosts = mutableSetOf<Int>()
    fun isPostLiked(postId: Int) = likedPosts.contains(postId)
    fun toggleLike(postId: Int) {
        if (!likedPosts.add(postId)) likedPosts.remove(postId)
    }
}
