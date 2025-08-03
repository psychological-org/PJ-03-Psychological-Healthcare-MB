package com.example.beaceful.ui.screens.notification

import android.util.Log
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.beaceful.core.util.formatDate
import com.example.beaceful.domain.model.Appointment
import com.example.beaceful.ui.viewmodel.AppointmentViewModel

@Composable
fun RatingFullScreen(
    doctorId: String,
    viewModel: AppointmentViewModel = hiltViewModel()
) {
    Log.d("RatingFullScreen", "Received doctorId: $doctorId")
    val appointments by viewModel.appointments.collectAsState()
    val error by viewModel.error.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val filters = remember { listOf<Int?>(null, 5, 4, 3, 2, 1) }
    var selected by remember { mutableStateOf<Int?>(null) }

    // Gọi API để lấy các cuộc hẹn đã được đánh giá
    LaunchedEffect(doctorId) {
        viewModel.getRatedAppointments(doctorId)
    }

    // Lọc các cuộc hẹn theo rating đã chọn
    val filteredAppointments by remember(selected, appointments) {
        derivedStateOf {
            appointments.filter { appt ->
                appt.rating != null && (selected == null || appt.rating == selected)
            }
        }
    }

    when {
        isLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        error != null -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Lỗi: $error", color = MaterialTheme.colorScheme.error)
            }
        }
        else -> {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Đánh giá của bệnh nhân",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    items(filters) { rating ->
                        val isSelected = rating == selected

                        FilterChip(
                            selected = isSelected,
                            onClick = { selected = rating },
                            label = {
                                if (rating == null) Text("Tất cả")
                                else Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(rating.toString())
                                    Spacer(Modifier.width(4.dp))
                                    Icon(Icons.Default.Star, null, Modifier.size(16.dp))
                                }
                            },
                            shape = RoundedCornerShape(50),
                            colors = FilterChipDefaults.filterChipColors().copy(
                                selectedContainerColor = MaterialTheme.colorScheme.secondary,
                                selectedLabelColor = MaterialTheme.colorScheme.onSecondary,
                                selectedLeadingIconColor = MaterialTheme.colorScheme.onSecondary,
                                containerColor = Color.Transparent,
                                labelColor = MaterialTheme.colorScheme.secondary,
                                leadingIconColor = MaterialTheme.colorScheme.secondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                selected = isSelected,
                                enabled = isSelected,
                                borderColor = MaterialTheme.colorScheme.secondary,
                                selectedBorderColor = Color.Transparent
                            )
                        )
                    }
                }
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.secondary
                )
                LazyColumn(
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredAppointments) { appointment ->
                        if (appointment.rating != null) {
                            RatingItem(appointment = appointment)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RatingItem(appointment: Appointment) {
    var expanded by remember { mutableStateOf(false) }
    if (appointment.rating != null) {
        Column(Modifier.padding(horizontal = 16.dp)) {
            /* Dòng sao + ngày */
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row {
                    (1..5).forEach { star ->
                        Icon(
                            if (star <= appointment.rating) Icons.Default.Star
                            else Icons.Default.StarBorder,
                            null,
                            tint = if (star <= appointment.rating)
                                MaterialTheme.colorScheme.secondary
                            else Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Text(
                    text = formatDate(appointment.appointmentDate),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            /* Review */
            if (appointment.review?.isNotBlank() == true) {
                Spacer(Modifier.height(4.dp))

                val contentModifier = if (expanded) Modifier.fillMaxWidth()
                else Modifier.fillMaxWidth().heightIn(max = 120.dp)

                Text(
                    text = appointment.review,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = if (expanded) Int.MAX_VALUE else 5,
                    overflow = TextOverflow.Ellipsis,
                    modifier = contentModifier
                )

                if (!expanded && appointment.review.length > 200) {
                    Text(
                        "...xem thêm",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier
                            .align(Alignment.End)
                            .clickable { expanded = true }
                    )
                }
            }
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}
