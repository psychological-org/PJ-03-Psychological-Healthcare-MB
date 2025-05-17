package com.example.beaceful.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.beaceful.domain.model.Community
import com.example.beaceful.domain.model.DumpDataProvider
import com.example.beaceful.domain.model.Post
import com.example.beaceful.domain.model.PostVisibility
import com.example.beaceful.domain.model.User
import com.example.beaceful.domain.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class ForumViewModel @Inject constructor(
    private val repository: PostRepository
) :
    ViewModel() {
    //    Data
    val communities: List<Community> = DumpDataProvider.communities

    val allPosts: List<Post> = repository.getAllPosts()
    val allUsers: List<User> = repository.getAllUsers()

    private val _localPosts = mutableStateListOf<Post>()
    val localPosts: List<Post> get() = _localPosts

    private val _postText = mutableStateOf("")
    val postText: State<String> = _postText

    //    Func
    fun initCommunityPosts(communityId: Int) {
        val filteredPosts = allPosts.filter { it.communityId == communityId }
        _localPosts.clear()
        _localPosts.addAll(filteredPosts)
    }

    fun onPostTextChange(newText: String) {
        _postText.value = newText
    }

    fun submitPost(communityId: Int, posterId: Int = 1) {
        val post = Post(
            id = _localPosts.size + 1,
            content = _postText.value,
            posterId = posterId,
            communityId = communityId,
            visibility = PostVisibility.PUBLIC,
            imageUrl = "",
            reactCount = 0,
            createdAt = LocalDateTime.now()
        )
        _localPosts.add(post)
        _postText.value = ""
    }

    fun getCommunityById(id: Int): Community? {
        return communities.find { it.id == id }
    }

    fun getAdminByCommunity(community: Community?): User? {
        return DumpDataProvider.listUser.find { it.id == community?.adminId }
    }

    fun getUserById(id: Int) = repository.getUserById(id)
    fun getCommentCount(postId: Int) = repository.getCommentCountForPost(postId)

    fun isPostLiked(postId: Int) = repository.isPostLiked(postId)

    fun toggleLike(postId: Int) = repository.toggleLike(postId)


}