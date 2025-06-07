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
import com.example.beaceful.domain.model.Comment
import com.example.beaceful.domain.model.Community
import com.example.beaceful.domain.model.DumpDataProvider
import com.example.beaceful.domain.model.Post
import com.example.beaceful.domain.model.PostVisibility
import com.example.beaceful.domain.model.User
import com.example.beaceful.domain.repository.CommunityRepository
import com.example.beaceful.domain.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class ForumViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val communityRepository: CommunityRepository
) : ViewModel() {

    private val _allPosts = MutableStateFlow<List<Post>>(emptyList())
    val allPosts: StateFlow<List<Post>> = _allPosts.asStateFlow()

    private val _allUsers = MutableStateFlow<List<User>>(emptyList())
    val allUsers: StateFlow<List<User>> = _allUsers.asStateFlow()

    private val _allCommunities = MutableStateFlow<List<Community>>(emptyList())
    val allCommunities: StateFlow<List<Community>> = _allCommunities.asStateFlow()

    private val _userCommunityIds = MutableStateFlow<List<Int>>(emptyList())
    val userCommunityIds: StateFlow<List<Int>> = _userCommunityIds.asStateFlow()

    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> = _comments.asStateFlow()

    private val _postText = mutableStateOf("")
    val postText: State<String> = _postText

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _hiddenPostIds = mutableStateListOf<Int>()
    val hiddenPostIds: List<Int> get() = _hiddenPostIds

    init {
        fetchUsers()
        fetchPosts()
        fetchCommunities()
    }

    private fun fetchUsers() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val users = postRepository.getAllUsers()
                _allUsers.value = users
            } catch (e: Exception) {
                _error.value = "Lỗi khi tải người dùng: ${e.message}"
                Log.e("ForumViewModel", "Error fetching users: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun fetchPosts(page: Int = 0, limit: Int = 10) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val posts = postRepository.getAllPosts(page, limit)
                _allPosts.value = posts.filterNot { it.id in _hiddenPostIds }
            } catch (e: Exception) {
                _error.value = "Lỗi khi tải bài viết: ${e.message}"
                Log.e("ForumViewModel", "Error fetching posts: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun fetchCommunities(page: Int = 0, limit: Int = 10) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val communities = communityRepository.getAllCommunities(page, limit)
                _allCommunities.value = communities
            } catch (e: Exception) {
                _error.value = "Lỗi khi tải cộng đồng: ${e.message}"
                Log.e("ForumViewModel", "Error fetching communities: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchUserCommunityIds(userId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val communityIds = communityRepository.getUserCommunityIds(userId)
                _userCommunityIds.value = communityIds
            } catch (e: Exception) {
                _error.value = "Lỗi khi tải cộng đồng của người dùng: ${e.message}"
                Log.e("ForumViewModel", "Error fetching user communities: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadCommentsForPost(postId: Int, page: Int = 0, limit: Int = 10) {
        viewModelScope.launch {
            try {
                val comments = postRepository.getCommentsForPost(postId, page, limit)
                Log.d("ForumViewModel", "Fetched comments for post $postId: $comments")
                _comments.value = comments
            } catch (e: Exception) {
                _error.value = "Lỗi khi tải bình luận: ${e.message}"
                Log.e("ForumViewModel", "Error fetching comments: ${e.message}", e)
            }
        }
    }

    fun createComment(postId: Int, userId: String, content: String) {
        viewModelScope.launch {
            try {
                val comment = postRepository.createComment(postId, userId, content)
                Log.d("ForumViewModel", "Created comment for post $postId: $comment")
                _comments.value = _comments.value + comment
            } catch (e: Exception) {
                _error.value = "Lỗi khi tạo bình luận: ${e.message}"
                Log.e("ForumViewModel", "Error creating comment: ${e.message}", e)
            }
        }
    }

    fun getAllCommunities(): List<Community> = _allCommunities.value

    fun getUserCommunityIds(): List<Int> = _userCommunityIds.value

    fun hidePost(postId: Int) {
        if (!_hiddenPostIds.contains(postId)) {
            _hiddenPostIds.add(postId)
            _allPosts.value = _allPosts.value.filterNot { it.id in _hiddenPostIds }
        }
    }

    fun getUserCommunityPosts(userId: String): List<Post> {
        val userCommunityIds = getUserCommunityIds()
        return _allPosts.value
            .filter { it.communityId in userCommunityIds }
            .filterNot { it.id in _hiddenPostIds }
    }

    @Composable
    fun postsAsState(): State<List<Post>> = _allPosts.collectAsState()

    fun initCommunityPosts(communityId: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val filteredPosts = postRepository.getPostsByCommunity(communityId)
                    .filterNot { it.id in _hiddenPostIds }
                _allPosts.value = filteredPosts
            } catch (e: Exception) {
                _error.value = "Lỗi khi tải bài viết cộng đồng: ${e.message}"
                Log.e("ForumViewModel", "Error fetching community posts: ${e.message}", e)
            } finally {
                _isLoading.value = false
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
                val postId = postRepository.createPost(postRequest)
                // Reload posts for the community
                initCommunityPosts(communityId)
                _postText.value = ""
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Lỗi khi tạo bài viết: ${e.message}"
                Log.e("ForumViewModel", "Error creating post: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getCommunityById(id: Int): Community? {
        return _allCommunities.value.find { it.id == id }
    }

    fun getAdminByCommunity(community: Community?): User? {
        return _allUsers.value.find { it.id == community?.adminId }
    }

    suspend fun getCommunityMembers(communityId: Int): List<User> {
        val memberIds = communityRepository.getCommunityMembers(communityId)
        return memberIds.mapNotNull { userId ->
            _allUsers.value.find { it.id == userId }
        }
    }

    suspend fun getUserById(userId: String): User? = postRepository.getUserById(userId)

    fun joinCommunity(userId: String, communityId: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                communityRepository.joinCommunity(userId, communityId)
                fetchUserCommunityIds(userId)
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Lỗi khi tham gia cộng đồng: ${e.message}"
                Log.e("ForumViewModel", "Error joining community: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    suspend fun getCommentCountForPost(postId: Int): Int {
        return postRepository.getCommentCountForPost(postId)
    }

    suspend fun isPostLiked(postId: Int, userId: String): Boolean {
        return postRepository.isPostLiked(postId, userId)
    }

    fun toggleLike(postId: Int, userId: String) {
        viewModelScope.launch {
            try {
                val isLiked = postRepository.toggleLike(postId, userId)
                Log.d("ForumViewModel", "Toggled like for post $postId: $isLiked")
                // Reload posts to update reactCount
                fetchPosts()
            } catch (e: Exception) {
                _error.value = "Lỗi khi thích bài viết: ${e.message}"
                Log.e("ForumViewModel", "Error toggling like: ${e.message}", e)
            }
        }
    }

}