package com.bics.expense.receptionistmodule.fragment.document


data class AddDocumentRequest(
    val success: Boolean,
    val error: String?,
    val data: ApiData?,
    val statusCode: Int
)

data class ApiData(
   val message: String
)
