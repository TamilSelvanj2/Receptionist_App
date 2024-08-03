package com.bics.expense.receptionistmodule.forgotpassword

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.bics.expense.receptionistmodule.R
import com.bics.expense.receptionistmodule.api.RetrofitClient
import com.bics.expense.receptionistmodule.databinding.ActivityForgotBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotActivity : AppCompatActivity() {

    private lateinit var binding : ActivityForgotBinding
    private lateinit var sharedPreferences: SharedPreferences


    private  var userId: String? = null

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = DataBindingUtil.setContentView(this,R.layout.activity_forgot)


        setSupportActionBar(binding.toolbars)

        // Set action bar title
        supportActionBar?.title = " "

        // Enable the back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbars.setNavigationOnClickListener {
            onBackPressed()
        }


        userId = intent.getStringExtra("User_Id")

        // Set the user ID in the EditText if it's valid

            binding.editTextForgotPassword.setText(userId)


        binding.forgotBtn.setOnClickListener {
            val enteredUserId = binding.editTextForgotPassword.text.toString().trim()

            // Validate the entered user ID
            if (isValidUserId(enteredUserId)) {
                // Proceed to fetch data if the entered user ID is valid
                fetchData(enteredUserId)
            } else {
                // Show error message if the entered user ID is invalid
                binding.forgotErrorMessage.text = " Please enter a 10-digit number."
            }
        }
    }

    // Function to validate user ID
    private fun isValidUserId(userId: String?): Boolean {
        return userId != null && userId.length == 10 && userId.all { it.isDigit() }
    }

    private fun fetchData(userId: String) {


        val sharedPreferences = getSharedPreferences("your_preference_name", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", "")

        // Ensure authToken is not null before making the API call
        if (token != null && token.isNotBlank()) {
            val apiService = RetrofitClient.apiService

            // Set the token in RetrofitClient to be used in the interceptor
            RetrofitClient.setAuthToken(token)

            apiService.forgotPassword(userId).enqueue(object : Callback<ForgotPasswordResponse> {
                override fun onResponse(call: Call<ForgotPasswordResponse>, response: Response<ForgotPasswordResponse>) {
                    Log.d("ForgotActivity", "Response: ${response.raw()}")

                    if (response.isSuccessful) {
                        val forgotPasswordResponse = response.body()
                        if (forgotPasswordResponse != null) {
                            if (forgotPasswordResponse.success) {
                                // Handle success message
                                forgotPasswordResponse.data?.let {

                                 binding.forgotErrorMessage.text = it.message
                                }

                            } else {
                                // Handle error message
                                binding.forgotErrorMessage.text = forgotPasswordResponse.error

                            }
                        } else {
                            Toast.makeText(this@ForgotActivity, "Response body is null", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        // Handle error response
                        Toast.makeText(this@ForgotActivity, "Error: ${response.message()}", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<ForgotPasswordResponse>, t: Throwable) {
                    // Handle failure
                    Toast.makeText(this@ForgotActivity, "Failure: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        } else {
            // Handle case where token is null or blank
            Toast.makeText(this@ForgotActivity, "Token is null or blank", Toast.LENGTH_LONG).show()
        }
    }
}