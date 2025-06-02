package com.example.beaceful.viewmodel

import androidx.lifecycle.ViewModel
import com.example.beaceful.domain.model.Post
import com.example.beaceful.domain.model.User
import com.example.beaceful.domain.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DoctorViewModel @Inject constructor(
    private val repository: PostRepository
) : ViewModel() {

    fun getAllDoctors(): List<User> =
        repository.getAllUsers().filter { it.roleId == 2 }

    fun getPostsByDoctor(doctorId: Int): List<Post> =
        repository.getPostsByUser(doctorId)

    fun getDoctorById(doctorId: Int): User? =
        repository.getUserById(doctorId)

    fun getCommentCount(postId: Int): Int =
        repository.getCommentCountForPost(postId)
    fun getUserById(id: Int) = repository.getUserById(id)
    fun isPostLiked(postId: Int) = repository.isPostLiked(postId)

    fun toggleLike(postId: Int) = repository.toggleLike(postId)
}
