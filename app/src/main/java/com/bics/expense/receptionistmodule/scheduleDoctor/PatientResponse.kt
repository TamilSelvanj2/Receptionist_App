package com.bics.expense.receptionistmodule.scheduleDoctor

import com.google.gson.annotations.SerializedName

data class PatientResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("error") val error: String,
    @SerializedName("data") val data: ArrayList<Speciality>,
    @SerializedName("statusCode") val statusCode: Int
)
