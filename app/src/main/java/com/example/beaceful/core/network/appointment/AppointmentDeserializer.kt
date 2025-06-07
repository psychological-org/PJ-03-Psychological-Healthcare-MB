package com.example.beaceful.core.network.appointment

import com.example.beaceful.domain.model.Appointment
import com.example.beaceful.domain.model.AppointmentStatus
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.LocalTime
import java.time.LocalDateTime

class AppointmentDeserializer : JsonDeserializer<Appointment> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): Appointment {
        val jsonObject = json.asJsonObject
        val appointmentDate = LocalDate.parse(jsonObject.get("appointmentDate").asString)
        val appointmentTime = jsonObject.get("appointmentTime")?.asString?.let { LocalTime.parse(it) }
        val appointmentDateTime = if (appointmentTime != null) {
            LocalDateTime.of(appointmentDate, appointmentTime)
        } else {
            LocalDateTime.of(appointmentDate, LocalTime.of(0, 0))
        }

        return Appointment(
            id = jsonObject.get("id").asInt,
            status = AppointmentStatus.fromString(jsonObject.get("status").asString),
            patientId = jsonObject.get("patientId").asString,
            doctorId = jsonObject.get("doctorId").asString,
            appointmentDate = appointmentDateTime,
            note = jsonObject.get("note")?.asString,
            rating = jsonObject.get("rating")?.asDouble?.toInt(),
            review = jsonObject.get("review")?.asString
        )
    }
}