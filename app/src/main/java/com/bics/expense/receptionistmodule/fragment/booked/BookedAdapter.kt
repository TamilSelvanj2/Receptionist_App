package com.bics.expense.receptionistmodule.fragment.booked

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bics.expense.receptionistmodule.R
import com.bics.expense.receptionistmodule.fragment.booked.BookedModel
import com.bics.expense.receptionistmodule.patientDetails.PatientDetailsActivity
import kotlin.coroutines.coroutineContext

class   BookedAdapter : ListAdapter<BookedModel, BookedAdapter.BookedViewHolder>(BookedDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookedViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.cardview_booking, parent, false)
        return BookedViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BookedViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }


    inner class BookedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val appointmentIDTextView: TextView = itemView.findViewById(R.id.text_appointment_id)
        private val doctorNameTextView: TextView = itemView.findViewById(R.id.text_doctor_name)
        private val specailtyNameTextView: TextView = itemView.findViewById(R.id.text_specialty)
        private val statusCodeTextView: TextView = itemView.findViewById(R.id.text_state_code)
        private val patientNameTextView: TextView = itemView.findViewById(R.id.text_patientName)

        fun bind(bookedModel: BookedModel) {
            // Bind your data to the item view here
            appointmentIDTextView.text = bookedModel.appointmentID
            doctorNameTextView.text = bookedModel.doctorName
            specailtyNameTextView.text = bookedModel.speciality
            statusCodeTextView.text = bookedModel.status
            patientNameTextView.text = bookedModel.patientName


//            itemView.setOnClickListener {
//                val bookedModel = getItem(adapterPosition)
//
//                val intent = Intent(itemView.context,PatientDetailsActivity::class.java)
//
//                intent.putExtra("appointmentID", bookedModel.appointmentID)
//                intent.putExtra("patientId", bookedModel.patientID)
//                intent.putExtra("userId", bookedModel.userID)
//                intent.putExtra("status", bookedModel.status)
//
//                // Start the activity
//                itemView.context.startActivity(intent)
//            }
        }

    }


    private class BookedDiffCallback : DiffUtil.ItemCallback<BookedModel>() {
        override fun areItemsTheSame(oldItem: BookedModel, newItem: BookedModel): Boolean {
            return oldItem.appointmentID == newItem.appointmentID
        }

        override fun areContentsTheSame(oldItem: BookedModel, newItem: BookedModel): Boolean {
            return oldItem == newItem
        }
    }
}
