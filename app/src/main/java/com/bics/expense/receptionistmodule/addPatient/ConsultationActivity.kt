package com.bics.expense.receptionistmodule.addPatient

import com.bics.expense.receptionistmodule.scheduleDoctor.ScheduleDoctorActivity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bics.expense.receptionistmodule.R
import com.bics.expense.receptionistmodule.api.RetrofitClient
import com.bics.expense.receptionistmodule.dashboard.DashboardActivity
import com.bics.expense.receptionistmodule.databinding.ActivityConsultationBinding
import retrofit2.Call
import retrofit2.Response
import java.util.Calendar

class ConsultationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConsultationBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_consultation)

        setSupportActionBar(binding.toolbar)

        // Set action bar title
        supportActionBar?.title = "CONSULTATION INFO"

        // Enable the back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        val phone = intent.getStringExtra("phoneNumber")
        binding.editTextMobileNumber.setText(phone)



        binding.buttonNext.setOnClickListener {
            if (validateInput()) {
                val patientDetails = getSelectedGender()?.let { it1 ->
                    PatientDetails(
                        firstName = binding.editTextPatientName.text.toString().trim(),
                        lastName = binding.editTextPatientlastName?.text.toString().trim(),
                        dateOfBirth = binding.editTextDate.text.toString().trim(),
                        gender = it1,
                        phone = binding.editTextMobileNumber.text.toString().trim(),
                        email = binding.editTextEmail.text.toString().trim()
                    )
                }

                if (patientDetails != null) {
                    addPatient(patientDetails)
                }
            }
        }

        binding.editTextDate.setOnClickListener {
            showDatePickerDialog()
        }
    }

    private fun validateInput(): Boolean {
        val patientNameError = validPatientName()
        val dateOfBirthError = validDate()
        val emailError = validEmail()

        binding.usernameTextInputLayout.helperText = patientNameError
        binding.textFieldDate.helperText = dateOfBirthError
        binding.textFieldEmail.helperText = emailError

        val gender = getSelectedGender()
        val mobileNumber = binding.editTextMobileNumber.text.toString().trim()

        if (patientNameError != null || dateOfBirthError != null || emailError != null || gender.isNullOrEmpty() || mobileNumber.isEmpty()) {
            return false
        }
        return true
    }

    private fun validPatientName(): String? {
        val patientName = binding.editTextPatientName.text.toString().trim()
        return when{
            patientName.isEmpty() -> "Enter the Patient Name"
            else -> null
        }
    }

    private fun validDate(): String? {
        val dateOfBirth = binding.editTextDate.text.toString().trim()
        return when{
            dateOfBirth.isEmpty() -> "Click and selected the Date Of Birth"
            else -> null
        }
    }
    private fun validEmail(): String? {
        val PatientEmail = binding.editTextEmail.text.toString().trim()
        return when{
            PatientEmail.isEmpty() -> "Enter Email ID"
            !isValidEmail(PatientEmail) -> "Enter a valid Email ID"
            else -> null
        }
    }


    private fun addPatient(patientDetails: PatientDetails) {
        val apiService = RetrofitClient.apiService
        apiService.addPatient(patientDetails).enqueue(object : retrofit2.Callback<AddPatientResponse> {
            override fun onResponse(call: Call<AddPatientResponse>, response: Response<AddPatientResponse>) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null && apiResponse.success) {
                        val patientID = apiResponse.data?.patientID

                        Toast.makeText(this@ConsultationActivity, "Patient added successfully", Toast.LENGTH_SHORT).show()
                        finish()

                        // If patient ID is available, proceed to ScheduleDoctorActivity
                        if (!patientID.isNullOrEmpty()) {
                            val intentcon = Intent(this@ConsultationActivity, ScheduleDoctorActivity::class.java).apply {
                                putExtra("name", patientDetails.firstName)
                                putExtra("email", patientDetails.email)
                                putExtra("mobileNumber", patientDetails.phone)
                                putExtra("gender", patientDetails.gender)
                                putExtra("patientID", patientID) // Pass the patient ID to ScheduleDoctorActivity
                            }
                            startActivity(intentcon)
                        } else {
                            Toast.makeText(this@ConsultationActivity, "Patient ID is missing", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@ConsultationActivity, "Failed to add patient: ${apiResponse?.error}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@ConsultationActivity, "Failed to add patient", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AddPatientResponse>, t: Throwable) {
                Toast.makeText(this@ConsultationActivity, "Error add patient: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun showDatePickerDialog() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, monthOfYear, dayOfMonth ->
                val formattedDate = String.format("%d-%02d-%02d", year, monthOfYear + 1, dayOfMonth)
                binding.editTextDate.setText(formattedDate)
                binding.editTextDate.error = null
                // Remove focus from gender field
                binding.radioGroup.clearFocus()
            },
            year,
            month,
            day
        )
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()

        datePickerDialog.show()
    }

    private fun getSelectedGender(): String? {

        val selectedRadioButtonId = binding.radioGroup.checkedRadioButtonId
        val selectedRadioButton = findViewById<RadioButton>(selectedRadioButtonId)

        return selectedRadioButton?.text?.toString()
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\$")
        return emailRegex.matches(email)
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
