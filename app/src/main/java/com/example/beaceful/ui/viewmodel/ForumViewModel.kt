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
import com.example.beaceful.core.util.UserSession
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

    private val _communityMembers = MutableStateFlow<List<User>>(emptyList())
    val communityMembers: StateFlow<List<User>> = _communityMembers.asStateFlow()

    private val _likedPosts = MutableStateFlow<Map<Int, Boolean>>(emptyMap())
    val likedPosts: StateFlow<Map<Int, Boolean>> = _likedPosts.asStateFlow()

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
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchPosts(page: Int = 0, limit: Int = 100) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val posts = postRepository.getAllPosts(page, limit)
                    .filter { postRepository.existsById(it.id) }
                _allPosts.value = posts
                    .filterNot { it.id in _hiddenPostIds }
                    .sortedByDescending { it.createdAt }
                val userId = UserSession.getCurrentUserId()
                val likedMap = posts.associate { post ->
                    post.id to postRepository.isPostLiked(post.id, userId)
                }
                _likedPosts.value = likedMap
            } catch (e: Exception) {
                _error.value = "Lỗi khi tải bài viết: ${e.message}"
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
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadCommentsForPost(postId: Int, page: Int = 0, limit: Int = 10) {
        viewModelScope.launch {
            try {
                val comments = postRepository.getCommentsForPost(postId, page, limit)
                _comments.value = comments
            } catch (e: Exception) {
                _error.value = "Lỗi khi tải bình luận: ${e.message}"
            }
        }
    }

    fun createComment(postId: Int, userId: String, content: String) {
        viewModelScope.launch {
            try {
                val comment = postRepository.createComment(postId, userId, content)
                _comments.value = _comments.value + comment
            } catch (e: Exception) {
                _error.value = "Lỗi khi tạo bình luận: ${e.message}"
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
                // Fetch tất cả bài post từ API để đảm bảo dữ liệu mới nhất
                val allPosts = postRepository.getAllPosts(page = 0, limit = 100)
                _allPosts.value = allPosts
                    .filterNot { it.id in _hiddenPostIds }
                    .sortedByDescending { it.createdAt }
                // Cập nhật likedPosts
                val userId = UserSession.getCurrentUserId()
                val likedMap = allPosts.associate { post ->
                    post.id to postRepository.isPostLiked(post.id, userId)
                }
                _likedPosts.value = likedMap
            } catch (e: Exception) {
                _error.value = "Lỗi khi tải bài viết cộng đồng: ${e.message}"
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
                val newPost = postRepository.getPostById(postId)
                if (newPost != null) {
                    _allPosts.value = (_allPosts.value + newPost)
                        .filterNot { it.id in _hiddenPostIds }
                        .sortedByDescending { it.createdAt }
                    _likedPosts.value = _likedPosts.value + (newPost.id to false)
                    Log.d("ForumViewModel", "Added new post $postId to _allPosts")
                }
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

    fun fetchCommunityMembers(communityId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val members = communityRepository.getCommunityMembers(communityId)
                _communityMembers.value = members
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message ?: "Error loading community members"
            } finally {
                _isLoading.value = false
            }
        }
    }

    suspend fun getCommunityMembers(communityId: Int): List<User> {
        return communityRepository.getCommunityMembers(communityId)
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
                val (newIsLiked, newReactCount) = postRepository.toggleLike(postId, userId)
                if (newReactCount != -1) {
                    _allPosts.value = _allPosts.value.map { post ->
                        if (post.id == postId) {
                            post.copy(reactCount = newReactCount)
                        } else {
                            post
                        }
                    }
                    _likedPosts.value = _likedPosts.value + (postId to newIsLiked)
                    Log.d("ForumViewModel", "Toggled like for post $postId: $newIsLiked, reactCount: $newReactCount")
                } else {
                    _error.value = "Lỗi khi cập nhật lượt thích"
                }
            } catch (e: Exception) {
                _error.value = "Lỗi khi thích bài viết: ${e.message}"
                Log.e("ForumViewModel", "Error toggling like: ${e.message}", e)
            }
        }
    }

    fun deletePost(postId: Int, userId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val post = postRepository.getPostById(postId)
                if (post != null && post.posterId == userId) {
                    postRepository.deletePost(postId)
                    _allPosts.value = _allPosts.value.filterNot { it.id == postId }
                    _likedPosts.value -= postId
                    _error.value = null
                    Log.d("ForumViewModel", "Deleted post $postId")
                } else {
                    _error.value = "Không có quyền xóa bài viết này"
                }
            } catch (e: Exception) {
                _error.value = "Lỗi khi xóa bài viết: ${e.message}"
                Log.e("ForumViewModel", "Error deleting post: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updatePost(postId: Int, userId: String, content: String, visibility: PostVisibility) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val post = postRepository.getPostById(postId)
                if (post != null && post.posterId == userId) {
                    val postRequest = PostRequest(
                        id = postId,
                        content = content,
                        imageUrl = post.imageUrl,
                        visibility = visibility.toString(),
                        reactCount = post.reactCount,
                        communityId = post.communityId,
                        userId = userId
                    )
                    postRepository.updatePost(postId, postRequest)
                    // Làm mới _allPosts sau khi cập nhật
                    val allPosts = postRepository.getAllPosts(page = 0, limit = 100)
                        .filter { postRepository.existsById(it.id) }
                    _allPosts.value = allPosts
                        .filterNot { it.id in _hiddenPostIds }
                        .sortedByDescending { it.createdAt }
                    _error.value = null
                    Log.d("ForumViewModel", "Updated post $postId successfully")
                } else {
                    _error.value = "Không có quyền chỉnh sửa bài viết này"
                    Log.e("ForumViewModel", "Permission denied: postId=$postId, userId=$userId")
                }
            } catch (e: Exception) {
                _error.value = "Lỗi khi chỉnh sửa bài viết: ${e.message}"
                Log.e("ForumViewModel", "Error updating post $postId: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

}