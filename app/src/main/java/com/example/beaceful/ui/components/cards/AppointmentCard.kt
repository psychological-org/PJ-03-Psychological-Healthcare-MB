package com.example.beaceful.ui.components.cards

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.beaceful.R
import com.example.beaceful.core.util.formatAppointmentDate
import com.example.beaceful.domain.model.Appointment
import com.example.beaceful.domain.model.AppointmentStatus
import com.example.beaceful.domain.model.Emotions
import com.example.beaceful.ui.viewmodel.AppointmentViewModel

@Composable
fun AppointmentCard(
    appointment: Appointment,
    onAppointmentClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AppointmentViewModel = hiltViewModel(),
) {
    val patients by viewModel.patients.collectAsState()
    val patient = patients[appointment.patientId]

    // Gọi API để lấy thông tin bệnh nhân khi appointment thay đổi
    LaunchedEffect(appointment) {
        viewModel.getPatientByAppointment(appointment)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable { onAppointmentClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Row(modifier = Modifier.padding(8.dp)) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(patient?.avatarUrl ?: "") // Xử lý khi avatarUrl là null
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(R.drawable.doctor_placeholder_avatar),
                    error = painterResource(R.drawable.doctor_placeholder_avatar), // Hình ảnh dự phòng khi lỗi
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.onPrimary,
                            shape = CircleShape
                        )
                )
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(
                        text = formatAppointmentDate(appointment.appointmentDate),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    if (patient != null) {
                        Text(
                            text = patient!!.fullName,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        patient!!.phone?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    } else {
                        Text(
                            text = "Đang tải thông tin bệnh nhân...",
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Button(
                    onClick = { viewModel.onClickReject(appointment.id) },
                    shape = RoundedCornerShape(40.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        contentColor = MaterialTheme.colorScheme.onTertiary
                    ),
                    enabled = appointment.status == AppointmentStatus.PENDING
                ) {
                    Text(text = if (appointment.status == AppointmentStatus.CANCELLED) {
                        "Đã từ chối"
                    } else {
                        stringResource(R.string.cancel)
                    },
                        style = MaterialTheme.typography.bodySmall)
                }
                Button(
                    onClick = { viewModel.onClickAccept(appointment.id) },
                    shape = RoundedCornerShape(40.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = when (appointment.status) {
                            AppointmentStatus.CONFIRMED -> MaterialTheme.colorScheme.secondary
                            else -> MaterialTheme.colorScheme.onPrimary
                        },
                        contentColor = when (appointment.status) {
                            AppointmentStatus.CONFIRMED -> MaterialTheme.colorScheme.onSecondary
                            else -> MaterialTheme.colorScheme.primary
                        }
                    ),
                    enabled = appointment.status == AppointmentStatus.PENDING
                ) {
                    Text(text = when (appointment.status) {
                        AppointmentStatus.CONFIRMED -> "Đã xác nhận"
                        else -> stringResource(R.string.verify)
                    },
                        style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}


@Composable
fun AppointmentList(
    modifier: Modifier = Modifier,
    appointments: List<Appointment>,
    navController: NavHostController,
    viewModel: AppointmentViewModel = hiltViewModel()
) {
    val success by viewModel.success.collectAsState()
    val error by viewModel.error.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(success) {
        success?.let { successMessage ->
            Toast.makeText(context, successMessage, Toast.LENGTH_SHORT).show()
            viewModel.clearMessages()
        }
    }

    LaunchedEffect(error) {
        error?.let { errorMessage ->
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            viewModel.clearMessages()
        }
    }

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier,
    ) {
        items(appointments) { appointment ->
            AppointmentCard(
                appointment,
                onAppointmentClick = {
                    navController.navigate("appointment_details/${appointment.id}")
                },
            )
        }
        item {
            Spacer(Modifier.height(80.dp))
        }
    }
}