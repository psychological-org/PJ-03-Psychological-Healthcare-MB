package com.example.beaceful.core.network.appointment

import com.example.beaceful.domain.model.Appointment
import com.example.beaceful.domain.model.PagedResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface AppointmentApiService {
    @GET("appointments")
    suspend fun getAppointments(
        @Query("page") page: Int = 0,
        @Query("limit") limit: Int = 10
    ): PagedResponse<AppointmentRequest>

    @GET("appointments/{appointment-id}")
    suspend fun getAppointmentById(@Path("appointment-id") id: Int): AppointmentRequest

    @POST("appointments")
    suspend fun createAppointment(@Body appointmentRequest: AppointmentRequest): Int

    @PUT("appointments")
    suspend fun updateAppointment(@Body appointmentRequest: AppointmentRequest)

    @DELETE("appointments/{appointment-id}")
    suspend fun deleteAppointment(@Path("appointment-id") id: Int)
}

