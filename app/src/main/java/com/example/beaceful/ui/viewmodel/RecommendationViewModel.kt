package com.example.beaceful.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beaceful.core.network.recommended.AnswerResponse
import com.example.beaceful.domain.repository.RecommendationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecommendationViewModel @Inject constructor(
    private val repository: RecommendationRepository
) : ViewModel() {
    private val _recommendation = MutableLiveData<Result<AnswerResponse>>()
    val recommendation: LiveData<Result<AnswerResponse>> get() = _recommendation

    private val _homeRecommendation = MutableStateFlow<String>("")
    val homeRecommendation: StateFlow<String> get() = _homeRecommendation.asStateFlow()

    fun getRecommendation(diaryContent: String?) {
        viewModelScope.launch {
            val result = repository.getRecommendation(diaryContent)
            _recommendation.postValue(result)
        }
    }

    fun getHomeRecommendation(negativityScore: Float) {
        viewModelScope.launch {
            val result = repository.getHomeRecommendation(negativityScore)
            _homeRecommendation.value = result.getOrElse { "Lỗi: ${it.message}" }
        }
    }
}