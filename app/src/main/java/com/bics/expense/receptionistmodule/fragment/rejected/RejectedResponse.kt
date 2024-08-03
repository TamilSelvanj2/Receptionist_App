package com.bics.expense.receptionistmodule.fragment.rejected

import java.util.Date

data class RejectedResponse(
    val appointmentID: String,
    val appointmentDate: String,
    val doctorName: String,
    val patientName: String,
    val speciality: String,
    val appointmentStartDateAndTime: String,
    val appointmentEndDateAndTime: String,
    val status: String,
    val reason : String
)