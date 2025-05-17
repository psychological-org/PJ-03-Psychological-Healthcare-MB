package com.example.beaceful.domain.repository

import com.example.beaceful.domain.model.Diary
import com.example.beaceful.domain.model.DumpDataProvider
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DiaryRepository @Inject constructor() {

    fun getAllDiaries(): List<Diary> = DumpDataProvider.diaries

    fun getDiaryById(id: Int): Diary? = DumpDataProvider.diaries.find { it.id == id }

    fun getDiariesInWeek(date: LocalDate): List<Diary> {
        val monday = date.with(java.time.DayOfWeek.MONDAY)
        val sunday = monday.plusDays(6)
        return DumpDataProvider.diaries.filter {
            val day = it.createdAt.toLocalDate()
            day in monday..sunday
        }
    }

    fun getDiariesOnDate(date: LocalDate): List<Diary> =
        DumpDataProvider.diaries.filter { it.createdAt.toLocalDate() == date }
}