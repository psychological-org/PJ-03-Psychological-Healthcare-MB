package com.example.beaceful.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beaceful.domain.model.Appointment
import com.example.beaceful.domain.model.AppointmentStatus
import com.example.beaceful.domain.model.TimeSlot
import com.example.beaceful.domain.repository.AppointmentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class BookingViewModel @Inject constructor(
    private val repo: AppointmentRepository
) : ViewModel() {
    private val _currentMonth = MutableStateFlow(LocalDateTime.now().withDayOfMonth(1))
    val currentMonth: StateFlow<LocalDateTime> = _currentMonth
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    val _success = MutableStateFlow<String?>(null)
    val success: StateFlow<String?> = _success

    fun goToPreviousMonth() {
        _currentMonth.update { it.minusMonths(1).withDayOfMonth(1) }
    }

    fun goToNextMonth() {
        _currentMonth.update { it.plusMonths(1).withDayOfMonth(1) }
    }

    fun goBackCurrentMonth() {
        _currentMonth.update { LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")).withDayOfMonth(1) }
    }

    fun generateTimeSlots(
        selectedDate: LocalDate,
        bookedTimes: List<LocalDateTime>
    ): List<TimeSlot> {
        return (7..16).filter { it !in 11..13 }.map { hour ->
            val slotTime = selectedDate.atTime(hour, 0)
            val isBooked = bookedTimes.any { booked ->
                booked.toLocalDate() == slotTime.toLocalDate() &&
                        booked.hour == slotTime.hour &&
                        booked.minute == slotTime.minute
            }
            TimeSlot(
                time = slotTime,
                isBooked = isBooked
            )
        }
    }

    suspend fun getBookedTime(doctorId: String): List<LocalDateTime> {
        return try {
            val bookedAppointments = repo.getAppointmentsOfDoctor(doctorId)
            Log.d("BookingViewModel", "Booked times for doctor $doctorId: $bookedAppointments")
            bookedAppointments.map { it.appointmentDate }
        } catch (e: Exception) {
            _error.value = "Lỗi khi tải danh sách thời gian đã đặt: ${e.message}"
            emptyList()
        }
    }

    fun bookAppointment(doctorId: String, patientId: String, appointmentTime: LocalDateTime) {
        viewModelScope.launch {
            try {
                val appointment = Appointment(
                    id = 0,
                    doctorId = doctorId,
                    patientId = patientId,
                    appointmentDate = appointmentTime,
                    status = AppointmentStatus.PENDING

                )
                val result = repo.createAppointment(appointment)
                if (result != null) {
                    _success.value = "Đặt lịch hẹn thành công"
                } else {
                    _error.value = "Lỗi khi đặt lịch hẹn"
                }
            } catch (e: Exception) {
                _error.value = "Lỗi khi đặt lịch hẹn: ${e.message}"
            }
        }
    }

}