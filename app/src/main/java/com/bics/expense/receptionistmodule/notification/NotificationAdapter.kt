package com.bics.expense.receptionistmodule.notification

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bics.expense.receptionistmodule.R
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit


class NotificationAdapter(private val appointments: List<NotificationData>) :
    RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    inner class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewAppointmentID: TextView = itemView.findViewById(R.id.textViewAppointmentID)
        val textViewStatusID: TextView = itemView.findViewById(R.id.textViewStatusId)
        val textViewDate: TextView = itemView.findViewById(R.id.Days)
        // Add more TextViews for other appointment details as needed
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.notification, parent, false)
        return NotificationViewHolder(itemView)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val currentItem = appointments[position]

        holder.textViewAppointmentID.text = " ${currentItem.name}, ${currentItem.appointmentID}"
        holder.textViewStatusID.text = getStatusName(currentItem.statusID)
        holder.textViewStatusID.setTextColor(getStatusColor(currentItem.statusID, holder.itemView))

        val notificationDate = ZonedDateTime.parse(currentItem.notificationDatetime, DateTimeFormatter.ISO_ZONED_DATE_TIME)

        // Get the current date
        val currentDate = ZonedDateTime.now()

        // Calculate the difference in days and minutes
        val daysDifference = ChronoUnit.DAYS.between(notificationDate, currentDate)
        val minutesDifference = ChronoUnit.MINUTES.between(notificationDate, currentDate)

        // Display the difference in days or minutes
        if (daysDifference > 0) {
            holder.textViewDate.text = "$daysDifference days ago"
        } else {
            holder.textViewDate.text = "$minutesDifference minutes ago"
        }

        // Bind other appointment details to their respective TextViews
    }

    override fun getItemCount() = appointments.size
    private fun getStatusName(statusID: Int): String {
        return when (statusID) {
            1 -> "PreBookAppointment"
            2 -> "Accepted"
            3 -> "Rejected"
            4 -> "PaymentCompleted"
            5 -> "AppointmentCompleted"
            6 -> "Cancelled"
            else -> "Unknown Status"
        }
    }
    private fun getStatusColor(statusID: Int, view: View): Int {
        return when (statusID) {
            1 -> ContextCompat.getColor(view.context, R.color.preBookAppointmentColor)
            2 -> ContextCompat.getColor(view.context, R.color.acceptedColor)
            3 -> ContextCompat.getColor(view.context, R.color.rejectedColor)
            4 -> ContextCompat.getColor(view.context, R.color.paymentCompletedColor)
            5 -> ContextCompat.getColor(view.context, R.color.appointmentCompletedColor)
            6 -> ContextCompat.getColor(view.context, R.color.cancelledColor)
            else -> ContextCompat.getColor(view.context, android.R.color.black)
        }
    }
}