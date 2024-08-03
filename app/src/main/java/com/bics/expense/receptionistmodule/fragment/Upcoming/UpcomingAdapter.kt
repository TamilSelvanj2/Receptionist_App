package com.bics.expense.receptionistmodule.fragment.Upcoming

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bics.expense.receptionistmodule.R
import com.bics.expense.receptionistmodule.dashboard.AppointmentBookingActivity
import com.bics.expense.receptionistmodule.fragment.rejected.RejectedResponse
import java.text.SimpleDateFormat
import java.util.Locale

class UpcomingAdapter (private var appointments: List<UpcomingResponse>): RecyclerView.Adapter<UpcomingAdapter.UpcomingViewHolder>() {


    fun submitList(upcomingList: List<UpcomingResponse>) {
        this.appointments = upcomingList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpcomingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_upcoming, parent, false)
        return UpcomingViewHolder(view)
    }

    override fun onBindViewHolder(holder: UpcomingViewHolder, position: Int) {
        val upcoming = appointments[position]
        holder.bind(upcoming)
    }

    override fun getItemCount(): Int {
        return appointments.size
    }

    inner class UpcomingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val appointmentId: TextView = itemView.findViewById(R.id.textViewAppointmentIdUpcoming)
        private val doctorName: TextView = itemView.findViewById(R.id.textViewDoctorNameUpcoming)
        private val patientName: TextView = itemView.findViewById(R.id.textViewPatientNamesUpcoming)
        private val speciality: TextView = itemView.findViewById(R.id.textViewSpecialitysUpcoming)
        private val status: TextView = itemView.findViewById(R.id.textViewStatusUpcoming)
        private val date: TextView = itemView.findViewById(R.id.dateUpcoming)
        private val time: TextView = itemView.findViewById(R.id.timeUpcoming)

        fun bind(appointment: UpcomingResponse) {




            val startDateTime = appointment.appointmentStartDateAndTime ?: ""
            val endDateTime = appointment.appointmentEndDateAndTime ?: ""

            val startTime = extractTime(startDateTime)
            val endTime = extractTime(endDateTime)

            // Format the time range
            val timeRange = "$startTime - $endTime"

            appointmentId.text = appointment.appointmentID
            doctorName.text = ":  ${appointment.doctorName}"
            patientName.text = ":  ${appointment.patientName}"
            speciality.text = ":  ${appointment.speciality}"
            date.text = appointment.appointmentDate
            time.text = timeRange

            if (appointment.status.equals("paymentCompleted", ignoreCase = true)) {
                status.text = ": CASH"
            } else {
                status.text = appointment.status
            }


            itemView.setOnClickListener {

                val intent = Intent(itemView.context, AppointmentBookingActivity::class.java)
                intent.putExtra("APPOINTMENT_ID", appointment.appointmentID)

                itemView.context.startActivity(intent)
            }

        }
        fun extractTime(dateTime: String): String {
            if (dateTime.isBlank()) return ""

            try {
                // Parse the complete date and time string
                val inputFormat = SimpleDateFormat("M/d/yyyy h:mm:ss a", Locale.getDefault())
                val parsedDate = inputFormat.parse(dateTime)

                // Format the parsed time to "h:mm a"
                val outputFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
                return outputFormat.format(parsedDate)
            } catch (e: Exception) {
                e.printStackTrace()
                return ""
            }
        }
    }
}

