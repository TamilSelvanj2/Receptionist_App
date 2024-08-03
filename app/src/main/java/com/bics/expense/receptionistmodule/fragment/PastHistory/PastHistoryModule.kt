package com.bics.expense.receptionistmodule.fragment.PastHistory



data class PastHistoryModule(

    val success: Boolean,
    val error: String,
    val data: List<PastHistoryResponse>,
    val statusCode: Int
)