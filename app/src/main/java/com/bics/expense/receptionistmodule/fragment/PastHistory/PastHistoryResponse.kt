package com.bics.expense.receptionistmodule.fragment.PastHistory

import java.util.Date

data class PastHistoryResponse(
    val appointmentID: String,
    val appointmentDate: String,
    val doctorName: String,
    val patientName: String,
    val speciality: String,
    val appointmentStartDateAndTime: String,
    val appointmentEndDateAndTime: String,
    val status: String
)