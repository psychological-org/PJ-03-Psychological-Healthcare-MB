package com.example.beaceful.ui.screens.customer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.beaceful.R
import com.example.beaceful.core.util.formatAppointmentDate
import com.example.beaceful.domain.model.Appointment
import com.example.beaceful.domain.model.AppointmentStatus
import com.example.beaceful.ui.components.CustomSearchBar
import com.example.beaceful.ui.navigation.AppointmentDetails
import com.example.beaceful.ui.viewmodel.AppointmentViewModel

@Composable
fun CustomerDetailsScreen(
    customerId: Int,
    navController: NavHostController,
    viewModel: AppointmentViewModel = hiltViewModel(),
    isDoctorView: Boolean = true,
) {
    val patient = viewModel.getPatient(customerId)
    if (patient != null) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(top = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "${stringResource(R.string.cu2)} ${
                        if (isDoctorView) {
                            patient.fullName
                        } else {
                            "bạn"
                        }
                    }",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge
                )
            }
            CustomSearchBar(
                suggestions = null,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            AppointmentAccordion(
                appointments = if (isDoctorView) viewModel.getAppointmentsOfPatientByDoctor(
                    2,
                    customerId
                ) else viewModel.repo.getAllAppointmentsOfPatient(customerId),
                navController = navController,
                isDoctorView = isDoctorView,
            )
        }
    }
}

@Composable
fun AppointmentItem(
    appointment: Appointment,
    onAppointmentClick: () -> Unit,
    modifier: Modifier = Modifier,
) {

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable { onAppointmentClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Text(
            formatAppointmentDate(appointment.appointmentDate),
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.padding(8.dp)
        )
    }
}


@Composable
fun AppointmentAccordionList(
    appointments: List<Appointment>,
    navController: NavHostController,
    isDoctorView: Boolean = true,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(start = 40.dp, top = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        VerticalDivider(
            Modifier
                .fillMaxHeight()
                .width(2.dp),
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.width(20.dp))
        Column(
            modifier = Modifier
                .padding(start = 20.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            appointments.forEach { appointment ->
                AppointmentItem(
                    appointment,
                    onAppointmentClick = {
                        navController.navigate(
                            AppointmentDetails.createRoute(appointment.id, isDoctorView)
                        )
                    },
                )
            }
        }
    }
}


@Composable
fun AppointmentAccordion(
    appointments: List<Appointment>,
    navController: NavHostController,
    isDoctorView: Boolean = true,
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(AppointmentStatus.entries) { status ->
            var expanded by remember { mutableStateOf(false) }
            val degrees by animateFloatAsState(if (expanded) -90f else 90f)
            Column {
                Row(modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .background(if (expanded) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary)
                    .clickable { expanded = expanded.not() }
                    .fillMaxWidth()
                    .padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    when (status) {
                        AppointmentStatus.PENDING -> Text(
                            stringResource(R.string.cu6),
                            color = if (expanded) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onPrimary
                        )

                        AppointmentStatus.CONFIRMED -> Text(
                            stringResource(R.string.cu3),
                            color = if (expanded) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onPrimary
                        )

                        AppointmentStatus.CANCELLED -> Text(
                            stringResource(R.string.cu5),
                            color = if (expanded) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onPrimary
                        )

                        AppointmentStatus.COMPLETED -> Text(
                            stringResource(R.string.cu4),
                            color = if (expanded) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = null,
                        modifier = Modifier.rotate(degrees),
                        tint = if (expanded) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onPrimary
                    )
                }
                AnimatedVisibility(
                    visible = expanded,
                    enter = expandVertically(
                        spring(
                            stiffness = Spring.StiffnessMediumLow,
                            visibilityThreshold = IntSize.VisibilityThreshold
                        )
                    ),
                    exit = shrinkVertically()
                ) {
                    AppointmentAccordionList(
                        appointments.filter { it.status == status }
                            .sortedByDescending { it.appointmentDate },
                        navController = navController,
                        isDoctorView = isDoctorView,
                    )
                }
            }
        }
    }
}