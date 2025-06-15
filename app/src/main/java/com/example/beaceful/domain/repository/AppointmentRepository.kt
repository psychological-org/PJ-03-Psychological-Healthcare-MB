package com.example.beaceful.domain.repository

import android.util.Log
import com.example.beaceful.core.network.appointment.AppointmentApiService
import com.example.beaceful.core.network.appointment.AppointmentRequest
import com.example.beaceful.domain.model.DumpDataProvider
import com.example.beaceful.domain.model.Appointment
import com.example.beaceful.domain.model.AppointmentStatus
import com.example.beaceful.domain.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppointmentRepository @Inject constructor(
    private val appointmentApiService: AppointmentApiService,
    private val userRepository: UserRepository
) {
    suspend fun createAppointment(appointment: Appointment): Int? = withContext(Dispatchers.IO) {
        try {
            val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val formattedDate = appointment.appointmentDate.toLocalDate().format(dateFormatter)

            val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
            val formattedTime = appointment.appointmentDate.toLocalTime().format(timeFormatter)

            val request = AppointmentRequest(
                id = null,
                status = appointment.status.name.lowercase(),
                appointmentDate = formattedDate,
                appointmentTime = formattedTime,
                rating = appointment.rating?.toDouble(),
                review = appointment.review,
                patientId = appointment.patientId,
                doctorId = appointment.doctorId,
                note = appointment.note
            )
            appointmentApiService.createAppointment(request)
        } catch (e: Exception) {
            Log.e("AppointmentRepository", "Error creating appointment: ${e.message}", e)
            null
        }
    }

    suspend fun updateAppointment(appointment: Appointment): Boolean = withContext(Dispatchers.IO) {
        try {
            val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val formattedDate = appointment.appointmentDate.toLocalDate().format(dateFormatter)

            // Định dạng giờ thành "HH:mm:ss"
            val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
            val formattedTime = appointment.appointmentDate.toLocalTime().format(timeFormatter)

            val request = AppointmentRequest(
                id = appointment.id,
                status = appointment.status.name.lowercase(),
                appointmentDate = formattedDate,
                appointmentTime = formattedTime,
                rating = appointment.rating?.toDouble(),
                review = appointment.review,
                patientId = appointment.patientId,
                doctorId = appointment.doctorId,
                note = appointment.note
            )
            appointmentApiService.updateAppointment(request)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteAppointment(appointmentId: Int): Boolean = withContext(Dispatchers.IO) {
        try {
            appointmentApiService.deleteAppointment(appointmentId)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getAppointmentsOfDoctor(doctorId: String): List<Appointment> = withContext(Dispatchers.IO) {
        try {
            // Gọi API để lấy danh sách lịch hẹn
            val response = appointmentApiService.getAppointments(page = 0, limit = 100)
            Log.d("AppointmentRepository", "Raw appointments: ${response.content}")
            // Lọc và chuyển đổi từ AppointmentRequest sang Appointment
            val filteredAppointments = response.content
                .filter {
                    it.doctorId == doctorId &&
                            it.appointmentTime != null && // Chỉ lấy lịch có appointmentTime
                            (it.status == "pending" || it.status == "confirmed")
                }
                .map { appt ->
                    Appointment.fromApiResponse(
                        id = appt.id ?: 0,
                        status = appt.status,
                        appointmentDate = appt.appointmentDate,
                        appointmentTime = appt.appointmentTime,
                        patientId = appt.patientId,
                        doctorId = appt.doctorId,
                        note = appt.note,
                        rating = appt.rating,
                        review = appt.review
                    )
                }
            Log.d("AppointmentRepository", "Filtered appointments for doctor $doctorId: $filteredAppointments")
            filteredAppointments
        } catch (e: Exception) {
            Log.e("AppointmentRepository", "Error fetching appointments: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun getAppointmentsOfDoctorByStatus(doctorId: String, status: AppointmentStatus): List<Appointment> = withContext(Dispatchers.IO) {
        try {
            val response = appointmentApiService.getAppointments(page = 0, limit = 100)
            Log.d("AppointmentRepository", "Raw appointments for status: ${response.content}")
            response.content
                .filter {
                    it.doctorId == doctorId &&
                            it.appointmentTime != null && // Chỉ lấy lịch có appointmentTime
                            it.status == status.name.lowercase()
                }
                .map { appt ->
                    Appointment.fromApiResponse(
                        id = appt.id ?: 0,
                        status = appt.status,
                        appointmentDate = appt.appointmentDate,
                        appointmentTime = appt.appointmentTime,
                        patientId = appt.patientId,
                        doctorId = appt.doctorId,
                        note = appt.note,
                        rating = appt.rating,
                        review = appt.review
                    )
                }
        } catch (e: Exception) {
            Log.e("AppointmentRepository", "Error fetching appointments by status: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun getAppointmentsOnDate(date: LocalDateTime): List<Appointment> = withContext(Dispatchers.IO) {
        try {
            val response = appointmentApiService.getAppointments(page = 0, limit = 100)
            Log.d("AppointmentRepository", "Raw appointments for date: ${response.content}")
            response.content
                .filter {
                    it.appointmentTime != null && // Chỉ lấy lịch có appointmentTime
                            LocalDate.parse(it.appointmentDate, DateTimeFormatter.ISO_LOCAL_DATE) == date.toLocalDate()
                }
                .map { appt ->
                    Appointment.fromApiResponse(
                        id = appt.id ?: 0,
                        status = appt.status,
                        appointmentDate = appt.appointmentDate,
                        appointmentTime = appt.appointmentTime,
                        patientId = appt.patientId,
                        doctorId = appt.doctorId,
                        note = appt.note,
                        rating = appt.rating,
                        review = appt.review
                    )
                }
        } catch (e: Exception) {
            Log.e("AppointmentRepository", "Error fetching appointments on date: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun getAppointmentById(appointmentId: Int): Appointment? = withContext(Dispatchers.IO) {
        try {
            val response = appointmentApiService.getAppointmentById(appointmentId)
            Log.d("AppointmentRepository", "Raw appointment $appointmentId: $response")
            Appointment.fromApiResponse(
                id = response.id ?: 0,
                status = response.status,
                appointmentDate = response.appointmentDate,
                appointmentTime = response.appointmentTime,
                patientId = response.patientId,
                doctorId = response.doctorId,
                note = response.note,
                rating = response.rating,
                review = response.review
            )
        } catch (e: Exception) {
            Log.e("AppointmentRepository", "Error fetching appointment $appointmentId: ${e.message}", e)
            null
        }
    }

//    fun getUserById(userId: String): User? =
//        DumpDataProvider.listUser.find { it.id == userId }
    suspend fun getUserById(userId: String): User? = userRepository.getUserById(userId)

    suspend fun getPatientByAppointment(appointment: Appointment): User? =
        getUserById(appointment.patientId)

    suspend fun getPatientByAppointmentId(appointmentId: Int): User? = withContext(Dispatchers.IO) {
        getAppointmentById(appointmentId)?.let { getUserById(it.patientId) }
    }

    suspend fun getAllPatientsOfDoctor(doctorId: String): List<User> = withContext(Dispatchers.IO) {
        try {
            val appointments = getAppointmentsOfDoctor(doctorId)
            val patientIds = appointments.map { it.patientId }.toSet()
            patientIds.mapNotNull { userRepository.getUserById(it) }
        } catch (e: Exception) {
            Log.e("AppointmentRepository", "Error fetching patients: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun getAllAppointmentsOfPatient(patientId: String): List<Appointment> = withContext(Dispatchers.IO) {
        try {
            val response = appointmentApiService.getAppointments(page = 0, limit = 100)
            Log.d("AppointmentRepository", "Raw appointments: ${response.content}")
            val filteredAppointments = response.content
                .filter {
                    it.patientId == patientId &&
                            it.appointmentTime != null &&
                            (it.status == "pending" || it.status == "confirmed")
                }
                .map { appt ->
                    Appointment.fromApiResponse(
                        id = appt.id ?: 0,
                        status = appt.status,
                        appointmentDate = appt.appointmentDate,
                        appointmentTime = appt.appointmentTime,
                        patientId = appt.patientId,
                        doctorId = appt.doctorId,
                        note = appt.note,
                        rating = appt.rating,
                        review = appt.review
                    )
                }
            Log.d("AppointmentRepository", "Filtered appointments for patient $patientId: $filteredAppointments")
            filteredAppointments
        } catch (e: Exception) {
            Log.e("AppointmentRepository", "Error fetching appointments for patient: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun getAllPatientsOfDoctorByStatus(doctorId: String, status: AppointmentStatus): List<User> = withContext(Dispatchers.IO) {
        try {
            val appointments = getAppointmentsOfDoctorByStatus(doctorId, status)
            val patientIds = appointments.map { it.patientId }.toSet()
            patientIds.mapNotNull { userRepository.getUserById(it) }
        } catch (e: Exception) {
            Log.e("AppointmentRepository", "Error fetching patients by status: ${e.message}", e)
            emptyList()
        }
    }
}