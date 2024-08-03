package com.bics.expense.receptionistmodule.fragment.patient


import com.google.gson.annotations.SerializedName


data class PatientsResponse (

    @SerializedName("success") var success: Boolean,
    @SerializedName("error") var error: String,
    @SerializedName("data") var data: List<PatientModule>?,
    @SerializedName("statusCode") var statusCode: Int
)