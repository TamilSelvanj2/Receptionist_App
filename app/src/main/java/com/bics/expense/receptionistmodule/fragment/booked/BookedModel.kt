package com.bics.expense.receptionistmodule.fragment.booked

// Appointment.kt
data class BookedModel(
    val appointmentID: String,
    val patientID: String,
    val appointmentDate: String,
    val appointmentStartDateandTime: String,
    val appointmentEndDateandTime: String,
    val statusID: Int,
    val specialityID: Int,
    val notes: List<String>,
    val patientName: String,
    val doctorName: String,
    val consultingFees: Int,
    val speciality: String,
    val status: String,
    val email: String,
    val phoneNumber: String,
    val userID: String,
    val dateOfBirth: String,
    val gender: String,
    val age: Int?,
    val startTimeInMillis: Long // Change type to Long

)
