package com.example.beaceful.core.util

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

fun formatMessageTime(createdAt: LocalDateTime): String {
    val today = LocalDate.now()
    val messageDate = createdAt.toLocalDate()
    val daysDifference = ChronoUnit.DAYS.between(messageDate, today).toInt()

    return when {
        daysDifference == 0 -> {
            // Hôm nay, hiển thị giờ
            createdAt.format(DateTimeFormatter.ofPattern("HH:mm"))
        }
        daysDifference == 1 -> {
            // Hôm qua
            "Hôm qua"
        }
        daysDifference < 7 -> {
            // Trong tuần này, hiển thị thứ
            createdAt.format(
                DateTimeFormatter.ofPattern("EEEE", Locale("vi"))
            ).replaceFirstChar { it.uppercase() } // Ví dụ: Thứ Hai
        }
        else -> {
            // Trước đó, hiển thị ngày
            createdAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        }
    }
}

// Hàm cho timestamp (nếu cần)
fun formatMessageTime(timestamp: Long): String {
    val createdAt = Instant.ofEpochMilli(timestamp)
        .atZone(ZoneId.of("Asia/Ho_Chi_Minh"))
        .toLocalDateTime()
    return formatMessageTime(createdAt)
}