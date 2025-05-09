package com.example.beaceful.core.util

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun formatDiaryDate(date: LocalDateTime): String {
    val today = LocalDate.now()
    val yesterday = today.minusDays(1)
    val dateOnly = date.toLocalDate()

    return when (dateOnly) {
        today -> "Hôm nay, ngày ${date.dayOfMonth}"
        yesterday -> "Hôm qua, ngày ${date.dayOfMonth}"
        else -> "Ngày ${date.dayOfMonth}"
    }
}

fun formatDate (date: LocalDateTime): String {
    return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
}

fun formatDateWithHour (date: LocalDateTime): String {
    return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
}