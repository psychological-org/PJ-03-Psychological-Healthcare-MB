package com.example.beaceful.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beaceful.core.network.topic.TopicRequest
import com.example.beaceful.domain.model.PagedResponse
import com.example.beaceful.domain.model.Topic
import com.example.beaceful.domain.repository.TopicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TopicViewModel @Inject constructor(
    private val topicRepository: TopicRepository
) : ViewModel() {

    private val _topics = MutableLiveData<Result<PagedResponse<Topic>>>()
    val topics: LiveData<Result<PagedResponse<Topic>>> = _topics

    private val _topic = MutableLiveData<Result<Topic>>()
    val topic: LiveData<Result<Topic>> = _topic

    private val _createResult = MutableLiveData<Result<Int>>()
    val createResult: LiveData<Result<Int>> = _createResult

    private val _updateResult = MutableLiveData<Result<Unit>>()
    val updateResult: LiveData<Result<Unit>> = _updateResult

    private val _deleteResult = MutableLiveData<Result<Unit>>()
    val deleteResult: LiveData<Result<Unit>> = _deleteResult

    private val _existsResult = MutableLiveData<Result<Boolean>>()
    val existsResult: LiveData<Result<Boolean>> = _existsResult

    fun getAllTopics(page: Int, limit: Int) {
        viewModelScope.launch {
            _topics.value = topicRepository.getAllTopics(page, limit)
        }
    }

    fun getTopicById(topicId: Int) {
        viewModelScope.launch {
            _topic.value = topicRepository.getTopicById(topicId)
        }
    }

    fun createTopic(request: TopicRequest) {
        viewModelScope.launch {
            _createResult.value = topicRepository.createTopic(request)
        }
    }

    fun updateTopic(request: TopicRequest) {
        viewModelScope.launch {
            _updateResult.value = topicRepository.updateTopic(request)
        }
    }

    fun deleteTopic(topicId: Int) {
        viewModelScope.launch {
            _deleteResult.value = topicRepository.deleteTopic(topicId)
        }
    }

    fun checkTopicExists(topicId: Int) {
        viewModelScope.launch {
            _existsResult.value = topicRepository.existsById(topicId)
        }
    }
}