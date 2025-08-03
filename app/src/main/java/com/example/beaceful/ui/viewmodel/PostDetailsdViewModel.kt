package com.example.beaceful.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beaceful.core.util.UserSession
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

    suspend fun updateComment(commentId: Int, content: String) {
        repo.updateComment(commentId, content)
        _comments.value = _comments.value.map { comment ->
            if (comment.id == commentId) comment.copy(content = content) else comment
        }
    }

    suspend fun deleteComment(commentId: Int) {
        repo.deleteComment(commentId)
        _comments.value = _comments.value.filter { it.id != commentId }
    }

    suspend fun toggleLikeComment(commentId: Int) {
        val userId = UserSession.getCurrentUserId() ?: return
        try {
            repo.toggleLikeComment(commentId, userId)
            val isLiked = repo.isCommentLiked(commentId, userId)
            _comments.value = _comments.value.map { comment ->
                if (comment.id == commentId) {
                    val newReactCount = if (isLiked) {
                        (comment.reactCount ?: 0) + 1
                    } else {
                        Math.max(0, (comment.reactCount ?: 0) - 1)
                    }
                    comment.copy(reactCount = newReactCount)
                } else comment
            }
            _error.value = null
        } catch (e: Exception) {
            _error.value = e.message
        }
    }

    suspend fun isCommentLiked(commentId: Int): Boolean {
        val userId = UserSession.getCurrentUserId() ?: return false
        return try {
            repo.isCommentLiked(commentId, userId)
        } catch (e: Exception) {
            false
        }
    }
}