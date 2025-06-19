package com.example.beaceful.ui.screens.appointment

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import com.example.beaceful.core.util.UserSession
import com.example.beaceful.core.util.formatAppointmentDate
import com.example.beaceful.domain.model.AppointmentStatus
import com.example.beaceful.ui.navigation.RatingRoute
import com.example.beaceful.ui.viewmodel.AppointmentViewModel

@Composable
fun AppointmentDetailsScreen(
    appointmentId: Int,
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: AppointmentViewModel = hiltViewModel(),
    isDoctorView: Boolean = true,
) {
    val appointment by viewModel.appointment.collectAsState()
    val patients by viewModel.patients.collectAsState()
    val error by viewModel.error.collectAsState()
    val success by viewModel.success.collectAsState()
    val userRole = try { UserSession.getCurrentUserRole() } catch (e: IllegalStateException) { "" }
    val userId = try { UserSession.getCurrentUserId() } catch (e: IllegalStateException) { "" }
    var doctorNote by remember { mutableStateOf(appointment?.note ?: "") }
    var showDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(appointment, patients) {
        Log.d("AppointmentDetailsScreen", "Appointment: $appointment")
        Log.d("AppointmentDetailsScreen", "Patients: $patients")
        Log.d("AppointmentDetailsScreen", "Error: $error")
        Log.d("AppointmentViewModel", "Loaded appointment: $appointment")
    }

    // Cập nhật doctorNote khi appointment thay đổi
    LaunchedEffect(appointment) {
        appointment?.let {
            doctorNote = it.note ?: ""
            viewModel.getPatient(it.patientId)
            viewModel.getDoctor(it.doctorId)
        }
        isLoading = false
    }

    // Gọi API lấy chi tiết lịch hẹn
    LaunchedEffect(appointmentId) {
        if (userId.isNotEmpty()) {
            viewModel.getAppointment(appointmentId)
        } else {
            navController.navigate("login")
        }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (error != null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Lỗi: $error", color = MaterialTheme.colorScheme.error)
        }
    } else if (appointment != null && patients[appointment!!.patientId] != null) {
        val patient = patients[appointment!!.patientId]!!
        val doctor = patients[appointment!!.doctorId]
        val isEditable = isDoctorView && userRole == "doctor" && appointment!!.status != AppointmentStatus.CANCELLED
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.background)
                .padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBackIos,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }
            Text(
//                text = "${stringResource(R.string.cu2)} ${patient!!.fullName}",
                text = "${stringResource(R.string.cu2)} ${
                    if (isDoctorView) {
                        patient?.fullName ?: "Đang tải..."
                    } else {
                        "bạn"
                    }
                }",
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
                    if (doctor != null && !isDoctorView) {
                        Text(
                            text = "${stringResource(R.string.cu7)} ${doctor.fullName}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Text(
                        text = formatAppointmentDate(appointment!!.appointmentDate),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        text = "Trạng thái: ${appointment!!.status}",
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
                            onValueChange = { if (isEditable) doctorNote = it },
                            placeholder = {
                                Text(
                                    text = if (isEditable) "Nhập ghi chép..." else "Chưa có ghi chép nào",
                                    color = Color.Gray
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .background(
                                    MaterialTheme.colorScheme.secondary,
                                    RoundedCornerShape(24.dp)
                                ),
                            readOnly = !isEditable,
                            colors = TextFieldDefaults.colors(
                                focusedTextColor = MaterialTheme.colorScheme.onSecondary,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSecondary,
                                disabledTextColor = MaterialTheme.colorScheme.onSecondary,
                                focusedContainerColor = MaterialTheme.colorScheme.secondary,
                                unfocusedContainerColor = MaterialTheme.colorScheme.secondary,
                                disabledContainerColor = MaterialTheme.colorScheme.secondary
                            )
                        )
                    }
                }
                if (isEditable || appointment!!.status == AppointmentStatus.PENDING) {
                    item {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            if (isEditable) {
                                Button(
                                    onClick = {
                                        viewModel.updateAppointmentStatus(
                                            appointmentId = appointmentId,
                                            status = AppointmentStatus.COMPLETED,
                                            note = doctorNote
                                        )
                                        navController.popBackStack()
                                    },
                                    shape = RoundedCornerShape(24.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.secondary
                                    ),
                                    enabled = doctorNote.isNotBlank()
                                ) {
                                    Text("Lưu ghi chú", color = MaterialTheme.colorScheme.primary)
                                }
                                Spacer(Modifier.width(16.dp))
                            }
                            if (appointment!!.status == AppointmentStatus.PENDING) {
                                OutlinedButton(
                                    onClick = { showDialog = true },
                                    shape = RoundedCornerShape(24.dp)
                                ) {
                                    Text(
                                        "Hủy lịch hẹn",
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            }
                        }
                    }
                }
                if (!isEditable || appointment!!.status == AppointmentStatus.COMPLETED) {
                    item {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            if (appointment!!.status == AppointmentStatus.COMPLETED && !isDoctorView && appointment!!.rating == null) {
                                OutlinedButton(
                                    onClick = { navController.navigate(RatingRoute.createRoute(appointmentId)) },
                                    shape = RoundedCornerShape(24.dp)
                                ) {
                                    Text(
                                        "Đánh giá",
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Hủy lịch hẹn?", color = MaterialTheme.colorScheme.primary) },
            text = {
                Text(
                    "Bạn có chắc chắn muốn hủy lịch hẹn này không?",
                    color = MaterialTheme.colorScheme.primary
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.updateAppointmentStatus(
                            appointmentId = appointmentId,
                            status = AppointmentStatus.CANCELLED,
                            note = doctorNote
                        )
                        showDialog = false
                        navController.popBackStack()
                    }
                ) {
                    Text("Hủy lịch hẹn", color = MaterialTheme.colorScheme.primary)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialog = false }
                ) {
                    Text("Quay lại", color = MaterialTheme.colorScheme.primary)
                }
            }
        )
    }
}
