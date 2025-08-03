package com.example.beaceful.core.network.appointment

import com.google.gson.annotations.SerializedName


data class AppointmentRequest(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("status")
    val status: String,
    @SerializedName("appointmentDate")
    val appointmentDate: String,
    @SerializedName("appointmentTime")
    val appointmentTime: String?,
    @SerializedName("rating")
    val rating: Double? = null,
    @SerializedName("review")
    val review: String? = null,
    @SerializedName("patientId")
    val patientId: String,
    @SerializedName("doctorId")
    val doctorId: String,
    @SerializedName("note")
    val note: String? = null
)