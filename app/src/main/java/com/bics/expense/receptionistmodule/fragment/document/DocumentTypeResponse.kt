package com.bics.expense.receptionistmodule.fragment.document

data class DocumentTypesResponse(
    val success: Boolean,
    val error: String,
    val data: List<DocumentType>,
    val statusCode: Int
)

data class DocumentType(
    val documentTypeID: String,
    val documentType: String
)
