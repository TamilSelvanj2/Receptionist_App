package com.bics.expense.receptionistmodule.fragment.home

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class DashboardData(
    @SerializedName("patientID") val patientID: String,
    @SerializedName("userID") val userID: String,
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName") val lastName: String,
    @SerializedName("name") val name: String?,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("dateOfBirth") val dateOfBirth: String,
    @SerializedName("age") val age: String,
    @SerializedName("gender") val gender: String,
    @SerializedName("createdBy") val createdBy: String,
    @SerializedName("dateCreated") val dateCreated: String
)