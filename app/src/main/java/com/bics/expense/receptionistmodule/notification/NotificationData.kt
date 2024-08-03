package com.bics.expense.receptionistmodule.notification

data class NotificationData(
    val appointmentID: String,
    val accountID: String,
    val name : String,
    val patientID: String,
    val statusID: Int,
    val isOpened: Boolean,
    val isRejected: Boolean,
    val description: String,
    val notificationDatetime: String
)