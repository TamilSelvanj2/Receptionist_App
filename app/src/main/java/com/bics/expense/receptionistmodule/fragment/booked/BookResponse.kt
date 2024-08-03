package com.bics.expense.receptionistmodule.fragment.booked

import com.google.gson.annotations.SerializedName

data class BookResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("error") val error: String,
    @SerializedName("data") val data: List<BookedModel>?, // Change to List<BookedModel>?
    @SerializedName("statusCode") val statusCode: Int
)
