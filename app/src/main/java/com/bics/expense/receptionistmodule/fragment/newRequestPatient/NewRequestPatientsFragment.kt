package com.bics.expense.receptionistmodule.fragment.newRequestPatient

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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class NewRequestPatientsFragment : Fragment() {

    private lateinit var patientId : TextView
    private lateinit var patientName : TextView
    private lateinit var patientEmail : TextView
    private lateinit var patientMobile : TextView
    private lateinit var patientGender : TextView
    private lateinit var patientAge : TextView
    private lateinit var progressBar : ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_new_request_patients, container, false)

        patientId = view.findViewById(R.id.patientIDsTextView)
        patientName = view.findViewById(R.id.patientNamesTextView)
        patientEmail = view.findViewById(R.id.patientEmailsTextView)
        patientMobile = view.findViewById(R.id.patientNumbersTextView)
        patientGender = view.findViewById(R.id.PatientGendersTextView)
        patientAge = view.findViewById(R.id.PatientAgesTextView)
        progressBar = view.findViewById(R.id.progressBarRequestsPatient)

        val appointmentID = arguments?.getString("APPOINTMENT_ID")

        if (appointmentID != null){
            fetchPatientDetails(appointmentID)
        }else{
            Toast.makeText(requireContext(),"invalid appointment id", Toast.LENGTH_SHORT).show()
        }
        return view
    }
    private fun fetchPatientDetails(appointmentID: String){

        progressBar.visibility = View.VISIBLE // Show progress bar

        if (!isAdded || activity == null || requireContext() == null) {
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

                    if (response.isSuccessful){
                        val patientDetail = response.body()
                        if (patientDetail != null && patientDetail.success == true){
                            displayPatientDetails(patientDetail.data?.appointmentDetail)
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Failed to get details",
                                Toast.LENGTH_SHORT
                            ).show()
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
    private fun displayPatientDetails(patientDetails : AppointmentDetail?){

        patientDetails?. let {

            patientId.text = ": ${it.userID}"
            patientName.text = ": ${it.patientName}"
            patientEmail.text = ": ${it.email}"
            patientMobile.text = ": ${it.phoneNumber}"
            patientGender.text = ": ${it.gender}"
            patientAge.text= ": ${it.age}"
        }

    }
}