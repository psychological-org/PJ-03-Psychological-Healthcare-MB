package com.example.beaceful.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beaceful.core.network.collection.CollectionRequest
import com.example.beaceful.core.network.collection.CollectionSeenRequest
import com.example.beaceful.core.util.UserSession
import com.example.beaceful.domain.model.Collection
import com.example.beaceful.domain.model.CollectionSeen
import com.example.beaceful.domain.model.PagedResponse
import com.example.beaceful.domain.repository.CollectionRepository
import com.example.beaceful.domain.repository.CollectionSeenRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CollectionViewModel @Inject constructor(
    private val collectionRepository: CollectionRepository,
    private val collectionSeenRepository: CollectionSeenRepository
) : ViewModel() {

    private val _collections = MutableStateFlow<Result<PagedResponse<Collection>>?>(null)
    val collections: StateFlow<Result<PagedResponse<Collection>>?> = _collections.asStateFlow()

    private val _collection = MutableStateFlow<Result<Collection>?>(null)
    val collection: StateFlow<Result<Collection>?> = _collection.asStateFlow()

    private val _collectionsByTopic = MutableStateFlow<Result<List<Collection>>?>(null)
    val collectionsByTopic: StateFlow<Result<List<Collection>>?> = _collectionsByTopic.asStateFlow()

    private val _createCollectionResult = MutableStateFlow<Result<Int>?>(null)
    val createCollectionResult: StateFlow<Result<Int>?> = _createCollectionResult.asStateFlow()

    private val _updateCollectionResult = MutableStateFlow<Result<Unit>?>(null)
    val updateCollectionResult: StateFlow<Result<Unit>?> = _updateCollectionResult.asStateFlow()

    private val _deleteCollectionResult = MutableStateFlow<Result<Unit>?>(null)
    val deleteCollectionResult: StateFlow<Result<Unit>?> = _deleteCollectionResult.asStateFlow()

    private val _existsCollectionResult = MutableStateFlow<Result<Boolean>?>(null)
    val existsCollectionResult: StateFlow<Result<Boolean>?> = _existsCollectionResult.asStateFlow()

    private val _collectionSeen = MutableStateFlow<Result<CollectionSeen>?>(null)
    val collectionSeen: StateFlow<Result<CollectionSeen>?> = _collectionSeen.asStateFlow()

    private val _collectionsSeenByUser = MutableStateFlow<Result<PagedResponse<CollectionSeen>>?>(null)
    val collectionsSeenByUser: StateFlow<Result<PagedResponse<CollectionSeen>>?> = _collectionsSeenByUser.asStateFlow()

    private val _allCollectionsSeen = MutableStateFlow<Result<PagedResponse<CollectionSeen>>?>(null)
    val allCollectionsSeen: StateFlow<Result<PagedResponse<CollectionSeen>>?> = _allCollectionsSeen.asStateFlow()

    private val _createCollectionSeenResult = MutableStateFlow<Result<Int>?>(null)
    val createCollectionSeenResult: StateFlow<Result<Int>?> = _createCollectionSeenResult.asStateFlow()

    private val _updateCollectionSeenResult = MutableStateFlow<Result<Unit>?>(null)
    val updateCollectionSeenResult: StateFlow<Result<Unit>?> = _updateCollectionSeenResult.asStateFlow()

    private val _deleteCollectionSeenResult = MutableStateFlow<Result<Unit>?>(null)
    val deleteCollectionSeenResult: StateFlow<Result<Unit>?> = _deleteCollectionSeenResult.asStateFlow()

    private val _existsCollectionSeenResult = MutableStateFlow<Result<Boolean>?>(null)
    val existsCollectionSeenResult: StateFlow<Result<Boolean>?> = _existsCollectionSeenResult.asStateFlow()

    fun getAllCollections(page: Int, limit: Int) {
        viewModelScope.launch {
            _collections.value = collectionRepository.getAllCollections(page, limit)
        }
    }

    fun getCollectionById(collectionId: Int) {
        viewModelScope.launch {
            _collection.value = collectionRepository.getCollectionById(collectionId)
        }
    }

    fun getCollectionsByTopicId(topicId: Int) {
        viewModelScope.launch {
            _collectionsByTopic.value = collectionRepository.getCollectionsByTopicId(topicId)
        }
    }

    fun createCollection(request: CollectionRequest) {
        viewModelScope.launch {
            _createCollectionResult.value = collectionRepository.createCollection(request)
        }
    }

    fun updateCollection(request: CollectionRequest) {
        viewModelScope.launch {
            _updateCollectionResult.value = collectionRepository.updateCollection(request)
        }
    }

    fun deleteCollection(collectionId: Int) {
        viewModelScope.launch {
            _deleteCollectionResult.value = collectionRepository.deleteCollection(collectionId)
        }
    }

    fun checkCollectionExists(collectionId: Int) {
        viewModelScope.launch {
            _existsCollectionResult.value = collectionRepository.existsById(collectionId)
        }
    }

    fun createCollectionSeen(collectionId: Int) {
        viewModelScope.launch {
            val request = CollectionSeenRequest(
                userId = UserSession.getCurrentUserId(),
                collectionId = collectionId
            )
            _createCollectionSeenResult.value = collectionSeenRepository.createCollectionSeen(request)
        }
    }

    fun getCollectionSeenByUserId(page: Int, limit: Int) {
        viewModelScope.launch {
            _collectionsSeenByUser.value = collectionSeenRepository.getCollectionSeenByUserId(
                userId = UserSession.getCurrentUserId(),
                page = page,
                limit = limit
            )
        }
    }

    fun getCollectionSeenById(id: Int) {
        viewModelScope.launch {
            _collectionSeen.value = collectionSeenRepository.getCollectionSeenById(id)
        }
    }

    fun getAllCollectionSeen(page: Int, limit: Int) {
        viewModelScope.launch {
            _allCollectionsSeen.value = collectionSeenRepository.getAllCollectionSeen(page, limit)
        }
    }

    fun updateCollectionSeen(request: CollectionSeenRequest) {
        viewModelScope.launch {
            _updateCollectionSeenResult.value = collectionSeenRepository.updateCollectionSeen(request)
        }
    }

    fun deleteCollectionSeen(id: Int) {
        viewModelScope.launch {
            _deleteCollectionSeenResult.value = collectionSeenRepository.deleteCollectionSeen(id)
        }
    }

    fun checkCollectionSeenExists(id: Int) {
        viewModelScope.launch {
            _existsCollectionSeenResult.value = collectionSeenRepository.existsById(id)
        }
    }
}