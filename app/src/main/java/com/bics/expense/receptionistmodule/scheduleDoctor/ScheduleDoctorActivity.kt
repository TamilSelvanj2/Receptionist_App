package com.bics.expense.receptionistmodule.scheduleDoctor

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bics.expense.receptionistmodule.R
import com.bics.expense.receptionistmodule.api.RetrofitClient
import com.bics.expense.receptionistmodule.dashboard.DashboardActivity
import com.bics.expense.receptionistmodule.databinding.ActivityScheduleDoctorBinding
import com.bics.expense.receptionistmodule.fragment.appointment.AppointmentFragment
import com.bics.expense.receptionistmodule.fragment.home.HomeFragment
import com.bics.expense.receptionistmodule.fragment.newRequest.NewRequestFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ScheduleDoctorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScheduleDoctorBinding
    private val specialityList = ArrayList<Speciality>()
    private val doctors = ArrayList<SpecialityId>()
    private val emptyGuidFormat = "00000000-0000-0000-0000-000000000000"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_schedule_doctor)

        setSupportActionBar(binding.toolbar)

        supportActionBar?.title = "Consultation Info"

        // Enable the back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        val patientID = intent.getStringExtra("patientID") ?: ""


        fetchAllSpecialities()
// Inside your activity or fragment class

// Setting click listener for the button
        binding.buttonNext.setOnClickListener {
            val specialityValidationMessage = specialityValid()
            val noteValidationMessage = doctorValid()
            val noteContent = binding.editTextNotes.text.toString().trim()
            var isOtherDoctor = false

            binding.textFieldDoctor1.helperText = specialityValidationMessage
            binding.textFieldNotes.helperText = noteValidationMessage

            if (specialityValidationMessage != null || noteValidationMessage != null) {
                // Show error message if any validation fails
                return@setOnClickListener
            } else {
                val selectedSpeciality = specialityList.find { it.speciality == binding.editTextSpecialityName.text.toString().trim() }
                val specialityID = selectedSpeciality?.specialityID ?: 0
                val selectedDoctor = doctors.find { it.toString() == binding.editTextDoctor.text.toString().trim() }

                if (selectedDoctor == null)
                {
                    isOtherDoctor = true
                }

                val doctorID = selectedDoctor?.doctorID ?: emptyGuidFormat

                createAppointment(
                    doctorID,
                    patientID,
                    specialityID,
                    noteContent.toString(),
                    isOtherDoctor,
                    binding.editTextNotes.text.toString().trim(),
                    binding.editTextSpecialityName.text.toString().trim()

                )
            }
        }


// Setting item selection listener for the speciality field
        binding.editTextSpecialityName.setOnItemClickListener { _, _, position, _ ->
            val selectedSpeciality = specialityList[position]

            fetchGetDoctorBasedOnSpecialities(selectedSpeciality.specialityID)
        }
    }
        // Validation function for speciality field
        private fun specialityValid(): String? {
            val specialityName = binding.editTextSpecialityName?.text.toString()
            return if (specialityName.isEmpty()) {
                "Please select the speciality Name"
            } else {
                null
            }
        }

        // Validation function for doctor field
        private fun doctorValid(): String? {
            val doctorName = binding.editTextNotes?.text.toString()
            return if (doctorName.isEmpty()) {
                "Please Enter Your Notes"
            } else {
                null
            }

        }
            private fun fetchAllSpecialities() {
                val sharedPreferences = getSharedPreferences("your_preference_name", Context.MODE_PRIVATE)
                val token = sharedPreferences.getString("token", "")

                RetrofitClient.setAuthToken(token)

                RetrofitClient.apiService.getAllSpecialities("Bearer $token")
                    .enqueue(object : Callback<PatientResponse> {
                        override fun onResponse(
                            call: Call<PatientResponse>,
                            response: Response<PatientResponse>
                        ) {
                            if (response.isSuccessful) {
                                val specialities = response.body()?.data
                                specialityList.clear()
                                specialities?.let {
                                    specialityList.addAll(it)
                                    val specialityNames = it.map { speciality -> speciality.speciality }
                                    val adapter = ArrayAdapter(this@ScheduleDoctorActivity, android.R.layout.simple_dropdown_item_1line, specialityNames)
                                    binding.editTextSpecialityName.setAdapter(adapter)
                                }
                            } else {
                                Toast.makeText(
                                    this@ScheduleDoctorActivity,
                                    "Failed to fetch specialities",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }

                        override fun onFailure(call: Call<PatientResponse>, t: Throwable) {
                            Toast.makeText(
                                this@ScheduleDoctorActivity,
                                "Failed to fetch specialities",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    })
            }

            private fun fetchGetDoctorBasedOnSpecialities(specialityId: Int) {
                val sharedPreferences = getSharedPreferences("your_preference_name", Context.MODE_PRIVATE)
                val token = sharedPreferences.getString("token", "")

                RetrofitClient.setAuthToken(token)

                RetrofitClient.apiService.getDoctorsBasedOnSpeciality(specialityId.toString(), "Bearer $token"
                ).enqueue(object : Callback<DoctorResponse> {
                    override fun onResponse(
                        call: Call<DoctorResponse>,
                        response: Response<DoctorResponse>
                    ) {
                        if (response.isSuccessful) {
                            val doctorResponse = response.body()
                            doctors.clear()
                            doctorResponse?.data?.let {
                                doctors.addAll(it)
                                val doctorNames = it.map { doctor -> "Dr.${doctor.firstName} ${doctor.lastName}" }
                                val adapter = ArrayAdapter(this@ScheduleDoctorActivity, android.R.layout.simple_dropdown_item_1line, doctorNames)
                                binding.editTextDoctor.setAdapter(adapter)
                            }
                        } else {
                            Toast.makeText(
                                this@ScheduleDoctorActivity,
                                "Failed to fetch doctors",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<DoctorResponse>, t: Throwable) {
                        Toast.makeText(
                            this@ScheduleDoctorActivity,
                            "Failed to fetch doctors",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                })
            }

            private fun createAppointment(
                doctorID: String,
                patientID: String,
                specialityID: Int,
                note: String,
                isOtherDoctor: Boolean,
                doctorName: String,
                specialityName: String
            ) {

                binding.progressBarScheduleDoctor.visibility = View.VISIBLE

                val sharedPreferences = getSharedPreferences("your_preference_name", Context.MODE_PRIVATE)
                val token = sharedPreferences.getString("token", "")
5
                RetrofitClient.setAuthToken(token)

                // Assume patient account information is retrieved from intent extras
                val userId = intent.getStringExtra("userId") ?: ""
                val patientName = intent.getStringExtra("patientName") ?: ""
                val patientEmail = intent.getStringExtra("patientEmail") ?: ""
                val patientGender = intent.getStringExtra("patientGender") ?: ""
                val patientMobile = intent.getStringExtra("patientMobile") ?: ""
                val patientDate = intent.getStringExtra("patientDate") ?: ""


                val appointmentRequest = CreateAppointmentRequest(specialityID, doctorID, patientID, isOtherDoctor, note)

                RetrofitClient.apiService.createAppointment("Bearer $token", appointmentRequest)
                    .enqueue(object : Callback<AppointmentResponse> {
                        override fun onResponse(call: Call<AppointmentResponse>, response: Response<AppointmentResponse>) {

                            binding.progressBarScheduleDoctor.visibility = View.GONE


                            if (response.isSuccessful) {
                                val appointmentResponse = response.body()
                                if (appointmentResponse?.success == true) {
                                    // Handle appointment creation success

                                    Toast.makeText(this@ScheduleDoctorActivity, "Appointment created successfully", Toast.LENGTH_SHORT).show()


                                    finish()

//                                    val doctorFcmToken = getDoctorFcmToken(doctorID) // Implement this method to retrieve the token
//                                    // Send notification to the patient
//                                    NotificationSender.sendNotification(this@ScheduleDoctorActivity, "Appointment Created", "Appointment with $doctorName scheduled successfully", patientID // Replace with the patient's FCM token
//                                    )
//                                    // Send notification to the doctor
//                                    NotificationSender.sendNotification(this@ScheduleDoctorActivity, "New Appointment", "You have a new appointment with $patientName", doctorFcmToken)
//                                    // Redirect to patient details activity or any other activity as needed

                                } else {
                                    // Handle appointment creation failure
                                    if (response.code() == 200) {
                                        Toast.makeText(this@ScheduleDoctorActivity, appointmentResponse?.error, Toast.LENGTH_LONG).show()
                                    } else {
                                        Toast.makeText(this@ScheduleDoctorActivity, "Failed to create appointment: ${appointmentResponse?.error}", Toast.LENGTH_LONG).show()
                                    }
                                }
                            } else {
                                // Log response error if any
                                Log.e(
                                    "AppointmentCreation",
                                    "Response error: ${response.errorBody()?.string()}"
                                )
                                Toast.makeText(
                                    this@ScheduleDoctorActivity,
                                    "Failed to create appointment",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }

                        override fun onFailure(call: Call<AppointmentResponse>, t: Throwable) {
                            // Handle failure to make the appointment request
                            binding.progressBarScheduleDoctor.visibility = View.GONE

                            Toast.makeText(
                                this@ScheduleDoctorActivity,
                                "Failed to create appointment error",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    })
            }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }


    private fun getDoctorFcmToken(doctorID: String): String {
                    // Placeholder: Retrieve the doctor's FCM token from your storage
                    return "2de444438539c0415e894dcd3ccb5e8cda99a134"
                }
            }



