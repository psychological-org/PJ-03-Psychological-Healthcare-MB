package com.example.beaceful.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beaceful.domain.model.Comment
import com.example.beaceful.domain.model.Post
import com.example.beaceful.domain.model.User
import com.example.beaceful.domain.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DoctorViewModel @Inject constructor(
    private val repository: PostRepository
) : ViewModel() {
    private val _doctors = MutableStateFlow<List<User>>(emptyList())
    val doctors: StateFlow<List<User>> = _doctors.asStateFlow()

    private val _selectedDoctor = MutableStateFlow<User?>(null)
    val selectedDoctor: StateFlow<User?> = _selectedDoctor.asStateFlow()

    private val _doctorPosts = MutableStateFlow<List<Post>>(emptyList())
    val doctorPosts: StateFlow<List<Post>> = _doctorPosts.asStateFlow()

    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> = _comments.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        fetchDoctors()
    }

    private fun fetchDoctors() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val users = repository.getAllUsers().filter { it.roleId == 2 }
                Log.d("DoctorViewModel", "Fetched doctors: $users")
                _doctors.value = users
            } catch (e: Exception) {
                Log.e("DoctorViewModel", "Error fetching doctors: ${e.message}", e)
                _error.value = "Lỗi khi tải danh sách bác sĩ: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchDoctorById(doctorId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                val doctor = repository.getUserById(doctorId)
                if (doctor != null && doctor.roleId == 2) {
                    _selectedDoctor.value = doctor
                    Log.d("DoctorViewModel", "Fetched doctor $doctorId: $doctor")
                } else {
                    _error.value = "Bác sĩ không tồn tại"
                    _selectedDoctor.value = null
                }
            } catch (e: Exception) {
                Log.e("DoctorViewModel", "Error fetching doctor $doctorId: ${e.message}", e)
                _error.value = "Lỗi khi tải thông tin bác sĩ: ${e.message}"
                _selectedDoctor.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchDoctorPosts(doctorId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val posts = repository.getPostsByUser(doctorId)
                Log.d("DoctorViewModel", "Fetched posts for doctor $doctorId: $posts")
                _doctorPosts.value = posts
            } catch (e: Exception) {
                Log.e("DoctorViewModel", "Error fetching doctor posts: ${e.message}", e)
                _error.value = "Lỗi khi tải bài viết của bác sĩ: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadCommentsForPost(postId: Int, page: Int = 0, limit: Int = 10) {
        viewModelScope.launch {
            try {
                val comments = repository.getCommentsForPost(postId, page, limit)
                Log.d("DoctorViewModel", "Fetched comments for post $postId: $comments")
                _comments.value = comments
            } catch (e: Exception) {
                Log.e("DoctorViewModel", "Error fetching comments: ${e.message}", e)
                _error.value = "Lỗi khi tải bình luận: ${e.message}"
            }
        }
    }

    fun createComment(postId: Int, userId: String, content: String) {
        viewModelScope.launch {
            try {
                val comment = repository.createComment(postId, userId, content)
                Log.d("DoctorViewModel", "Created comment for post $postId: $comment")
                _comments.value = _comments.value + comment
            } catch (e: Exception) {
                Log.e("DoctorViewModel", "Error creating comment: ${e.message}", e)
                _error.value = "Lỗi khi tạo bình luận: ${e.message}"
            }
        }
    }

    fun getDoctorById(doctorId: String): User? {
        return _doctors.value.find { it.id == doctorId }
    }

    suspend fun getUserById(id: String): User? = repository.getUserById(id)

    suspend fun getCommentCount(postId: Int): Int =
        repository.getCommentCountForPost(postId)

    suspend fun isPostLiked(postId: Int, userId: String): Boolean =
        repository.isPostLiked(postId, userId)

    fun toggleLike(postId: Int, userId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val isLiked = repository.toggleLike(postId, userId)
                Log.d("DoctorViewModel", "Toggled like for post $postId: $isLiked")
                // Reload posts to update reactCount
                fetchDoctorPosts(userId)
            } catch (e: Exception) {
                Log.e("DoctorViewModel", "Error toggling like: ${e.message}", e)
                _error.value = "Lỗi khi thích bài viết: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
