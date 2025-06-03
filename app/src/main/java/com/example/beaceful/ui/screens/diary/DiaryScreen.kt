package com.example.beaceful.ui.screens.diary

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.beaceful.domain.model.ViewMode
import com.example.beaceful.ui.components.calendar.CalendarDiaryScreen
import com.example.beaceful.ui.components.cards.DiaryList
import com.example.beaceful.ui.navigation.SelectEmotionDiary
import com.example.beaceful.ui.viewmodel.DiaryViewModel

@Composable
fun DiaryScreen(
    navController: NavHostController,
    viewModel: DiaryViewModel = hiltViewModel()
) {
    var viewMode by remember { mutableStateOf(ViewMode.LIST) }
    val currentMonth by viewModel.currentMonth.collectAsState()
    val diariesForMonth by viewModel.diariesForMonth.collectAsState()

    Box {
        Column(modifier = Modifier.fillMaxHeight()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
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
            Spacer(Modifier.height(6.dp))

            AnimatedVisibility(
                visible = viewMode == ViewMode.LIST,
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(initialAlpha = 0.3f),
                exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
            ) {
                AnimatedContent(
                    targetState = currentMonth,
                    transitionSpec = {
                        if (targetState > initialState) {
                            (slideInHorizontally { it } + fadeIn()).togetherWith(
                                slideOutHorizontally { -it } + fadeOut())
                        } else {
                            (slideInHorizontally { -it } + fadeIn()).togetherWith(
                                slideOutHorizontally { it } + fadeOut())
                        }
                    }
                ) { month ->
                    LaunchedEffect(currentMonth) {
                        viewModel.loadDiariesForMonth(month)
                    }
                    DiaryList(
//                        diaries = viewModel.repo.getDiariesInMonth(month),
                        diaries = diariesForMonth,
                        navController = navController,
                        onDeleteDiary = { diary -> viewModel.deleteDiary(diary.id) }
                    )
                }
            }

            AnimatedVisibility(
                visible = viewMode == ViewMode.CALENDAR,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(initialAlpha = 0.3f),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                AnimatedContent(
                    targetState = currentMonth,
                    transitionSpec = {
                        if (targetState > initialState) {
                            (slideInHorizontally { it } + fadeIn()).togetherWith(
                                slideOutHorizontally { -it } + fadeOut())
                        } else {
                            (slideInHorizontally { -it } + fadeIn()).togetherWith(
                                slideOutHorizontally { it } + fadeOut())
                        }
                    }
                )
                { currentMonth ->
                    CalendarDiaryScreen(currentMonth = currentMonth, navController = navController)
                }
            }
        }

        FloatingActionButton(
            onClick = { navController.navigate(SelectEmotionDiary.route) },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = 18.dp, start = 36.dp)
                .border(1.dp, MaterialTheme.colorScheme.onPrimary, RoundedCornerShape(18.dp)),
            containerColor = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(18.dp),
            contentColor = MaterialTheme.colorScheme.onPrimary,
            elevation = FloatingActionButtonDefaults.elevation(),
        )
        {
            Icon(Icons.Default.Add, contentDescription = null)
        }
        FloatingActionButton(
            onClick = {
                viewMode = if (viewMode == ViewMode.LIST) ViewMode.CALENDAR else ViewMode.LIST
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 18.dp, end = 36.dp)
                .border(1.dp, MaterialTheme.colorScheme.onPrimary, RoundedCornerShape(18.dp)),
            containerColor = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(18.dp),
            contentColor = MaterialTheme.colorScheme.onPrimary,
            elevation = FloatingActionButtonDefaults.elevation(),
        ) {
            Icon(
                if (viewMode == ViewMode.LIST) Icons.Default.CalendarMonth else Icons.Default.FormatListNumbered,
                contentDescription = "Change"
            )
        }
    }
}

