package com.example.beaceful.ui.screens.diary

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SsidChart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MonotonicFrameClock
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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

    var expanded by remember { mutableStateOf(false) }

    val transition = updateTransition(targetState = expanded, label = "fabTransition")

    val overlayAlpha by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 100) },
        label = "overlayAlpha"
    ) { if (it) 0.45f else 0f }

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
                Button(onClick = { viewModel.goBackCurrentMonth() }) {
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
            AnimatedVisibility(
                visible = viewMode == ViewMode.CHART,
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
                    ChartScreen(currentMonth = currentMonth, navController = navController)
                }
            }
        }

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
            val leftOffset by transition.animateDp(label = "leftOffset") { if (it) (-120).dp else 0.dp }
            val rightOffset by transition.animateDp(label = "rightOffset") { if (it) (120).dp else 0.dp }
            val diagOffset by transition.animateDp(label = "diagOffset") { if (it) (-84).dp else 0.dp }


            if (overlayAlpha > 0f) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = overlayAlpha))
                        .clickable { expanded = false }
                )
            }

            /* Left */
            AnimatedVisibility(
                visible = expanded,
                enter = scaleIn(
                    animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
                ) + fadeIn(
                    animationSpec = tween(durationMillis = 150, easing = FastOutSlowInEasing)
                ),
                exit = scaleOut(
                    animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
                ) +  fadeOut(
                    animationSpec = tween(durationMillis = 400, easing = LinearOutSlowInEasing)
                )

            ) {
                ActionFab(
                    icon = Icons.Default.Add,
                    onClick = {
                        expanded = false;
                        navController.navigate(SelectEmotionDiary.route)
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = leftOffset)
                        .padding(bottom = 18.dp)
                )
            }

            /* Top Left */
            AnimatedVisibility(
                visible = expanded,
                enter = scaleIn(
                    animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
                ) + fadeIn(
                    animationSpec = tween(durationMillis = 150, easing = FastOutSlowInEasing)
                ),
                exit = scaleOut(
                    animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
                ) +  fadeOut(
                    animationSpec = tween(durationMillis = 400, easing = LinearOutSlowInEasing)
                )

            ) {
                ActionFab(
                    icon = Icons.Default.CalendarMonth,
                    onClick = {
                        expanded = false;
                        viewMode = ViewMode.CALENDAR
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(x = -diagOffset, y = diagOffset)
                        .padding(bottom = 18.dp)
                )
            }

            /* Top Right */
            AnimatedVisibility(
                visible = expanded,
                enter = scaleIn(
                    animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
                ) + fadeIn(
                    animationSpec = tween(durationMillis = 150, easing = FastOutSlowInEasing)
                ),
                exit = scaleOut(
                    animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
                ) +  fadeOut(
                    animationSpec = tween(durationMillis = 400, easing = LinearOutSlowInEasing)
                )

            ) {
                ActionFab(
                    icon = Icons.Default.FormatListNumbered,
                    onClick = { expanded = false;
                        viewMode = ViewMode.LIST
                              },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(x = diagOffset, y = diagOffset)
                        .padding(bottom = 18.dp)
                )
            }

            /* Right */
            AnimatedVisibility(
                visible = expanded,
                enter = scaleIn(
                    animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
                ) + fadeIn(
                    animationSpec = tween(durationMillis = 150, easing = FastOutSlowInEasing)
                ),
                exit = scaleOut(
                    animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
                ) +  fadeOut(
                    animationSpec = tween(durationMillis = 400, easing = LinearOutSlowInEasing)
                )

            ) {
                ActionFab(
                    icon = Icons.Default.SsidChart,
                    onClick = { expanded = false;
                              viewMode = ViewMode.CHART},
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(x = rightOffset)
                        .padding(bottom = 18.dp)
                )
            }

            //Main
            FloatingActionButton(
                onClick = {
                    expanded = !expanded
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 18.dp)
                    .border(1.dp, MaterialTheme.colorScheme.onPrimary, RoundedCornerShape(18.dp)),
                containerColor = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(18.dp),
                contentColor = MaterialTheme.colorScheme.onPrimary,
                elevation = FloatingActionButtonDefaults.elevation(),
            ) {
                Icon(
                    if (!expanded) Icons.Default.Star else Icons.Default.StarOutline,
                    contentDescription = "Change"
                )
            }
        }
    }
}

@Composable
private fun ActionFab(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.primary,
        shape = RoundedCornerShape(18.dp),
        contentColor = MaterialTheme.colorScheme.onPrimary,
        elevation = FloatingActionButtonDefaults.elevation()
    ) {
        Icon(icon, contentDescription = null)
    }
}

