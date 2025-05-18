package com.example.beaceful.ui.components.calendar

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.beaceful.ui.navigation.DiaryCalendar
import com.example.beaceful.ui.navigation.DiaryRoute
import com.example.beaceful.ui.viewmodel.DiaryViewModel
import java.time.LocalDate

@Composable
fun CalendarDiaryScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: DiaryViewModel = hiltViewModel()
) {

    val backStackEntry = navController.previousBackStackEntry
    val selectedMonth = backStackEntry
        ?.savedStateHandle
        ?.get<LocalDate>("selectedMonth")

    LaunchedEffect(selectedMonth) {
        selectedMonth?.let { viewModel.setMonth(it) }
    }

    val currentMonth by viewModel.currentMonth.collectAsState()
//    val currentMonth = selectedMonth ?: currentMonthState

    val daysInMonth = currentMonth.lengthOfMonth()
    val startDayOfWeek = currentMonth.dayOfWeek.value % 7
    Box {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { viewModel.goToPreviousMonth() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous")
                }
                Button(onClick = {}) {
                    Text(
                        text = "${
                            currentMonth.month.name.lowercase().replaceFirstChar { it.uppercase() }
                        } ${currentMonth.year}",
                        style = MaterialTheme.typography.titleLarge,
                    )
                }
                IconButton(onClick = { viewModel.goToNextMonth() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next")
                }
            }

            Text(
                text = "Xem đặt lịch / xem tâm trạng",
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(24.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(16.dp))

            // Weekday headers
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                listOf("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su").forEach {
                    Text(text = it, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
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
                                val diariesOnDay = viewModel.getDiariesOnDate(date)

                                val colors = diariesOnDay.take(2).map { it.emotion.backgroundColor }


                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .padding(2.dp)
                                        .clip(CircleShape)
                                        .clickable {
                                            val weeklyDiaries = viewModel.getDiariesInWeek(date)
                                            navController.currentBackStackEntry
                                                ?.savedStateHandle
                                                ?.set("weeklyDiaries", weeklyDiaries)
                                            navController.navigate(DiaryRoute.route)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (colors.isEmpty()) {
                                        Text("$dayNumber", textAlign = TextAlign.Center)
                                    } else {
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
                                }
                            } else {
                                Box(modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)) { }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Mood count / các lịch sắp tới",
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(24.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(
                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f),
                        RoundedCornerShape(24.dp)
                    )
            ) {
                // TODO: thống kê cảm xúc / lịch
            }

        }
        FloatingActionButton(
            onClick = {navController.popBackStack()},
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 18.dp, end = 36.dp)
                .border(1.dp, MaterialTheme.colorScheme.onPrimary,RoundedCornerShape(18.dp)),
            containerColor = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(18.dp),
            contentColor = MaterialTheme.colorScheme.onPrimary,
            elevation = FloatingActionButtonDefaults.elevation(),
        ) {
            Icon(Icons.Default.FormatListNumbered, contentDescription = "Change")
        }
    }
}

