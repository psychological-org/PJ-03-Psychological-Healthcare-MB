package com.example.beaceful.ui.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.beaceful.domain.model.Comment
import com.example.beaceful.domain.model.Post
import com.example.beaceful.domain.model.User
import com.example.beaceful.domain.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PostDetailsViewModel @Inject constructor(
    private val repo: PostRepository
) : ViewModel() {

    fun getPost(postId: Int): Post? = repo.getPostById(postId)

    fun getAuthor(post: Post?): User? = repo.getAuthorOfPost(post)
    fun getAuthor(postId: Int): User? = repo.getAuthorOfPostById(postId)

    fun getComments(postId: Int): List<Comment> = repo.getCommentsForPost(postId)

    fun getCommenter(comment: Comment): User? = repo.getCommenter(comment)

    val localComments = mutableStateListOf<Comment>()

    fun submitComment(postId: Int, userId: Int = 0, content: String) {
        val comment = repo.createComment(postId, userId, content.trim())
        localComments.add(comment)
    }
}