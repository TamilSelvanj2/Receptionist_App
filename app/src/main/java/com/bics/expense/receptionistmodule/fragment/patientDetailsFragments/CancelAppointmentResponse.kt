package com.bics.expense.receptionistmodule.fragment.patientDetailsFragments

data class CancelAppointmentResponse(
    val success: Boolean,
    val error: String,
    val data: AppointmentData?,
    val statusCode: Int
)

data class AppointmentData(
    val message: String
)
