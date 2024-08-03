package com.bics.expense.receptionistmodule.fragment.document

import com.google.gson.annotations.SerializedName


data class DeleteDocumentRequest(
    @SerializedName("appointmentNotesID") val appointmentNotesID: String,
    @SerializedName("fileAttachment") val fileAttachment: String
)

data class DeleteDocumentResponse(
    val success: Boolean,
    val error: String?,
    val data: DeleteResponseData, // Adjust data type as per your API response
    val statusCode: Int
)

data class DeleteResponseData(
    val message: String
)
