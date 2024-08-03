package com.bics.expense.receptionistmodule.fragment.doctor

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.bics.expense.receptionistmodule.R
import com.bics.expense.receptionistmodule.api.RetrofitClient
import com.bics.expense.receptionistmodule.fragment.AppointmentDetail
import com.bics.expense.receptionistmodule.fragment.AppointmentDetails
import com.bics.expense.receptionistmodule.fragment.DoctorDetail
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DoctorDetailsFragment : Fragment() {

    private lateinit var doctorId : TextView
    private lateinit var doctorName : TextView
    private lateinit var doctorEmail : TextView
    private lateinit var doctorPhone : TextView
    private lateinit var doctorConsulting : TextView
    private lateinit var qulification : TextView
    private lateinit var experince : TextView
    private lateinit var specaiality : TextView
    private lateinit var language : TextView
    private lateinit var gender : TextView
    private lateinit var progressBar: ProgressBar

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val  view =inflater.inflate(R.layout.fragment_doctor_details, container, false)

        doctorId = view.findViewById(R.id.doctorIDTextView)
        doctorName = view.findViewById(R.id.doctorNameTextView)
        doctorEmail = view.findViewById(R.id.doctorEmailTextView)
        doctorPhone = view.findViewById(R.id.doctorNumberTextView)
        doctorConsulting = view.findViewById(R.id.consultingFeesTextView)
        qulification = view.findViewById(R.id.qualificationTextView)
        experince = view.findViewById(R.id.experienceTextView)
        specaiality = view.findViewById(R.id.doctorSpecailtyTextView)
        gender = view.findViewById(R.id.doctorGenderTextView)
        language = view.findViewById(R.id.doctor)
        progressBar = view.findViewById(R.id.progressBarDoctorDetails)

        val appointmentID = arguments?.getString("APPOINTMENT_ID")

        if (appointmentID != null) {
            fetchDoctorDetails(appointmentID)
        } else {
            Toast.makeText(requireContext(), "Invalid appointment ID", Toast.LENGTH_SHORT).show()
        }

    return view
    }

    private fun fetchDoctorDetails(appointmentID : String){
        progressBar.visibility = View.VISIBLE // Show progress bar

        if (!isAdded) {
            return
        }

        val sharedPreferences = requireContext().getSharedPreferences("your_preference_name", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", "")

        val apiService = RetrofitClient.apiService

        token?.let { authToken ->
            RetrofitClient.setAuthToken(authToken)
        }
      apiService.getAppointmentDetails(appointmentID)
        .enqueue(object : Callback<AppointmentDetails> {
            override fun onResponse(
                call: Call<AppointmentDetails>,
                response: Response<AppointmentDetails>
            ) {

                progressBar.visibility = View.GONE

                if (response.isSuccessful) {
                    val doctorDetails = response.body()
                    if (doctorDetails != null && doctorDetails.success == true) {
                        displayDoctorDetails(doctorDetails.data?.doctorDetail)
                    } else {
                        Toast.makeText(requireContext(), "Failed to get details", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Error: ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<AppointmentDetails>, t: Throwable) {
                progressBar.visibility = View.GONE

                Toast.makeText(
                    requireContext(),
                    "Failed to get details: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun displayDoctorDetails(doctorDetail: DoctorDetail?){

        doctorDetail?.let {

            doctorId.text = ": ${it.userID}"
            doctorName.text = ": ${it.name}"
            doctorEmail.text = ": ${it.email}"
            doctorPhone.text = ": ${it.phone}"
            doctorConsulting.text = ": ${it.consultingFees}"
            specaiality.text = ": ${it.speciality}"
            language.text = ": ${it.language}"
            gender.text = ": ${it.gender}"
            qulification.text = ": ${it.qualification}"
            experince.text =": ${ it.experience?.toInt()?.let { exp -> "$exp years" } ?: ""}"

        }

    }
}