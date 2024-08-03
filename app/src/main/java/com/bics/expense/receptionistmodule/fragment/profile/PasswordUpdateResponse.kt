package com.bics.expense.receptionistmodule.fragment.profile

data class PasswordUpdateResponse(
    val success: Boolean,
    val error: String?,
    val data: Any?, // Adjust the type based on your API response
    val statusCode: Int
)
