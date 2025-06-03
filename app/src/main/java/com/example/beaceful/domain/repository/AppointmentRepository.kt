package com.example.beaceful.domain.repository

import com.example.beaceful.domain.model.DumpDataProvider
import com.example.beaceful.domain.model.Appointment
import com.example.beaceful.domain.model.AppointmentStatus
import com.example.beaceful.domain.model.User
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppointmentRepository @Inject constructor() {
    fun getAppointmentsOfDoctor(doctorId: Int): List<Appointment> =
        DumpDataProvider.appointments.filter { it.doctorId == doctorId }.sortedBy { it.appointmentDate }
    fun getAppointmentsOfDoctorByStatus(doctorId: Int, status: AppointmentStatus): List<Appointment> =
        DumpDataProvider.appointments.filter { it.doctorId == doctorId && it.status == status }
    fun getAppointmentsOnDate(date: LocalDateTime): List<Appointment> =
        DumpDataProvider.appointments.filter { it.appointmentDate.toLocalDate() == date.toLocalDate() }
    fun getAppointmentById(appointmentId: Int): Appointment? =
        DumpDataProvider.appointments.find { it.id == appointmentId }

    fun getUserById(userId: Int): User? =
        DumpDataProvider.listUser.find { it.id == userId }

    fun getPatientByAppointment(appointment: Appointment): User? =
        getUserById(appointment.patientId)

    fun getPatientByAppointmentId(appointmentId: Int): User? =
        getAppointmentById(appointmentId)?.let { getUserById(it.patientId) }

    fun getAllPatientsOfDoctor(
        doctorId: Int
    ): List<User> {
        val patientIds = getAppointmentsOfDoctor(doctorId)
            .map { it.patientId }
            .toSet()
        return DumpDataProvider.listUser.filter { it.id in patientIds }
    }

    fun getAllPatientsOfDoctorByStatus(
        doctorId: Int,
        status: AppointmentStatus
    ): List<User> {
        val patientIds = getAppointmentsOfDoctorByStatus(doctorId, status)
            .map { it.patientId }
            .toSet()
        return DumpDataProvider.listUser.filter { it.id in patientIds }
    }
    fun getAllAppointmentsOfPatient(patientId: Int) : List<Appointment> = DumpDataProvider.appointments.filter { it.patientId == patientId }


}