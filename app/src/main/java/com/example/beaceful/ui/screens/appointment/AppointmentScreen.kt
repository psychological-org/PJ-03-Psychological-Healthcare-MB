package com.example.beaceful.ui.screens.appointment

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
import com.example.beaceful.ui.components.calendar.CalendarAppointmentScreen
import com.example.beaceful.ui.components.calendar.CalendarDiaryScreen
import com.example.beaceful.ui.components.cards.AppointmentList
import com.example.beaceful.ui.components.cards.DiaryList
import com.example.beaceful.ui.viewmodel.AppointmentViewModel

@Composable
fun AppointmentScreen(
    navController: NavHostController,
    viewModel: AppointmentViewModel = hiltViewModel(),
) {
    var viewMode by remember { mutableStateOf(ViewMode.LIST) }
//    val appointments = viewModel.getAppointments(2)
    val appointments by viewModel.appointments.collectAsState()
    val currentMonth by viewModel.currentMonth.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(2) {
        viewModel.getAppointments("68400bdcfcf44b6d4980cba2")
    }

    Box {
        Column(modifier = Modifier.fillMaxHeight()) {
            AnimatedVisibility(
                visible = viewMode == ViewMode.LIST,
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(initialAlpha = 0.3f),
                exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
            ) {
                Column(modifier = Modifier.fillMaxHeight()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("Lịch hẹn chờ duyệt", style = MaterialTheme.typography.titleLarge)
                    }
                    Spacer(Modifier.height(6.dp))

                    AppointmentList(
                        appointments = appointments,
                        navController = navController,
                    )
                }
            }
            AnimatedVisibility(
                visible = viewMode == ViewMode.CALENDAR,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(initialAlpha = 0.3f),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                Column {

                    CalendarAppointmentScreen(
                        currentMonth = currentMonth,
                        navController = navController
                    )

                }
            }
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