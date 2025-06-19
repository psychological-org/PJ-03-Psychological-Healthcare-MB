package com.example.beaceful.ui.components.calendar

import android.graphics.Color
import android.graphics.Paint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.beaceful.core.util.UserSession
import com.example.beaceful.core.util.formatAppointmentDate
import com.example.beaceful.domain.model.Appointment
import com.example.beaceful.domain.model.Diary
import com.example.beaceful.domain.model.Emotions
import com.example.beaceful.domain.model.User
import com.example.beaceful.ui.components.cards.DiaryList
import com.example.beaceful.ui.viewmodel.DiaryViewModel
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarDiaryScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: DiaryViewModel = hiltViewModel(),
    currentMonth: LocalDateTime,
) {
    var userId = UserSession.getCurrentUserId()
    var selectedDiaries by remember { mutableStateOf<List<Diary>?>(null) }
    var selectedIndex by remember { mutableIntStateOf(0) }
    val options = listOf("Mood count", "Lịch sắp tới")
    val diariesForDate by viewModel.diariesForDate.collectAsState()
    var moodCount by remember { mutableStateOf<Map<Emotions, Int>>(emptyMap()) }
    var nextAppointments by remember { mutableStateOf<List<Appointment>>(emptyList()) }
    var doctors by remember { mutableStateOf<Map<Appointment, User?>>(emptyMap()) }

    // Gọi các hàm suspend trong coroutine
    LaunchedEffect(currentMonth, selectedIndex) {
        moodCount = viewModel.moodCount(currentMonth)
        if (selectedIndex == 1) {
            nextAppointments = viewModel.getUpcoming(userId)
            doctors = nextAppointments.associateWith { viewModel.getDoctorByAppointment(it) }
        }
    }

    Box {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp)
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(MaterialTheme.colorScheme.primary),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
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
            }
            item {
                CustomCalendar(
                    currentMonth = currentMonth,
                    highlightDates = { date -> viewModel.getDiariesOnDate(date.toLocalDate()).isNotEmpty() },
                    getColorsForDate = { date ->
                        viewModel.getDiariesOnDate(date.toLocalDate())
                            .take(2)
                            .map { it.emotion.textColor }
                    },
                    onClickDate = { date -> selectedDiaries = viewModel.getDiariesOnDate(date.toLocalDate()) }
                )
                Spacer(Modifier.height(8.dp))

            }

            item {
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    options.forEachIndexed { index, label ->
                        SegmentedButton(
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = options.size
                            ),
                            onClick = { selectedIndex = index },
                            selected = index == selectedIndex,
                            label = { Text(label, style = MaterialTheme.typography.titleSmall) },
                            icon = {},
                            colors = SegmentedButtonDefaults.colors(
                                activeBorderColor = MaterialTheme.colorScheme.onPrimary,
                                activeContainerColor = MaterialTheme.colorScheme.onPrimary,
                                activeContentColor = MaterialTheme.colorScheme.primary,
                                inactiveContainerColor = androidx.compose.ui.graphics.Color.Gray,
                                inactiveContentColor = androidx.compose.ui.graphics.Color.DarkGray,
                                inactiveBorderColor = androidx.compose.ui.graphics.Color.Gray,
                            )
                        )
                    }
                }
            }

            item {

                AnimatedContent(
                    targetState = selectedIndex,
                    transitionSpec = {
                        if (targetState > initialState) {
                            (slideInHorizontally { it } + fadeIn()).togetherWith(
                                slideOutHorizontally { -it } + fadeOut())
                        } else {
                            (slideInHorizontally { -it } + fadeIn()).togetherWith(
                                slideOutHorizontally { it } + fadeOut())
                        }
                    }
                ) { selectedIndex ->
                    when (selectedIndex) {
                        0 -> Box(
                            modifier = Modifier.background(
                                MaterialTheme.colorScheme.tertiary,
                                RoundedCornerShape(24.dp)
                            ).fillMaxWidth()
                        ) { MoodBarChart(moodCount) }

                        1 -> Box(
                            modifier = Modifier.background(
                                MaterialTheme.colorScheme.tertiary,
                                RoundedCornerShape(24.dp)
                            ).fillMaxWidth().padding(horizontal = 24.dp, vertical = 12.dp)
                        ) {
                            if (nextAppointments.isEmpty()) {
                                Text(text = "Trống", textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onTertiary)
                            } else {
                                nextAppointments.forEach { appointment ->
                                    val doctor = doctors[appointment]
                                    if (doctor != null) {
                                        Column {
                                            Text(text = formatAppointmentDate(appointment.appointmentDate), color = MaterialTheme.colorScheme.onTertiary)
                                            Text(text = "Bs. ${doctor.fullName}", color = MaterialTheme.colorScheme.onTertiary)
                                            HorizontalDivider(
                                                color = MaterialTheme.colorScheme.secondary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                }
            }

            item {
                Spacer(Modifier.height(80.dp))
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
            Box {
                DiaryList(
                    diaries = it,
                    navController = navController,
                    onDeleteDiary = { diary -> viewModel.deleteDiary(diary.id) }
                )
                FloatingActionButton(
                    onClick = { selectedDiaries = null },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 18.dp)
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.onPrimary,
                            RoundedCornerShape(18.dp)
                        ),
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


@Composable
fun MoodBarChart(
    moodDistribution: Map<Emotions, Int>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val maxCount = (moodDistribution.values.maxOrNull() ?: 1).toFloat()
    val barWidth = 40.dp
    val spacing = 20.dp

    val emotions = moodDistribution.keys.toList()
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(
                top = 24.dp,
                start = 16.dp,
                bottom = 36.dp,
                end = 16.dp
            )
    ) {
        val barSpacePx = barWidth.toPx() + spacing.toPx()
        val chartHeight = size.height
//        val chartWidth = size.width

        emotions.forEachIndexed { index, emotion ->
            val count = moodDistribution[emotion] ?: 0
            val barHeight = (count / maxCount) * chartHeight

            val left = index * barSpacePx + spacing.toPx() / 2
            val top = chartHeight - barHeight

            drawRect(
                color = emotion.textColor,
                topLeft = Offset(left, top),
                size = Size(barWidth.toPx(), barHeight)
            )

            drawContext.canvas.nativeCanvas.drawText(
                "$count",
                left + barWidth.toPx() / 2,
                top - 12f,
                Paint().apply {
                    color = Color.BLACK
                    textSize = 32f
                    textAlign = Paint.Align.CENTER
                    isAntiAlias = true
                }
            )
            val drawable = ContextCompat.getDrawable(context, emotion.iconRes)
            val bmp = drawable?.toBitmap(80, 80)
            bmp?.let {
                drawImage(
                    it.asImageBitmap(),
                    topLeft = Offset(
                        left + (barWidth.toPx() - it.width) / 2,
                        chartHeight + 8f
                    )
                )
            }
        }
    }
}
