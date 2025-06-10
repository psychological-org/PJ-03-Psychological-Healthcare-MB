package com.example.beaceful.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beaceful.core.util.UserSession
import com.example.beaceful.domain.model.Comment
import com.example.beaceful.domain.model.Post
import com.example.beaceful.domain.model.User
import com.example.beaceful.domain.repository.PostRepository
import com.example.beaceful.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val postRepository: PostRepository
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts.asStateFlow()

    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> = _comments.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        fetchUserProfile()
    }

    private fun fetchUserProfile() {
        viewModelScope.launch {
            try {
                val userId = UserSession.getCurrentUserId()
                val user = userRepository.getUserById(userId)
                Log.d("ProfileViewModel", "Fetched user: $user")
                _user.value = user
                fetchUserPosts(userId)
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error fetching user: ${e.message}", e)
                _error.value = "Lỗi khi tải hồ sơ: ${e.message}"
            }
        }
    }

    private fun fetchUserPosts(userId: String) {
        viewModelScope.launch {
            try {
                val posts = postRepository.getPostsByUser(userId)
                Log.d("ProfileViewModel", "Fetched posts for user $userId: $posts")
                _posts.value = posts
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error fetching posts: ${e.message}", e)
                _error.value = "Lỗi khi tải bài viết: ${e.message}"
            }
        }
    }

    fun loadCommentsForPost(postId: Int, page: Int = 0, limit: Int = 10) {
        viewModelScope.launch {
            try {
                val comments = postRepository.getCommentsForPost(postId, page, limit)
                Log.d("ProfileViewModel", "Fetched comments for post $postId: $comments")
                _comments.value = comments
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error fetching comments: ${e.message}", e)
                _error.value = "Lỗi khi tải bình luận: ${e.message}"
            }
        }
    }

    fun createComment(postId: Int, userId: String, content: String) {
        viewModelScope.launch {
            try {
                val comment = postRepository.createComment(postId, userId, content)
                Log.d("ProfileViewModel", "Created comment for post $postId: $comment")
                _comments.value = _comments.value + comment
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error creating comment: ${e.message}", e)
                _error.value = "Lỗi khi tạo bình luận: ${e.message}"
            }
        }
    }

    suspend fun getCommentCount(postId: Int): Int =
        postRepository.getCommentCountForPost(postId)

    suspend fun isPostLiked(postId: Int, userId: String): Boolean =
        postRepository.isPostLiked(postId, userId)

    fun toggleLike(postId: Int, userId: String) {
        viewModelScope.launch {
            try {
                val isLiked = postRepository.toggleLike(postId, userId)
                Log.d("ProfileViewModel", "Toggled like for post $postId: $isLiked")
                fetchUserPosts(userId) // Reload posts to update reactCount
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error toggling like: ${e.message}", e)
                _error.value = "Lỗi khi thích bài viết: ${e.message}"
            }
        }
    }

    suspend fun getUserById(userId: String): User? =
        userRepository.getUserById(userId)
}