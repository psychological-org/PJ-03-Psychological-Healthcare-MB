package com.example.beaceful.ui.components.calendar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.beaceful.domain.model.Diary
import com.example.beaceful.ui.components.cards.DiaryList
import com.example.beaceful.ui.viewmodel.DiaryViewModel
import java.time.LocalDateTime

@Composable
fun CalendarDiaryScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: DiaryViewModel = hiltViewModel(),
    currentMonth: LocalDateTime,
) {
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

                    CustomCalendar(
                        currentMonth = currentMonth,
                        highlightDates = { date -> viewModel.getDiariesOnDate(date).isNotEmpty() },
                        getColorsForDate = { date ->
                            viewModel.getDiariesOnDate(date)
                                .take(2)
                                .map { it.emotion.textColor }
                        },
                        onClickDate = { date -> selectedDiaries = viewModel.getDiariesOnDate(date) }
                    )

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
                    DiaryList(
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
