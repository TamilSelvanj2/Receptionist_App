package com.bics.expense.receptionistmodule.fragment.document

data class DeleteResponse(
    val success: Boolean,
    val error: String,
    val data: DeleteResponseData,
    val statusCode: Int
)

//data class DeleteResponseData(
//    val message: String
//)
