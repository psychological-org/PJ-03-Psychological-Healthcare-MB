package com.example.beaceful.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.beaceful.domain.model.Appointment
import com.example.beaceful.domain.model.AppointmentStatus
import com.example.beaceful.domain.model.User
import com.example.beaceful.domain.repository.AppointmentRepository
import com.example.beaceful.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class AppointmentViewModel @Inject constructor(
    private val repo: AppointmentRepository,
    private val userRepository: UserRepository
):ViewModel() {
//    private val _currentMonth = MutableStateFlow(LocalDateTime.now().withDayOfMonth(1))
//    val currentMonth: StateFlow<LocalDateTime> = _currentMonth
//    fun goToPreviousMonth() {
//        _currentMonth.update { it.minusMonths(1).withDayOfMonth(1) }
//    }
//
//    fun goToNextMonth() {
//        _currentMonth.update { it.plusMonths(1).withDayOfMonth(1) }
//    }
//
//    fun setMonth(month: LocalDateTime) {
//        _currentMonth.value = month.withDayOfMonth(1)
//    }
//
//    fun getPatient(patientId: Int) = repo.getUserById(patientId)
//    fun getAppointment(appointmentId: Int) = repo.getAppointmentById(appointmentId)
//
//    fun getAppointmentsOnDate(date: LocalDateTime): List<Appointment> =
//        repo.getAppointmentsOnDate(date)
//    fun getPatientByAppointment(appointment: Appointment): User? =
//        repo.getPatientByAppointment(appointment)
//
//    fun getUpcoming(): List<Appointment> = getAppointmentsOnDate(LocalDateTime.now()).filter { it.status == AppointmentStatus.CONFIRMED }.sortedBy { it.appointmentDate }
//    fun getAppointments(doctorId: Int): List<Appointment> =
//        repo.getAppointmentsOfDoctor(doctorId)
//    fun getAppointmentsOfPatient(doctorId: Int, customerId: Int) =
//        repo.getAppointmentsOfDoctor(doctorId).filter { it.patientId == customerId }
//    fun getPatients(doctorId: Int): List<User> {
//        return getAppointments(doctorId)
//            .mapNotNull { getPatientByAppointment(it) }
//            .distinctBy { it.id }
//    }
//    fun onClickAccept() {
//
//    }
//    fun onClickReject() {
//
//    }
    private val _currentMonth = MutableStateFlow(LocalDateTime.now().withDayOfMonth(1))
    val currentMonth: StateFlow<LocalDateTime> = _currentMonth

    private val _appointments = MutableStateFlow<List<Appointment>>(emptyList())
    val appointments: StateFlow<List<Appointment>> = _appointments

    private val _appointment = MutableStateFlow<Appointment?>(null)
    val appointment: StateFlow<Appointment?> = _appointment

    private val _patients = MutableStateFlow<Map<String, User>>(emptyMap())
    val patients: StateFlow<Map<String, User>> = _patients

    private val _upcoming = MutableStateFlow<List<Appointment>>(emptyList())
    val upcoming: StateFlow<List<Appointment>> = _upcoming

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _success = MutableStateFlow<String?>(null)
    val success: StateFlow<String?> = _success

    private val TAG = "AppointmentViewModel"

    init {
        fetchAppointments()
    }

    fun goToPreviousMonth() {
        _currentMonth.update { it.minusMonths(1).withDayOfMonth(1) }
        fetchAppointments()
    }

    fun goToNextMonth() {
        _currentMonth.update { it.plusMonths(1).withDayOfMonth(1) }
        fetchAppointments()
    }

    fun setMonth(month: LocalDateTime) {
        _currentMonth.value = month.withDayOfMonth(1)
        fetchAppointments()
    }

    fun getPatient(patientId: String) {
        viewModelScope.launch {
            try {
                val patient = repo.getUserById(patientId)
                if (patient != null) {
                    _patients.update { it + (patientId to patient) }
                    Log.d(TAG, "Loaded patient: $patient")
                }
            } catch (e: Exception) {
                _error.value = "Lỗi khi tải thông tin bệnh nhân: ${e.message}"
                Log.e(TAG, "Error loading patient: ${e.message}", e)
            }
        }
    }

    fun getAppointment(appointmentId: Int) {
        viewModelScope.launch {
            try {
                _appointment.value = repo.getAppointmentById(appointmentId)
                _appointment.value?.let { appointment ->
                    getPatient(appointment.patientId)
                    Log.d(TAG, "Loaded appointment: $appointment")
                }
            } catch (e: Exception) {
                _error.value = "Lỗi khi tải lịch hẹn: ${e.message}"
                Log.e(TAG, "Error loading appointment: ${e.message}", e)
            }
        }
    }

    private fun fetchAppointments() {
        viewModelScope.launch {
            try {
                val date = _currentMonth.value
                // Tạm lấy tất cả lịch hẹn của doctor để debug
                _appointments.value = repo.getAppointmentsOfDoctor("0e370c47-9a29-4a8e-8f17-4e473d68cadd") // Doctor ID từ JSON
                // _appointments.value = repo.getAppointmentsOnDate(date) // Bật lại sau khi debug
                Log.d(TAG, "Fetched appointments for date ${date.toLocalDate()}: ${_appointments.value}")
                // Tải tất cả bệnh nhân
                val patientIds = _appointments.value.map { it.patientId }.toSet()
                patientIds.forEach { getPatient(it) }
            } catch (e: Exception) {
                _error.value = "Lỗi khi tải lịch hẹn: ${e.message}"
                Log.e(TAG, "Error loading appointments: ${e.message}", e)
            }
        }
    }

    fun getPatientByAppointment(appointment: Appointment) {
        getPatient(appointment.patientId)
    }

    fun getUpcoming() {
        viewModelScope.launch {
            try {
                val currentDate = LocalDateTime.now()
                _upcoming.value = repo.getAppointmentsOnDate(currentDate)
                    .filter { it.status == AppointmentStatus.CONFIRMED }
                    .sortedBy { it.appointmentDate }
                Log.d(TAG, "Loaded upcoming: ${_upcoming.value}")
            } catch (e: Exception) {
                _error.value = "Lỗi khi tải lịch hẹn sắp tới: ${e.message}"
                Log.e(TAG, "Error loading upcoming: ${e.message}", e)
            }
        }
    }

    fun getAppointments(doctorId: String) {
        viewModelScope.launch {
            try {
                _appointments.value = repo.getAppointmentsOfDoctor(doctorId)
                Log.d(TAG, "Loaded appointments for doctor $doctorId: ${_appointments.value}")
                val patientIds = _appointments.value.map { it.patientId }.toSet()
                patientIds.forEach { getPatient(it) }
            } catch (e: Exception) {
                _error.value = "Lỗi khi tải lịch hẹn của bác sĩ: ${e.message}"
                Log.e(TAG, "Error loading appointments: ${e.message}", e)
            }
        }
    }

    fun getAppointmentsOfPatient(doctorId: String, patientId: String) {
        viewModelScope.launch {
            try {
                _appointments.value = repo.getAppointmentsOfDoctor(doctorId)
                    .filter { it.patientId == patientId }
                val patientIds = _appointments.value.map { it.patientId }.toSet()
                patientIds.forEach { getPatient(it) }
            } catch (e: Exception) {
                _error.value = "Lỗi khi tải lịch hẹn của bệnh nhân: ${e.message}"
                Log.e(TAG, "Error loading patient appointments: ${e.message}", e)
            }
        }
    }

    fun getPatients(doctorId: String) {
        viewModelScope.launch {
            try {
                val patients = repo.getAllPatientsOfDoctor(doctorId)
                _patients.value = patients.associateBy { it.id }
                Log.d(TAG, "Loaded patients for doctor $doctorId: ${patients}")
            } catch (e: Exception) {
                _error.value = "Lỗi khi lấy danh sách bệnh nhân: ${e.message}"
                Log.e(TAG, "Error loading patients: ${e.message}", e)
            }
        }
    }

    fun onClickAccept(appointmentId: Int) {
        viewModelScope.launch {
            try {
                val appointment = _appointments.value.find { it.id == appointmentId }
                    ?: _appointment.value?.takeIf { it.id == appointmentId }
                if (appointment != null) {
                    val updatedAppointment = appointment.copy(status = AppointmentStatus.CONFIRMED)
                    val success = repo.updateAppointment(updatedAppointment)
                    if (success) {
                        _appointments.value = _appointments.value.map {
                            if (it.id == appointmentId) updatedAppointment else it
                        }
                        _appointment.value = if (_appointment.value?.id == appointmentId) {
                            updatedAppointment
                        } else {
                            _appointment.value
                        }
                        _success.value = "Đã xác nhận lịch hẹn"
                    } else {
                        _error.value = "Lỗi khi xác nhận lịch hẹn"
                    }
                } else {
                    _error.value = "Không tìm thấy lịch hẹn"
                }
            } catch (e: Exception) {
                _error.value = "Lỗi khi xác nhận lịch hẹn: ${e.message}"
            }
        }
    }

    fun onClickReject(appointmentId: Int) {
        viewModelScope.launch {
            try {
                val appointment = _appointments.value.find { it.id == appointmentId }
                    ?: _appointment.value?.takeIf { it.id == appointmentId }
                if (appointment != null) {
                    val updatedAppointment = appointment.copy(status = AppointmentStatus.CANCELLED)
                    val success = repo.updateAppointment(updatedAppointment)
                    if (success) {
                        _appointments.value = _appointments.value.map {
                            if (it.id == appointmentId) updatedAppointment else it
                        }
                        _appointment.value = if (_appointment.value?.id == appointmentId) {
                            updatedAppointment
                        } else {
                            _appointment.value
                        }
                        _success.value = "Đã hủy lịch hẹn"
                    } else {
                        _error.value = "Lỗi khi hủy lịch hẹn"
                    }
                } else {
                    _error.value = "Không tìm thấy lịch hẹn"
                }
            } catch (e: Exception) {
                _error.value = "Lỗi khi hủy lịch hẹn: ${e.message}"
            }
        }
    }

    fun updateAppointmentStatus(appointmentId: Int, status: AppointmentStatus, note: String? = null) {
        viewModelScope.launch {
            try {
                val appointment = _appointments.value.find { it.id == appointmentId }
                    ?: _appointment.value?.takeIf { it.id == appointmentId }
                if (appointment != null) {
                    val updatedAppointment = appointment.copy(
                        status = status,
                        note = note ?: appointment.note
                    )
                    val success = repo.updateAppointment(updatedAppointment)
                    if (success) {
                        _appointments.value = _appointments.value.map {
                            if (it.id == appointmentId) updatedAppointment else it
                        }
                        _appointment.value = if (_appointment.value?.id == appointmentId) {
                            updatedAppointment
                        } else {
                            _appointment.value
                        }
                    } else {
                        _error.value = "Lỗi khi cập nhật lịch hẹn"
                    }
                } else {
                    _error.value = "Không tìm thấy lịch hẹn"
                }
            } catch (e: Exception) {
                _error.value = "Lỗi khi cập nhật lịch hẹn: ${e.message}"
            }
        }
    }

    fun clearMessages() {
        _success.value = null
        _error.value = null
    }
}