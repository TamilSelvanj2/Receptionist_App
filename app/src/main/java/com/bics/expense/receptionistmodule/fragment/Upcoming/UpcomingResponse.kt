package com.bics.expense.receptionistmodule.fragment.Upcoming



data class UpcomingResponse(
    val appointmentID: String,
    val appointmentDate: String,
    val doctorName: String,
    val patientName: String,
    val speciality: String,
    val appointmentStartDateAndTime: String,
    val appointmentEndDateAndTime: String,
    val status: String
)