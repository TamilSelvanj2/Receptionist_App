package com.bics.expense.receptionistmodule.fragment.Upcoming



data class UpcomingModule(
    val success: Boolean,
    val error: String,
    val data: List<UpcomingResponse>,
    val statusCode: Int
)
