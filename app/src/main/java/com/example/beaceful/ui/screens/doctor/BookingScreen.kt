package com.example.beaceful.ui.screens.doctor

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.beaceful.R
import com.example.beaceful.core.util.formatAppointmentDate
import com.example.beaceful.domain.model.Diary
import com.example.beaceful.domain.model.User
import com.example.beaceful.ui.components.calendar.CustomCalendar
import com.example.beaceful.ui.viewmodel.BookingViewModel
import java.time.LocalDate
import java.time.LocalDateTime

@Composable
fun BookingScreen(
    doctorId: Int,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: BookingViewModel = hiltViewModel(),
) {
    var selectedDate by remember { mutableStateOf<LocalDate>(LocalDate.now()) }
    val currentMonth by viewModel.currentMonth.collectAsState()
    var selectedAppointmentTime by remember { mutableStateOf<LocalDateTime?>(null) }

    Box {
        Column {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(Modifier.height(6.dp))
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
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${
                                    currentMonth.month.name.lowercase()
                                        .replaceFirstChar { it.uppercase() }
                                } ${currentMonth.year}",
                                modifier = Modifier
                                    .background(
                                        MaterialTheme.colorScheme.onPrimary,
                                        RoundedCornerShape(24.dp)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Row {
                                IconButton(onClick = { viewModel.goToPreviousMonth() }) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Previous"
                                    )
                                }
                                IconButton(onClick = { viewModel.goToNextMonth() }) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = "Next"
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(16.dp))
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
                        ) { currentMonth ->
                            CustomCalendar(
                                currentMonth = currentMonth,
                                highlightDates = { date -> date.toLocalDate() == selectedDate },
                                getColorsForDate = { date ->
                                    if (date.toLocalDate() == selectedDate) {
                                        listOf(Color(0xFFB089CA))
                                    } else {
                                        emptyList()
                                    }
                                },
                                onClickDate = { date ->
                                    selectedDate = date.toLocalDate()
                                    selectedAppointmentTime = null
                                },
                                isBookingMode = true
                            )
                        }

                        Spacer(Modifier.height(24.dp))

                        Text(
                            text = stringResource(R.string.bo2),
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.onPrimary,
                                    RoundedCornerShape(24.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(Modifier.height(12.dp))
//                        Pick time
                        Box(
                            modifier = Modifier.background(
                                MaterialTheme.colorScheme.tertiary,
                                RoundedCornerShape(24.dp)
                            )
                        ) {
                            AnimatedContent(
                                targetState = selectedDate,
                                transitionSpec = {
                                    if (targetState > initialState) {
                                        (slideInHorizontally { it } + fadeIn()).togetherWith(
                                            slideOutHorizontally { -it } + fadeOut())
                                    } else {
                                        (slideInHorizontally { -it } + fadeIn()).togetherWith(
                                            slideOutHorizontally { it } + fadeOut())
                                    }
                                }
                            ) { selectedDate ->
                                val bookedSlots: List<LocalDateTime> =
                                    viewModel.getBookedAppointments(doctorId, selectedDate)
                                val timeSlots =
                                    viewModel.generateTimeSlots(selectedDate, bookedSlots)

                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    contentPadding = PaddingValues(
                                        horizontal = 12.dp,
                                        vertical = 6.dp
                                    )
                                ) {
                                    items(timeSlots.chunked(2)) { rowSlots ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            rowSlots.forEach { slot ->
                                                val isSelected =
                                                    selectedAppointmentTime == slot.time
                                                val isDisabled = slot.isBooked

                                                Button(
                                                    onClick = {
                                                        if (!isDisabled) selectedAppointmentTime =
                                                            slot.time
                                                    },
                                                    enabled = !isDisabled,
                                                    shape = RoundedCornerShape(24.dp),
                                                    colors = when {
                                                        isSelected -> ButtonDefaults.buttonColors(
                                                            containerColor = MaterialTheme.colorScheme.onPrimary,
                                                            contentColor = MaterialTheme.colorScheme.primary
                                                        )

                                                        isDisabled -> ButtonDefaults.buttonColors(
                                                            containerColor = Color.LightGray,
                                                            contentColor = Color.DarkGray
                                                        )

                                                        else -> ButtonDefaults.buttonColors(
                                                            containerColor = MaterialTheme.colorScheme.primary,
                                                            contentColor = MaterialTheme.colorScheme.onPrimary
                                                        )
                                                    },
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Text(
                                                        text = "${
                                                            slot.time.hour.toString()
                                                                .padStart(2, '0')
                                                        }:00 - ${
                                                            (slot.time.hour + 1).toString()
                                                                .padStart(2, '0')
                                                        }:00",
                                                        textAlign = TextAlign.Center
                                                    )
                                                }
                                            }

                                            if (rowSlots.size == 1) {
                                                Spacer(modifier = Modifier.weight(1f))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        Button(
                            onClick = {
                                Log.d("Booked", selectedAppointmentTime.toString())
                            },
                            enabled = selectedAppointmentTime != null,
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text("Xác nhận", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}