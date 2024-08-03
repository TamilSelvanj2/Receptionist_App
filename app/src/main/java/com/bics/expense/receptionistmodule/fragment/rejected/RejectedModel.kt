package com.bics.expense.receptionistmodule.fragment.rejected

data class RejectedModel(

    val success: Boolean,
    val error: String,
    val data: List<RejectedResponse>,
    val statusCode: Int
)
