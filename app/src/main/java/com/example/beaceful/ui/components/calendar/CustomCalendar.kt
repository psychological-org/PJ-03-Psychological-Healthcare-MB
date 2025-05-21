package com.example.beaceful.ui.components.calendar

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.LocalDateTime

@Composable
fun CustomCalendar(
    currentMonth: LocalDateTime,
    getColorsForDate: (LocalDateTime) -> List<Color>,
    highlightDates: (LocalDateTime) -> Boolean,
    onClickDate: (LocalDateTime) -> Unit
) {
    val daysInMonth = currentMonth.toLocalDate().lengthOfMonth()
    val startDayOfWeek = currentMonth.dayOfWeek.value % 7
    Column {
        // Weekday headers
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            listOf("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su").forEach {
                Text(
                    text = it,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // Calendar grid
        val totalCells = daysInMonth + startDayOfWeek
        val rows = (totalCells / 7) + if (totalCells % 7 != 0) 1 else 0
        Column {
            repeat(rows) { row ->
                Row(Modifier.fillMaxWidth()) {
                    (0..6).forEach { col ->
                        val dayIndex = row * 7 + col
                        val dayNumber = dayIndex - startDayOfWeek + 1
                        if (dayIndex >= startDayOfWeek && dayNumber <= daysInMonth) {
                            val date = currentMonth.withDayOfMonth(dayNumber)
                            val colors = getColorsForDate(date)
                            val isHighlighted = highlightDates(date)

                            if (isHighlighted) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .padding(2.dp)
                                        .clip(CircleShape)
                                        .clickable(onClick = { onClickDate(date) })
                                        .then(
                                            if (date.toLocalDate() == LocalDate.now())
                                                Modifier.border(
                                                    width = 2.dp,
                                                    color = MaterialTheme.colorScheme.secondary,
                                                    shape = CircleShape
                                                )
                                            else Modifier),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Canvas(modifier = Modifier.size(36.dp)) {
                                        drawCircle(
                                            color = colors[0],
                                            radius = size.minDimension / 2
                                        )
                                        if (colors.size > 1) {
                                            drawArc(
                                                color = colors[1],
                                                startAngle = -90f,
                                                sweepAngle = 180f,
                                                useCenter = true
                                            )
                                        }
                                    }

                                    Text(
                                        "$dayNumber",
                                        color = MaterialTheme.colorScheme.primary,
                                        style = MaterialTheme.typography.labelMedium,
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .then(
                                            if (date.toLocalDate() == LocalDate.now())
                                                Modifier.border(
                                                    width = 2.dp,
                                                    color = MaterialTheme.colorScheme.secondary,
                                                    shape = CircleShape
                                                )
                                            else Modifier),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("$dayNumber", textAlign = TextAlign.Center)
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                            ) { }
                        }
                    }
                }
            }
        }
    }
}