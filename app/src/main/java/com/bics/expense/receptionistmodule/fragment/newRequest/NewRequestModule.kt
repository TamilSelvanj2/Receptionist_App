package com.bics.expense.receptionistmodule.fragment.newRequest


data class NewRequestModule (
    val success: Boolean,
    val error: String,
    val data: List<NewRequestResponse>,
    val statusCode: Int
)
