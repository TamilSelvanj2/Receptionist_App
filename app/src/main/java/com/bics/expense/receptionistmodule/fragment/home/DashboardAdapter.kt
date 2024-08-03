package com.bics.expense.receptionistmodule.fragment.home

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bics.expense.receptionistmodule.R
import com.bics.expense.receptionistmodule.scheduleDoctor.ScheduleDoctorActivity


class DashboardAdapter : RecyclerView.Adapter<DashboardAdapter.ViewHolder>() {

    private var patients: List<DashboardData> = emptyList()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val firstNameTextView: TextView = itemView.findViewById(R.id.textViewName)
        private val genderTextView: TextView = itemView.findViewById(R.id.textViewGender)
        private val ageTextView: TextView = itemView.findViewById(R.id.textViewAge)
        private val emailTextView: TextView = itemView.findViewById(R.id.textViewMail)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(patient: DashboardData) {
            firstNameTextView.text = patient.firstName
            genderTextView.text = patient.gender
            emailTextView.text = patient.email
            ageTextView.text = patient.age.toString()
        }

        override fun onClick(view: View) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val context = view.context
                val patient = patients[position]

                // Start the next activity with the patient data
                val intent = Intent(context, ScheduleDoctorActivity::class.java).apply {
                    val fullName = "${patient.firstName} ${patient.lastName}"

                    putExtra("patientID", patient.patientID)
                    putExtra("userId",patient.userID)
                    putExtra("patientName", fullName)
                    putExtra("patientEmail", patient.email)
                    putExtra("patientGender", patient.gender)
                    putExtra("patientMobile", patient.phone)
                    putExtra("patientDate", patient.dateOfBirth)
                    putExtra("patientAge", patient.age)
                }
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.dashboard_cardview, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(patients[position])
    }

    override fun getItemCount(): Int {
        return patients.size
    }

    fun submitList(newPatients: List<DashboardData>) {
        patients = newPatients
        notifyDataSetChanged()
    }
}
