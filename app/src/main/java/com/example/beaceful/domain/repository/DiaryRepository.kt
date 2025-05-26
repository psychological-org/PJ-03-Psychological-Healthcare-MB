package com.example.beaceful.domain.repository

import com.example.beaceful.domain.model.Diary
import com.example.beaceful.domain.model.DumpDataProvider
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DiaryRepository @Inject constructor() {

    fun getAllDiaries(): List<Diary> = DumpDataProvider.diaries

    fun getDiaryById(id: Int): Diary? = DumpDataProvider.diaries.find { it.id == id }

    fun getDiariesInMonth(date: LocalDateTime): List<Diary> {
        val year = date.year
        val month = date.month
        return DumpDataProvider.diaries.filter {
            it.createdAt.year == year && it.createdAt.month == month
        }.sortedByDescending { it.createdAt }
    }

    fun getDiariesInWeek(date: LocalDateTime): List<Diary> {
        val monday = date.with(java.time.DayOfWeek.MONDAY)
        val sunday = monday.plusDays(6)
        return DumpDataProvider.diaries.filter {
            val day = it.createdAt
            day in monday..sunday
        }
    }

    fun getDiariesOnDate(date: LocalDateTime): List<Diary> =
        DumpDataProvider.diaries.filter { it.createdAt.toLocalDate() == date.toLocalDate() }
}