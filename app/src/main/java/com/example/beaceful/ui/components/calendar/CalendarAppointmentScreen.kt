package com.example.beaceful.ui.components.calendar

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.beaceful.core.util.formatAppointmentDate
import com.example.beaceful.domain.model.Appointment
import com.example.beaceful.domain.model.User
import com.example.beaceful.ui.components.cards.AppointmentList
import com.example.beaceful.ui.viewmodel.AppointmentViewModel
import java.time.LocalDateTime

@Composable
fun CalendarAppointmentScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: AppointmentViewModel = hiltViewModel(),
    currentMonth: LocalDateTime,
) {
    var selectedAppointments by remember { mutableStateOf<List<Appointment>?>(null) }
    val nextAppointments: List<Appointment> = viewModel.getUpcoming()

    Box {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { viewModel.goToPreviousMonth() }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Previous"
                    )
                }
                Button(onClick = {}) {
                    Text(
                        text = "${
                            currentMonth.month.name.lowercase()
                                .replaceFirstChar { it.uppercase() }
                        } ${currentMonth.year}",
                        style = MaterialTheme.typography.titleLarge,
                    )
                }
                IconButton(onClick = { viewModel.goToNextMonth() }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Next"
                    )
                }
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
                                highlightDates = { date ->
                                    viewModel.getAppointmentsOnDate(date).isNotEmpty()
                                },
                                getColorsForDate = { date ->
                                    if (viewModel.getAppointmentsOnDate(date).isNotEmpty()) {
                                        listOf(Color(0xFFB089CA))
                                    } else {
                                        emptyList()
                                    }
                                },
                                onClickDate = { }
                            )
                        }

                        Spacer(Modifier.height(24.dp))

                        Text(
                            text = "Lịch hẹn sắp tới",
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.onPrimary,
                                    RoundedCornerShape(24.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(Modifier.height(12.dp))

//                    Box(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(120.dp)
//                            .background(
//                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f),
//                                RoundedCornerShape(24.dp)
//                            )
//                    ) {
//                        // TODO
//
//                    }
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.tertiary,
                                    RoundedCornerShape(24.dp)
                                ),
                            contentPadding = PaddingValues(
                                horizontal = 24.dp,
                                vertical = 12.dp
                            )
                        ) {
                            if (nextAppointments.isEmpty()) {
                                item { Text(text = "Trống", textAlign = TextAlign.Center) }
                            } else {
                                items(nextAppointments) { appointment ->

                                    val patient: User? =
                                        viewModel.getPatientByAppointment(appointment)
                                    if (patient != null) {
                                        Column {
                                            Text(text = formatAppointmentDate(appointment.appointmentDate))
                                            Text(text = patient.fullName)
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
        }
        AnimatedVisibility(
            visible = selectedAppointments != null,
            enter = fadeIn(animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(300)),
            modifier = Modifier
                .fillMaxSize()
                .background(
                    MaterialTheme.colorScheme.background.copy(alpha = 0.95f)
                )
        ) {

            selectedAppointments?.let {
                Box {
                    AppointmentList(
                        appointments = it,
                        navController = navController,
                    )
                    FloatingActionButton(
                        onClick = { selectedAppointments = null },
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
}