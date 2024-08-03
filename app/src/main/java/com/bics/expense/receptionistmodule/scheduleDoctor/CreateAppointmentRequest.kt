package com.bics.expense.receptionistmodule.scheduleDoctor

import com.google.gson.annotations.SerializedName

data class CreateAppointmentRequest(
    @SerializedName("specialityID")
    val specialityID: Int,

    @SerializedName("doctorID")
    val doctorID: String,

    @SerializedName("patientID")
    val patientID: String?,

    @SerializedName("isOtherDoctor")
    val isOtherDoctor: Boolean,

    @SerializedName("notes")
    val notes: String
)
