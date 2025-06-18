package com.example.beaceful.ui.screens.notification

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.beaceful.R
import com.example.beaceful.domain.model.DumpDataProvider
import com.example.beaceful.domain.model.User

@Composable
fun RatingScreen(
    doctor: User? = DumpDataProvider.listUser.find { it.id == "2" },
) {
    var selectedRating by remember { mutableStateOf(0) }
    var review by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (doctor != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(doctor.avatarUrl)
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(R.drawable.doctor_placeholder_avatar),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape)
                )

                Text(doctor.fullName, style = MaterialTheme.typography.titleLarge)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Bạn đánh giá bác sĩ này như thế nào?", style = MaterialTheme.typography.bodyLarge)

        Spacer(modifier = Modifier.height(12.dp))

        Row (
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            (1..5).forEach { star ->
                Icon(
                    imageVector = if (star <= selectedRating) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = "Star $star",
                    tint = if (star <= selectedRating) Color(0xFFFFF79C) else Color.Gray,
                    modifier = Modifier
                        .size(40.dp)
                        .clickable { selectedRating = star }
                )
            }
        }

        Row (
            modifier = Modifier.height(48.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (selectedRating > 0) {
                Text(
                    text = when (selectedRating) {
                        1 -> "Rất tệ"
                        2 -> "Tệ"
                        3 -> "Bình thường"
                        4 -> "Tốt"
                        5 -> "Tuyệt vời"
                        else -> ""
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))


        OutlinedTextField(
            value = review,
            onValueChange = { review = it },
            placeholder = { Text("Viêt đánh giá", color = Color.Gray) },
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(horizontal = 12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.secondary,
                focusedTextColor = MaterialTheme.colorScheme.primary,
                unfocusedTextColor = MaterialTheme.colorScheme.primary,
            ),
            trailingIcon = {
                Box(Modifier.fillMaxHeight()){
                    Icon(
                        Icons.Default.Clear, contentDescription = null,
                        modifier = Modifier.clickable(onClick = { review = "" }).align(Alignment.TopEnd).offset(y = 12.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        )

        Spacer(Modifier.height(36.dp))
        Button(
            onClick = {},
            modifier = Modifier.fillMaxWidth(),
            enabled = selectedRating > 0,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            )
        ) {
            Text("Gửi đánh giá")
        }
    }
}
