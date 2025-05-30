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
    val repository: PostRepository
) :
    ViewModel() {
    fun getAllCommunities(): List<Community> = DumpDataProvider.communities
    fun getUserCommunityIds(userId: Int): List<Int> {
        return DumpDataProvider.participantCommunities
            .filter { it.userId == userId }
            .map { it.communityId }
    }


    private val _hiddenPostIds = mutableStateListOf<Int>()
    val hiddenPostIds: List<Int> get() = _hiddenPostIds

    fun hidePost(postId: Int) {
        if (!_hiddenPostIds.contains(postId)) {
            _hiddenPostIds.add(postId)
        }
    }
//    post in community that user join only
    fun getUserCommunityPosts(userId: Int): List<Post> {
        val userCommunityIds = getUserCommunityIds(userId)
        return allPosts
            .filter { it.communityId in userCommunityIds }
            .filterNot { it.id in hiddenPostIds }
    }


    val allPosts: List<Post> = repository.getAllPosts()
    val allUsers: List<User> = repository.getAllUsers()

    private val _localPosts = mutableStateListOf<Post>()
    val localPosts: List<Post> get() = _localPosts

    private val _postText = mutableStateOf("")
    val postText: State<String> = _postText

    //    post in one community
    fun initCommunityPosts(communityId: Int) {
        val filteredPosts = allPosts.filter { it.communityId == communityId }.filterNot { it.id in hiddenPostIds }
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
        return getAllCommunities().find { it.id == id }
    }

    fun getAdminByCommunity(community: Community?): User? {
        return DumpDataProvider.listUser.find { it.id == community?.adminId }
    }

    fun joinCommunity() {

    }

}