package com.example.beaceful.ui.viewmodel

import android.content.Context
import android.util.Log
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beaceful.core.network.user.UserRequest
import com.example.beaceful.core.util.UserSession
import com.example.beaceful.domain.amazon.S3Manager
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
import java.io.File
import java.util.UUID
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

    private val _success = MutableStateFlow<String?>(null)
    val success: StateFlow<String?> = _success.asStateFlow()

    init {
        fetchUserProfile()
    }

    fun fetchUserProfile() {
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

    fun updateUserProfile(
        context: Context,
        fullName: String?,
        yearOfBirth: String?,
        headline: String?,
        biography: String?,
        avatarUri: Uri?,
        backgroundUri: Uri?
    ) {
        viewModelScope.launch {
            try {
                val userId = UserSession.getCurrentUserId()
                if (userId != null) {
                    // Upload images to S3
                    val avatarUrl = avatarUri?.let { uri ->
                        val key = "avatars/$userId/${UUID.randomUUID()}.jpg"
                        uploadImageToS3(context, uri, key)
                    }
                    val backgroundUrl = backgroundUri?.let { uri ->
                        val key = "backgrounds/$userId/${UUID.randomUUID()}.jpg"
                        uploadImageToS3(context, uri, key)
                    }

                    // Split fullName into firstName and lastName
                    val (firstName, lastName) = splitFullName(fullName)

                    val request = UserRequest(
                        id = userId, // mongoId
                        username = null,
                        password = null,
                        email = null,
                        firstName = firstName,
                        lastName = lastName,
                        role = null,
                        biography = biography?.takeIf { it.isNotBlank() },
                        yearOfBirth = yearOfBirth?.takeIf { it.isNotBlank() },
                        yearOfExperience = null,
                        avatarUrl = avatarUrl,
                        backgroundUrl = backgroundUrl,
                        phone = null,
                        content = headline?.takeIf { it.isNotBlank() }
                    )

                    userRepository.updateUser(request)
                    // Tải lại thông tin người dùng để cập nhật _user
                    val updatedUser = userRepository.getUserById(userId)
                    _user.value = updatedUser
                    _success.value = "Cập nhật hồ sơ thành công"
                } else {
                    _error.value = "Lỗi: Người dùng chưa đăng nhập"
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error updating user: ${e.message}", e)
                _error.value = "Lỗi khi cập nhật hồ sơ: ${e.message}"
            }
        }
    }

    private fun splitFullName(fullName: String?): Pair<String?, String?> {
        if (fullName.isNullOrBlank()) return Pair(null, null)
        val names = fullName.trim().split("\\s+".toRegex())
        return when (names.size) {
            0 -> Pair(null, null)
            1 -> Pair(names[0], null)
            else -> Pair(names.first(), names.drop(1).joinToString(" "))
        }
    }

    fun deleteUser() {
        viewModelScope.launch {
            try {
                val userId = UserSession.getCurrentUserId()
                if (userId != null) {
                    userRepository.deleteUser(userId)
                    _user.value = null
                    _success.value = "Xóa tài khoản thành công"
                } else {
                    _error.value = "Lỗi: Người dùng chưa đăng nhập"
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error deleting user: ${e.message}", e)
                _error.value = "Lỗi khi xóa tài khoản: ${e.message}"
            }
        }
    }

    private suspend fun uploadImageToS3(context: Context, uri: Uri, key: String): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val file = File(context.cacheDir, key.substringAfterLast("/"))
            file.writeBytes(inputStream.readBytes())
            inputStream.close()
            S3Manager.uploadFile(file, key)
        } catch (e: Exception) {
            Log.e("ProfileViewModel", "Error uploading image: ${e.message}", e)
            null
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

    fun clearMessages() {
        _success.value = null
        _error.value = null
    }
}