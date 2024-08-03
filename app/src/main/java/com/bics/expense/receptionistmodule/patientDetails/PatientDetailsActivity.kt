package com.bics.expense.receptionistmodule.patientDetails

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bics.expense.receptionistmodule.R
import com.bics.expense.receptionistmodule.databinding.ActivityPatientDetailsBinding
import com.bics.expense.receptionistmodule.fragment.appointment.PatientUpcomingPastFragment


class PatientDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPatientDetailsBinding
    private var patientID: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_patient_details)



        setSupportActionBar(binding.toolbar)

        supportActionBar?.title = "PATIENT DETAILS"

        // Enable the back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        if (savedInstanceState == null) {
            val appointmentID = intent.getStringExtra("APPOINTMENT_ID")
            val patientID = intent.getStringExtra("patientId") ?: ""

            val fragment = PatientUpcomingPastFragment().apply {
                arguments = Bundle().apply {
                    putString("APPOINTMENT_ID", appointmentID)
                    putString("PATIENT_ID", patientID)
                }
            }
            setFragment(fragment)
        }
    }


    // Function to set the fragment
    private fun setFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragments_containerPatient, fragment)
        fragmentTransaction.commit()
    }
}





//        patientID = intent.getStringExtra("patientId")
//        val patientStatus = intent.getStringExtra("status")
//        val patientTimer = intent.getStringExtra("countdown_timer")
//
//
//
//        fetchPatientDetails(patientID, patientStatus, patientTimer)


//    }
//}


//    private fun fetchPatientDetails(
//        patientID: String?,
//        patientStatus: String?,
//        patientTimer: String?
//    ) {
//        if (!patientID.isNullOrBlank()) {
//            val service = RetrofitClient.apiService
//
//            val callPatientDetails = service.getPatientDetails(patientID)
//            callPatientDetails.enqueue(object : Callback<PatientDetailsModel> {
//                override fun onResponse(
//                    call: Call<PatientDetailsModel>,
//                    response: Response<PatientDetailsModel>
//                ) {
//                    if (response.isSuccessful) {
//                        val patientDetails = response.body()
//                        val callAppointmentDetails =
//                            service.getUpcomingAppointmentDetails(patientID)
//                        callAppointmentDetails.enqueue(object : Callback<CustomAppointment> {
//                            override fun onResponse(
//                                call: Call<CustomAppointment>,
//                                response: Response<CustomAppointment>
//                            ) {
//                                if (response.isSuccessful) {
//                                    val appointmentDetails = response.body()?.data
//                                    updateUIWithPatientDetails(
//                                        patientDetails,
//                                        appointmentDetails,
//                                        patientStatus
//                                    )
//                                } else {
//                                    // Handle unsuccessful response from the second API call
//                                }
//                            }
//
//                            override fun onFailure(call: Call<CustomAppointment>, t: Throwable) {
//                                // Handle failure
//                            }
//                        })
//                    } else {
//                        // Handle unsuccessful response from the first API call
//                    }
//                }
//
//                override fun onFailure(call: Call<PatientDetailsModel>, t: Throwable) {
//                    // Handle failure
//                }
//            })
//        } else {
//            // Handle the case where patientID is empty
//        }
//    }
//
//    private fun updateUIWithPatientDetails(
//        patientDetails: PatientDetailsModel?,
//        appointmentDetails: CustomAppointmentData?,
//        patientStatus: String?
//    ) {
//        patientDetails?.let {
//            binding.apply {
//                textViewPatientId.text = it.data.userID
//                textViewPatientName.text = it.data.name
//                textViewEmailID.text = it.data.email
//                textViewGender.text = it.data.gender
//                textViewMobilNumber.text = it.data.phone
//                textViewDate.text = it.data.dateOfBirth
//                textViewAge.text = it.data.age.toString()
//            }
//        }
//    }
//
//}
//
//
