package com.bics.expense.receptionistmodule.notification

data class NotificationResponse(
    val success: Boolean,
    val error: String?,
    val data: List<NotificationData>?,
    val statusCode: Int
)
