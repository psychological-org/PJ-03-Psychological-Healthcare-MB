package com.example.beaceful.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.beaceful.domain.model.Diary
import com.example.beaceful.domain.repository.DiaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class DiaryViewModel @Inject constructor(
    private val repo: DiaryRepository
) : ViewModel() {

    val allDiaries: List<Diary> = repo.getAllDiaries()

    fun getDiary(id: Int): Diary? = repo.getDiaryById(id)

    fun getWeeklyDiaries(baseDate: LocalDate): List<Diary> =
        repo.getDiariesInWeek(baseDate)

    fun getDiariesOnDate(date: LocalDate): List<Diary> =
        repo.getDiariesOnDate(date)
}