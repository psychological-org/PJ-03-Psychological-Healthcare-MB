package com.example.beaceful.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.beaceful.domain.model.Appointment
import com.example.beaceful.domain.model.AppointmentStatus
import com.example.beaceful.domain.model.User
import com.example.beaceful.domain.repository.AppointmentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class AppointmentViewModel @Inject constructor(
    val repo: AppointmentRepository
):ViewModel() {
    private val _currentMonth = MutableStateFlow(LocalDateTime.now().withDayOfMonth(1))
    val currentMonth: StateFlow<LocalDateTime> = _currentMonth
    fun goToPreviousMonth() {
        _currentMonth.update { it.minusMonths(1).withDayOfMonth(1) }
    }

    fun goToNextMonth() {
        _currentMonth.update { it.plusMonths(1).withDayOfMonth(1) }
    }

    fun setMonth(month: LocalDateTime) {
        _currentMonth.value = month.withDayOfMonth(1)
    }

    fun getPatient(patientId: Int) = repo.getUserById(patientId)
    fun getAppointment(appointmentId: Int) = repo.getAppointmentById(appointmentId)

    fun getAppointmentsOnDate(date: LocalDateTime): List<Appointment> =
        repo.getAppointmentsOnDate(date)
    fun getPatientByAppointment(appointment: Appointment): User? =
        repo.getPatientByAppointment(appointment)

    fun getUpcoming(): List<Appointment> = getAppointmentsOnDate(LocalDateTime.now()).filter { it.status == AppointmentStatus.CONFIRMED }.sortedBy { it.appointmentDate }
    fun getAppointments(doctorId: Int): List<Appointment> =
        repo.getAppointmentsOfDoctor(doctorId)
    fun getAppointmentsOfPatientByDoctor(doctorId: Int, customerId: Int) =
        repo.getAppointmentsOfDoctor(doctorId).filter { it.patientId == customerId }
    fun getPatients(doctorId: Int): List<User> {
        return getAppointments(doctorId)
            .mapNotNull { getPatientByAppointment(it) }
            .distinctBy { it.id }
    }
    fun onClickAccept() {

    }
    fun onClickReject() {

    }
}