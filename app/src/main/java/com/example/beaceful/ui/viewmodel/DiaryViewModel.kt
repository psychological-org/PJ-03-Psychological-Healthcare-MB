package com.example.beaceful.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beaceful.domain.model.Appointment
import com.example.beaceful.domain.model.AppointmentStatus
import com.example.beaceful.domain.model.Diary
import com.example.beaceful.domain.model.DumpDataProvider
import com.example.beaceful.domain.model.Emotions
import com.example.beaceful.domain.repository.DiaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.ZoneId
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class DiaryViewModel @Inject constructor(
    val repo: DiaryRepository,
) : ViewModel() {
    private val _allDiaries = MutableStateFlow<List<Diary>>(emptyList())
    val allDiaries: StateFlow<List<Diary>> = _allDiaries.asStateFlow()

    init {
        println("DiaryViewModel initialized")
        loadDiaries()
    }

    private fun loadDiaries() {
        viewModelScope.launch {
            println("Loading diaries...")
            _allDiaries.value = repo.getAllDiaries()
            println("Diaries loaded: ${_allDiaries.value}")
        }
    }

    private val _currentMonth = MutableStateFlow(LocalDateTime.now().withDayOfMonth(1))
    val currentMonth: StateFlow<LocalDateTime> = _currentMonth
    fun getDiary(id: Int): Diary? = repo.getDiaryById(id)

//    private val _currentMonth = MutableStateFlow(LocalDate.now().withDayOfMonth(1))
//    val currentMonth: StateFlow<LocalDate> = _currentMonth.asStateFlow()

    fun goToPreviousMonth() {
        _currentMonth.update { it.minusMonths(1).withDayOfMonth(1) }
    }

    fun goToNextMonth() {
        _currentMonth.update { it.plusMonths(1).withDayOfMonth(1) }
    }

    fun moodCount(month: LocalDateTime): Map<Emotions, Int> {
        val count = repo.getDiariesInMonth(month).groupingBy { it.emotion }.eachCount()
        return Emotions.entries.associateWith { count[it] ?: 0 }
    }

//    fun getAppointments(userId: Int): List<Appointment> =
//        DumpDataProvider.appointments.filter { it.patientId == userId }

    fun getDiaryById(id: Int): Diary? {
        return repo.getDiaryById(id)
    }

    fun getAppointmentsOnDate(userId: String, date: LocalDateTime): List<Appointment> =
        DumpDataProvider.appointments.filter { it.patientId == userId && it.appointmentDate == date }

    fun getUpcoming(userId: String): List<Appointment> = getAppointmentsOnDate(
        userId = userId,
        date = LocalDateTime.now()
    ).filter { it.status == AppointmentStatus.CONFIRMED }.sortedBy { it.appointmentDate }

    fun saveDiary(
        emotion: Emotions,
        title: String = "No title",
        content: String? = null,
        imageUrl: String? = null,
        voiceUrl: String? = null,
        posterId: String,
        createAt: LocalDateTime = LocalDateTime.now(ZoneId.of("UTC+7"))
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
                createdAt = createAt,
            )
            repo.saveDiary(diary)
            loadDiaries()
        }
    }

    fun updateDiary(
        id: Int,
        content: String?,
        imageUrl: String?,
        voiceUrl: String?
    ) {
        viewModelScope.launch {
            val currentDiary = repo.getDiaryById(id)
            if (currentDiary != null) {
                val updatedDiary = currentDiary.copy(
                    content = content,
                    imageUrl = imageUrl,
                    voiceUrl = voiceUrl,
                )
                repo.updateDiary(updatedDiary)
                _allDiaries.value = repo.getAllDiaries()
            }
        }
    }

    private val _diariesForMonth = MutableStateFlow<List<Diary>>(emptyList())
    val diariesForMonth: StateFlow<List<Diary>> = _diariesForMonth.asStateFlow()

    fun loadDiariesForMonth(month: LocalDateTime) {
        viewModelScope.launch {
            val result = repo.getDiariesInMonth(month)
            _diariesForMonth.value = result
        }
    }

    private val _diariesForDay = MutableStateFlow<List<Diary>>(emptyList())
    val diariesForDate: StateFlow<List<Diary>> = _diariesForDay.asStateFlow()

    fun loadDiariesForDate(date: LocalDateTime) {
        viewModelScope.launch {
            val result = repo.getDiariesOnDate(date.toLocalDate())
            _diariesForDay.value = result
        }
    }

    fun deleteDiary(diaryId: Int) {
        viewModelScope.launch {
            repo.deleteDiary(diaryId)
            loadDiariesForMonth(currentMonth.value)
            loadDiariesForDate(currentMonth.value)
            loadDiaries()
        }
    }
    fun getDoctorByAppointment(appointment: Appointment) = repo.getUserById(appointment.doctorId)
}