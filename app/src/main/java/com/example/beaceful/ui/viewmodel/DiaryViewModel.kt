package com.example.beaceful.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.beaceful.domain.model.Appointment
import com.example.beaceful.domain.model.AppointmentStatus
import com.example.beaceful.domain.model.Diary
import com.example.beaceful.domain.model.DumpDataProvider
import com.example.beaceful.domain.model.Emotions
import com.example.beaceful.domain.repository.DiaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class DiaryViewModel @Inject constructor(
    val repo: DiaryRepository,
) : ViewModel() {

    private val _currentMonth = MutableStateFlow(LocalDateTime.now().withDayOfMonth(1))
    val currentMonth: StateFlow<LocalDateTime> = _currentMonth

    fun goToPreviousMonth() {
        _currentMonth.update { it.minusMonths(1).withDayOfMonth(1) }
    }

    fun goToNextMonth() {
        _currentMonth.update { it.plusMonths(1).withDayOfMonth(1) }
    }

    fun moodCount(month: LocalDateTime): Map<Emotions, Int> {
        val count = repo.getDiariesInMonth(month).groupingBy { it.emotion }.eachCount()
        return Emotions.entries.associateWith { count[it] ?: 0 }
    }

    fun getAppointments(userId: Int): List<Appointment> =
        DumpDataProvider.appointments.filter { it.patientId == userId }

    fun getAppointmentsOnDate(userId: Int, date: LocalDateTime): List<Appointment> =
        DumpDataProvider.appointments.filter { it.patientId == userId && it.appointmentDate == date }

    fun getUpcoming(userId: Int): List<Appointment> = getAppointmentsOnDate(
        userId = userId,
        date = LocalDateTime.now()
    ).filter { it.status == AppointmentStatus.CONFIRMED }.sortedBy { it.appointmentDate }

    fun getDoctorByAppointment(appointment: Appointment) = repo.getUserById(appointment.doctorId)

}