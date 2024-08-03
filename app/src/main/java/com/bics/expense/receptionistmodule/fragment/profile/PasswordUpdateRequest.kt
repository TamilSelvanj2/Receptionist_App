package com.bics.expense.receptionistmodule.fragment.profile

data class PasswordUpdateRequest(
    val accountId: String,
    val password: String,
    val confirmPassword: String
)
