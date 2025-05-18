package com.example.beaceful.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.beaceful.domain.model.Diary
import com.example.beaceful.domain.repository.DiaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class DiaryViewModel @Inject constructor(
    private val repo: DiaryRepository
) : ViewModel() {

    val allDiaries: List<Diary> = repo.getAllDiaries()

    fun getDiary(id: Int): Diary? = repo.getDiaryById(id)

    private val _currentMonth = MutableStateFlow(LocalDate.now().withDayOfMonth(1))
    val currentMonth: StateFlow<LocalDate> = _currentMonth

    fun goToPreviousMonth() {
        _currentMonth.update { it.minusMonths(1).withDayOfMonth(1) }
    }

    fun goToNextMonth() {
        _currentMonth.update { it.plusMonths(1).withDayOfMonth(1) }
    }

    fun setMonth(month: LocalDate) {
        _currentMonth.value = month.withDayOfMonth(1)
    }

    fun getDiariesInCurrentMonth(): List<Diary> {
        val month = currentMonth.value
        return repo.getDiariesInMonth(month)
    }

    fun getDiariesInMonth(baseDate: LocalDate): List<Diary> =
        repo.getDiariesInMonth(baseDate)

    fun getDiariesInWeek(baseDate: LocalDate): List<Diary> =
        repo.getDiariesInWeek(baseDate)

    fun getDiariesOnDate(date: LocalDate): List<Diary> =
        repo.getDiariesOnDate(date)
}