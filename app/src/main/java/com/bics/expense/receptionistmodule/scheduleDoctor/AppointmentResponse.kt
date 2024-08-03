package com.bics.expense.receptionistmodule.scheduleDoctor

import com.google.gson.annotations.SerializedName

data class AppointmentResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("error")
    val error: String?,
    @SerializedName("data")
    val data: Any?, // You can replace `Any?` with the actual type of `data` if it's known
    @SerializedName("statusCode")
    val statusCode: Int
)