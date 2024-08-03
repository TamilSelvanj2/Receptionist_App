package com.bics.expense.receptionistmodule.forgotpassword

data class ForgotPasswordResponse(
    val success: Boolean,
    val error: String,
    val data: ForgotPasswordData,
    val statusCode: Int
)

data class ForgotPasswordData(
    val message: String
)
