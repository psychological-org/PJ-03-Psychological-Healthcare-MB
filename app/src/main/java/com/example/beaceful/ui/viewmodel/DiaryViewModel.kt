package com.example.beaceful.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beaceful.core.network.recommended.SerializableEmotion
import com.example.beaceful.domain.model.Appointment
import com.example.beaceful.domain.model.AppointmentStatus
import com.example.beaceful.domain.model.Diary
import com.example.beaceful.domain.model.DumpDataProvider
import com.example.beaceful.domain.model.Emotions
import com.example.beaceful.domain.model.User
import com.example.beaceful.domain.repository.AppointmentRepository
import com.example.beaceful.domain.repository.DiaryRepository
import com.example.beaceful.domain.repository.RecommendationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.ZoneId
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class DiaryViewModel @Inject constructor(
    private val repo: DiaryRepository,
    private val recommendationRepo: RecommendationRepository,
    private val appointmentRepo: AppointmentRepository
) : ViewModel() {
    private val _allDiaries = MutableStateFlow<List<Diary>>(emptyList())
    val allDiaries: StateFlow<List<Diary>> = _allDiaries.asStateFlow()

    private val _diariesForMonth = MutableStateFlow<List<Diary>>(emptyList())
    val diariesForMonth: StateFlow<List<Diary>> = _diariesForMonth.asStateFlow()

    private val _diariesForDay = MutableStateFlow<List<Diary>>(emptyList())
    val diariesForDate: StateFlow<List<Diary>> = _diariesForDay.asStateFlow()

    private val _currentMonth = MutableStateFlow(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")).withDayOfMonth(1))
    val currentMonth: StateFlow<LocalDateTime> = _currentMonth.asStateFlow()

    init {
        loadDiaries()
    }

    private fun loadDiaries() {
        viewModelScope.launch {
            try {
                val diaries = repo.getAllDiaries()
                Log.d("DiaryViewModel", "Loaded diaries: ${diaries.size}")
                // Sắp xếp theo thời gian tạo mới nhất
                _allDiaries.value = diaries.sortedByDescending { it.createdAt }
            } catch (e: Exception) {
                Log.e("DiaryViewModel", "Error loading diaries: ${e.message}")
            } finally {
            }
        }
    }

    fun refreshDiaries() {
        Log.d("DiaryViewModel", "Refreshing diaries...")
        loadDiaries()
    }

    fun onDiaryCreated() {
        refreshDiaries()
        loadDiariesForMonth(_currentMonth.value)
        loadDiariesForDate(_currentMonth.value)
    }

    fun onDiaryUpdated() {
        refreshDiaries()
        loadDiariesForMonth(_currentMonth.value)
        loadDiariesForDate(_currentMonth.value)
    }

    fun saveDiary(
        emotion: Emotions,
        title: String = "No title",
        content: String? = null,
        imageUrl: String? = null,
        voiceUrl: String? = null,
        posterId: String,
        createdAt: LocalDateTime = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"))
    ) {
        viewModelScope.launch {
            val newId = (_allDiaries.value.maxOfOrNull { it.id } ?: 0) + 1
            val diary = Diary(
                id = newId,
                emotion = emotion,
                title = title,
                content = content,
                imageUrl = imageUrl,
                voiceUrl = voiceUrl,
                posterId = posterId,
                createdAt = createdAt
            )
            repo.saveDiary(diary)
            if (!content.isNullOrBlank()) {
                val result = recommendationRepo.getRecommendation(content)
                result.onSuccess { response ->
                    Log.d("DiaryViewModel", "Emotions: ${response.emotions}")
                    repo.updateDiary(
                        diary.copy(
                            emotions = response.emotions.map {
                                SerializableEmotion(it.label, it.score)
                            },
                            negativityScore = response.negativityScore
                        )
                    )
                }.onFailure { e ->
                    Log.e("DiaryViewModel", "Recommendation error: ${e.message}")
                }
            }
            loadDiaries()
            loadDiariesForMonth(_currentMonth.value)
            loadDiariesForDate(_currentMonth.value)
        }
    }

    fun updateDiary(
        id: Int,
        content: String?,
        imageUrl: String?,
        voiceUrl: String?
    ) {
        viewModelScope.launch {
            val currentDiary = repo.getDiaryById(id) ?: return@launch
            val updatedDiary = currentDiary.copy(
                content = content,
                imageUrl = imageUrl,
                voiceUrl = voiceUrl
            )
            repo.updateDiary(updatedDiary)
            if (!content.isNullOrBlank()) {
                val result = recommendationRepo.getRecommendation(content)
                result.onSuccess { response ->
                    repo.updateDiary(
                        updatedDiary.copy(
                            emotions = response.emotions.map {
                                SerializableEmotion(it.label, it.score)
                            },
                            negativityScore = response.negativityScore
                        )
                    )
                }.onFailure { e ->
                    Log.e("DiaryViewModel", "Recommendation error: ${e.message}")
                }
            }
            loadDiaries()
            loadDiariesForMonth(_currentMonth.value)
            loadDiariesForDate(_currentMonth.value)
        }
    }

    fun getDiary(id: Int): Diary? {
        return runBlocking(Dispatchers.IO) { repo.getDiaryById(id) }
    }

    fun goToPreviousMonth() {
        _currentMonth.update { it.minusMonths(1).withDayOfMonth(1) }
        loadDiariesForMonth(_currentMonth.value)
    }

    fun goToNextMonth() {
        _currentMonth.update { it.plusMonths(1).withDayOfMonth(1) }
        loadDiariesForMonth(_currentMonth.value)
    }

    fun goBackCurrentMonth() {
        _currentMonth.update { LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")).withDayOfMonth(1) }
        loadDiariesForMonth(_currentMonth.value)
    }

    fun loadDiariesForMonth(month: LocalDateTime) {
        viewModelScope.launch {
            _diariesForMonth.value = repo.getDiariesInMonth(month)
        }
    }

    fun loadDiariesForDate(date: LocalDateTime) {
        viewModelScope.launch {
            _diariesForDay.value = repo.getDiariesOnDate(date)
        }
    }

    fun deleteDiary(diaryId: Int) {
        viewModelScope.launch {
            repo.deleteDiary(diaryId)
            loadDiaries()
            loadDiariesForMonth(_currentMonth.value)
            loadDiariesForDate(_currentMonth.value)
        }
    }

    suspend fun moodCount(month: LocalDateTime): Map<Emotions, Int> {
        return repo.getDiariesInMonth(month).groupingBy { it.emotion }.eachCount()
            .let { count -> Emotions.entries.associateWith { count[it] ?: 0 } }
    }

    fun getDiariesOnDate(date: LocalDate): List<Diary> {
        return repo.getDiariesOnDate(date)
    }

    suspend fun getUpcoming(userId: String): List<Appointment> {
        return appointmentRepo.getAllAppointmentsOfPatient(userId)
            .filter { it.appointmentDate.isAfter(LocalDateTime.now()) }
            .sortedBy { it.appointmentDate }
    }

    suspend fun getDoctorByAppointment(appointment: Appointment): User? {
        return appointmentRepo.getUserById(appointment.doctorId)
    }
}