package com.example.beaceful.ui.components.calendar

import android.util.Log
import android.widget.CalendarView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.beaceful.domain.model.Diary
import com.example.beaceful.ui.components.cards.DiaryCard
import com.example.beaceful.ui.navigation.DiaryCalendar
import com.example.beaceful.ui.navigation.DiaryDetails
import com.example.beaceful.ui.navigation.DiaryRoute
import com.example.beaceful.ui.navigation.SelectEmotionDiary
import com.example.beaceful.ui.screens.diary.DiaryListScreen
import com.example.beaceful.ui.viewmodel.DiaryViewModel
import java.time.LocalDate

@Composable
fun CalendarDiaryScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: DiaryViewModel = hiltViewModel(),
    currentMonth: LocalDate,
) {
    val daysInMonth = currentMonth.lengthOfMonth()
    val startDayOfWeek = currentMonth.dayOfWeek.value % 7
    var selectedDiaries by remember { mutableStateOf<List<Diary>?>(null) }

    Box {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                modifier = Modifier.fillMaxSize(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Lịch của bạn",
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.onPrimary,
                                RoundedCornerShape(24.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(Modifier.height(16.dp))

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
                                        val diariesOnDay = viewModel.getDiariesOnDate(date)

                                        val colors =
                                            diariesOnDay.take(2).map { it.emotion.textColor }
                                        if (diariesOnDay.isNotEmpty()) {
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .aspectRatio(1f)
                                                    .padding(2.dp)
                                                    .clip(CircleShape)
                                                    .clickable {
                                                        selectedDiaries = diariesOnDay
                                                    },
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
                                                    .aspectRatio(1f),
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


                    Spacer(Modifier.height(24.dp))

                    Text(
                        text = "Mood count / các lịch sắp tới",
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.onPrimary,
                                RoundedCornerShape(24.dp)
                            )
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
            }

        }
        AnimatedVisibility(
            visible = selectedDiaries != null,
            enter = fadeIn(animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(300)),
            modifier = Modifier
                .fillMaxSize()
                .background(
                    MaterialTheme.colorScheme.background.copy(alpha = 0.95f)
                )
        ) {

            selectedDiaries?.let {
                Box{
                    DiaryListScreen(
                        diaries = it,
                        navController = navController
                    )
                    FloatingActionButton(
                        onClick = {selectedDiaries = null },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 18.dp)
                            .border(1.dp, MaterialTheme.colorScheme.onPrimary, RoundedCornerShape(18.dp)),
                        containerColor = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(18.dp),
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        elevation = FloatingActionButtonDefaults.elevation(),
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "")
                    }
                }
            }
        }
    }
}
