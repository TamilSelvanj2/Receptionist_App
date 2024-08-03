package com.bics.expense.receptionistmodule.fragment.home

import com.google.gson.annotations.SerializedName


data class DashboardResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("error") val error: String,
    @SerializedName("data") val data: List<DashboardData>,
    @SerializedName("statusCode") val statusCode: Int
)



