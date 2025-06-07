package com.example.beaceful.ui.screens.appointment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.beaceful.R
import com.example.beaceful.core.util.formatAppointmentDate
import com.example.beaceful.ui.viewmodel.AppointmentViewModel

@Composable
fun AppointmentDetailsScreen(
    appointmentId: Int,
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: AppointmentViewModel = hiltViewModel()
) {
//    val appointment = viewModel.getAppointment(appointmentId)
//    val patient = appointment?.let { viewModel.getPatient(it.patientId) }
//    var doctorNote by remember { mutableStateOf(if (appointment?.note != null) appointment.note else "") }
    val appointment by viewModel.appointment.collectAsState()
    val patients by viewModel.patients.collectAsState()
    val error by viewModel.error.collectAsState()
    var doctorNote by remember { mutableStateOf(appointment?.note ?: "") }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(appointmentId) {
        viewModel.getAppointment(appointmentId)
        isLoading = false
    }

    LaunchedEffect(appointment) {
        appointment?.let { viewModel.getPatient(it.patientId) }
    }

    if (appointment != null && patients[appointment!!.patientId] != null) {
        val patient = patients[appointment!!.patientId]
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.background)
                .padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${stringResource(R.string.cu2)} ${patient!!.fullName}",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(MaterialTheme.colorScheme.primary),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Text(
                        text = stringResource(R.string.cu7),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        text = formatAppointmentDate(appointment!!.appointmentDate),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                // --- Diary Text Area ---
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.TopEnd
                    ) {
                        TextField(
                            value = doctorNote,
                            onValueChange = { doctorNote = it },
                            placeholder = { Text("Nhập ghi chép...", color = Color.Gray) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(500.dp)
                                .background(
                                    MaterialTheme.colorScheme.secondary,
                                    RoundedCornerShape(24.dp)
                                ),
                        )
                    }
                }
                item {
                    Button(
                        onClick = {
                            viewModel.updateAppointmentStatus(
                                appointmentId,
                                appointment!!.status,
                                doctorNote
                            )
                            navController.popBackStack()
                        },
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
