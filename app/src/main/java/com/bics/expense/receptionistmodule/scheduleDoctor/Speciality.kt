package com.bics.expense.receptionistmodule.scheduleDoctor



import com.google.gson.annotations.SerializedName


data class Speciality(
    @SerializedName("specialityID") val specialityID: Int,
    @SerializedName("speciality") val speciality: String,
    @SerializedName("specialityImage") val specialityImage: String?,
    @SerializedName("isActive") val isActive: Boolean
)
