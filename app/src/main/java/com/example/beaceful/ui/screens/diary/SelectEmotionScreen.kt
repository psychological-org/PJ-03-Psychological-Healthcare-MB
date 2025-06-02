package com.example.beaceful.ui.screens.diary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.beaceful.R
import com.example.beaceful.domain.model.Emotions
import com.example.beaceful.ui.components.calendar.TimePickerDialog
import com.example.beaceful.ui.navigation.WriteDiary
import com.example.beaceful.ui.screens.home.EmotionItem
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun SelectEmotionScreen(
    navController: NavHostController,
) {
    val now = remember { LocalDateTime.now() }
    var selectedDateTime by remember { mutableStateOf(now) }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = now.toLocalDate()
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli(),
        initialDisplayedMonthMillis = null,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis <= Instant.now().toEpochMilli()
            }
        }
    )

    val timePickerState = rememberTimePickerState(
        initialHour = now.hour,
        initialMinute = now.minute,
        is24Hour = true
    )

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(stringResource(R.string.di5_greeting), style = MaterialTheme.typography.headlineSmall)

        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton (shape = RectangleShape,onClick = { showDatePicker = true }) {
                Text(selectedDateTime.toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), textDecoration = TextDecoration.Underline)
            }
            VerticalDivider(Modifier.height(24.dp))
            TextButton(shape = RectangleShape,onClick = { showTimePicker = true }) {
                Text(selectedDateTime.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")), textDecoration = TextDecoration.Underline)
            }
        }

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            contentPadding = PaddingValues(horizontal = 36.dp),
        ) {
            items(Emotions.entries) { item ->
                EmotionItem(
                    drawable = item.iconRes,
                    text = item.descriptionRes,
                    onClick = {
                        navController.navigate(WriteDiary.createRoute(item, selectedDateTime))
                    }
                )
            }
        }
    }
    // date picker component
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                Button(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val selectedDate = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                            if (selectedDate.atTime(selectedDateTime.toLocalTime()).isBefore(
                                    LocalDateTime.now())) {
                                selectedDateTime = selectedDateTime.withYear(selectedDate.year)
                                    .withMonth(selectedDate.monthValue)
                                    .withDayOfMonth(selectedDate.dayOfMonth)
                            }
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("Chọn")
                }
            },
            dismissButton = {
                Button(onClick = { showDatePicker = false }) { Text("Hủy") }
            },
            colors = DatePickerDefaults.colors(
                containerColor = MaterialTheme.colorScheme.onPrimary,
                titleContentColor = MaterialTheme.colorScheme.primary,
                headlineContentColor = MaterialTheme.colorScheme.primary,
                weekdayContentColor = MaterialTheme.colorScheme.primary,
                subheadContentColor = MaterialTheme.colorScheme.primary,
                navigationContentColor = MaterialTheme.colorScheme.primary,
                yearContentColor = MaterialTheme.colorScheme.primary,
                disabledYearContentColor = Color.Gray,
                currentYearContentColor = MaterialTheme.colorScheme.primary,
                dayContentColor = MaterialTheme.colorScheme.primary,
                disabledDayContentColor = Color.Gray,
                selectedDayContentColor = MaterialTheme.colorScheme.onPrimary,
                disabledSelectedDayContentColor = Color.Gray,
                todayContentColor = MaterialTheme.colorScheme.primary,
                todayDateBorderColor = MaterialTheme.colorScheme.primary,
                dateTextFieldColors = TextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.primary,
                    unfocusedTextColor = MaterialTheme.colorScheme.primary,
                    focusedPlaceholderColor = MaterialTheme.colorScheme.primary,
                    focusedTrailingIconColor = MaterialTheme.colorScheme.primary,
                    focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
                    focusedIndicatorColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedPlaceholderColor = MaterialTheme.colorScheme.primary,
                    unfocusedTrailingIconColor = MaterialTheme.colorScheme.primary,
                    unfocusedLeadingIconColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.onPrimary,
                    focusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                )
            )
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.onPrimary,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    headlineContentColor = MaterialTheme.colorScheme.primary,
                    weekdayContentColor = MaterialTheme.colorScheme.primary,
                    subheadContentColor = MaterialTheme.colorScheme.primary,
                    navigationContentColor = MaterialTheme.colorScheme.primary,
                    yearContentColor = MaterialTheme.colorScheme.primary,
                    disabledYearContentColor = Color.Gray,
                    currentYearContentColor = MaterialTheme.colorScheme.primary,
                    dayContentColor = MaterialTheme.colorScheme.primary,
                    disabledDayContentColor = Color.Gray,
                    selectedDayContentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledSelectedDayContentColor = Color.Gray,
                    todayContentColor = MaterialTheme.colorScheme.primary,
                    todayDateBorderColor = MaterialTheme.colorScheme.primary,
                    dateTextFieldColors = TextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.primary,
                        unfocusedTextColor = MaterialTheme.colorScheme.primary,
                        focusedPlaceholderColor = MaterialTheme.colorScheme.primary,
                        focusedTrailingIconColor = MaterialTheme.colorScheme.primary,
                        focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
                        focusedIndicatorColor = MaterialTheme.colorScheme.onPrimary,
                        unfocusedPlaceholderColor = MaterialTheme.colorScheme.primary,
                        unfocusedTrailingIconColor = MaterialTheme.colorScheme.primary,
                        unfocusedLeadingIconColor = MaterialTheme.colorScheme.primary,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.onPrimary,
                        focusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                        unfocusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.primary,
                    )
                )

            )
        }
    }


// time picker component
    if (showTimePicker) {
        TimePickerDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                Button(
                    onClick = {
                        val newDateTime = selectedDateTime.withHour(timePickerState.hour).withMinute(timePickerState.minute)
                        if (newDateTime.isBefore(LocalDateTime.now())) {
                            selectedDateTime = newDateTime
                        }
                        showTimePicker = false
                    }
                ) {
                    Text("Chọn")
                }
            },
            dismissButton = {
                Button(onClick = { showTimePicker = false }) { Text("Hủy") }
            }
        ) {
            TimePicker(state = timePickerState, colors = TimePickerDefaults.colors())
        }
    }

}