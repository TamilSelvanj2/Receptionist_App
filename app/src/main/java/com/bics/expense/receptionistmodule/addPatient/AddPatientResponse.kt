package com.bics.expense.receptionistmodule.addPatient

data class AddPatientResponse(
    val success: Boolean,
    val error: String?,
    val data: Data?,
    val statusCode: Int
)

data class Data(
    val patientID: String?,
    val userID: String?,

)
