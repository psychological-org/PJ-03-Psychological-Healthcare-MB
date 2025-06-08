package com.example.beaceful.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beaceful.core.network.post.PostRequest
import com.example.beaceful.domain.model.Community
import com.example.beaceful.domain.model.Comment
import com.example.beaceful.domain.model.Post
import com.example.beaceful.domain.model.User
import com.example.beaceful.domain.repository.CommunityRepository
import com.example.beaceful.domain.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommunityViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val communityRepository: CommunityRepository
) : ViewModel() {

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts.asStateFlow()

    private val _community = MutableStateFlow<Community?>(null)
    val community: StateFlow<Community?> = _community.asStateFlow()

    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> = _comments.asStateFlow()

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users.asStateFlow()

    private val _postText = mutableStateOf("")
    val postText: State<String> = _postText

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _hiddenPostIds = mutableStateListOf<Int>()
    val hiddenPostIds: List<Int> get() = _hiddenPostIds

    fun loadCommunity(communityId: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val community = communityRepository.getCommunityById(communityId)
                _community.value = community
                if (community != null) {
                    val posts = postRepository.getPostsByCommunity(communityId)
                        .filterNot { it.id in _hiddenPostIds }
                    _posts.value = posts
                }
                _users.value = postRepository.getAllUsers()
            } catch (e: Exception) {
                _error.value = "Lỗi khi tải cộng đồng: ${e.message}"
                Log.e("CommunityViewModel", "Error loading community: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadCommentsForPost(postId: Int, page: Int = 0, limit: Int = 10) {
        viewModelScope.launch {
            try {
                val comments = postRepository.getCommentsForPost(postId, page, limit)
                Log.d("CommunityViewModel", "Fetched comments for post $postId: $comments")
                _comments.value = comments
            } catch (e: Exception) {
                _error.value = "Lỗi khi tải bình luận: ${e.message}"
                Log.e("CommunityViewModel", "Error fetching comments: ${e.message}", e)
            }
        }
    }

    fun createComment(postId: Int, userId: String, content: String) {
        viewModelScope.launch {
            try {
                val comment = postRepository.createComment(postId, userId, content)
                Log.d("CommunityViewModel", "Created comment for post $postId: $comment")
                _comments.value = _comments.value + comment
            } catch (e: Exception) {
                _error.value = "Lỗi khi tạo bình luận: ${e.message}"
                Log.e("CommunityViewModel", "Error creating comment: ${e.message}", e)
            }
        }
    }

    fun onPostTextChange(newText: String) {
        _postText.value = newText
    }

    fun submitPost(communityId: Int, userId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val postRequest = PostRequest(
                    id = null,
                    content = _postText.value,
                    imageUrl = null,
                    visibility = "PUBLIC",
                    reactCount = 0,
                    communityId = communityId,
                    userId = userId
                )
                postRepository.createPost(postRequest)
                // Reload posts for the community
                loadCommunity(communityId)
                _postText.value = ""
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Lỗi khi tạo bài viết: ${e.message}"
                Log.e("CommunityViewModel", "Error creating post: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun hidePost(postId: Int) {
        if (!_hiddenPostIds.contains(postId)) {
            _hiddenPostIds.add(postId)
            _posts.value = _posts.value.filterNot { it.id in _hiddenPostIds }
        }
    }

    suspend fun isPostLiked(postId: Int, userId: String): Boolean {
        return postRepository.isPostLiked(postId, userId)
    }

    fun toggleLike(postId: Int, userId: String) {
        viewModelScope.launch {
            try {
                val isLiked = postRepository.toggleLike(postId, userId)
                Log.d("CommunityViewModel", "Toggled like for post $postId: $isLiked")
                // Reload posts to update reactCount
                _community.value?.id?.let { loadCommunity(it) }
            } catch (e: Exception) {
                _error.value = "Lỗi khi thích bài viết: ${e.message}"
                Log.e("CommunityViewModel", "Error toggling like: ${e.message}", e)
            }
        }
    }

    fun joinCommunity(userId: String, communityId: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                communityRepository.joinCommunity(userId, communityId)
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Lỗi khi tham gia cộng đồng: ${e.message}"
                Log.e("CommunityViewModel", "Error joining community: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    suspend fun getUserById(userId: String): User? = postRepository.getUserById(userId)

    @Composable
    fun postsAsState(): State<List<Post>> = _posts.collectAsState()
}