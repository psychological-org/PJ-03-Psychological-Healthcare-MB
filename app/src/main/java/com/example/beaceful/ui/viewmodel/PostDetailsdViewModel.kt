package com.example.beaceful.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beaceful.domain.model.Comment
import com.example.beaceful.domain.model.Post
import com.example.beaceful.domain.model.User
import com.example.beaceful.domain.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostDetailsViewModel @Inject constructor(
    private val repo: PostRepository
) : ViewModel() {
    private val _post = MutableStateFlow<Post?>(null)
    val post: StateFlow<Post?> = _post

    private val _author = MutableStateFlow<User?>(null)
    val author: StateFlow<User?> = _author

    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> = _comments

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun initPost(postId: Int) {
        viewModelScope.launch {
            try {
                // Tải bài post
                val post = repo.getPostById(postId)
                _post.value = post

                // Tải tác giả
                val author = post?.let { repo.getAuthorOfPost(it) }
                _author.value = author

                // Tải bình luận
                val comments = repo.getCommentsForPost(postId)
                _comments.value = comments
            } catch (e: Exception) {
                Log.e("PostDetailsViewModel", "Error loading post details: ${e.message}", e)
                _error.value = "Lỗi khi tải chi tiết bài post: ${e.message}"
            }
        }
    }

    suspend fun getCommenter(comment: Comment): User? = repo.getCommenter(comment)

    suspend fun submitComment(postId: Int, userId: String, content: String) {
        val comment = repo.createComment(postId, userId, content.trim())
        _comments.value = _comments.value + comment
    }
}