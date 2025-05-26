package com.example.beaceful.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.beaceful.domain.model.TimeSlot
import com.example.beaceful.domain.repository.AppointmentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class BookingViewModel @Inject constructor(
    private val repo: AppointmentRepository
) : ViewModel() {
    private val _currentMonth = MutableStateFlow(LocalDateTime.now().withDayOfMonth(1))
    val currentMonth: StateFlow<LocalDateTime> = _currentMonth
    fun goToPreviousMonth() {
        _currentMonth.update { it.minusMonths(1).withDayOfMonth(1) }
    }

    fun goToNextMonth() {
        _currentMonth.update { it.plusMonths(1).withDayOfMonth(1) }
    }

    fun generateTimeSlots(
        selectedDate: LocalDate,
        bookedTimes: List<LocalDateTime>
    ): List<TimeSlot> {
        return (7..16).filter { it !in 11..13 }.map { hour ->
            val slotTime = selectedDate.atTime(hour, 0)
            TimeSlot(
                time = slotTime,
                isBooked = bookedTimes.any { it == slotTime }
            )
        }
    }

    fun getBookedTime(doctorId: Int): List<LocalDateTime> {
        val bookedAppointments = repo.getAppointmentsOfDoctor(doctorId)
        return bookedAppointments.map { it.appointmentDate }
    }

}