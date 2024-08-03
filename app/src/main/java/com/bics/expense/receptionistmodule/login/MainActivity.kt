package com.bics.expense.receptionistmodule.login


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bics.expense.receptionistmodule.dashboard.DashboardActivity
import com.bics.expense.receptionistmodule.R
import com.bics.expense.receptionistmodule.api.RetrofitClient
import com.bics.expense.receptionistmodule.databinding.ActivityMainBinding
import com.bics.expense.receptionistmodule.forgotpassword.ForgotActivity
import com.bics.expense.receptionistmodule.forgotpassword.ForgotPasswordResponse
import com.bics.expense.receptionistmodule.fragment.profile.ProfileFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        RetrofitClient.setAuthToken("your_auth_token_here")

        binding.forgotPassword.setOnClickListener {

            val userId = binding.editTextLoginId.text.toString()

            val intent = Intent(this@MainActivity, ForgotActivity::class.java)

            intent.putExtra("User_Id", userId)

            startActivity(intent)
        }

        binding.buttonLogin.setOnClickListener {
            val userIDError = validateUserId()
            val passwordError = validatePassword()

            binding.usernametextinputlayout.helperText = userIDError
            binding.textInputLayoutPassword.helperText = passwordError



            if (userIDError == null && passwordError == null) {
                performLogin()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Clear user ID and password fields when returning to the login screen
        binding.editTextLoginId.text?.clear()
        binding.editTextLoginPassword.text?.clear()
    }

    private fun validateUserId(): String? {
        val userIdLogin = binding.editTextLoginId.text.toString()
        binding.errorMessage.visibility = View.GONE

        return when {
            userIdLogin.isEmpty() -> "Please enter a userID"
            userIdLogin.length < 10 -> "Minimum 10 characters required for userID"
            else -> null
        }
    }

    private fun validatePassword(): String? {
        val userIdPassword = binding.editTextLoginPassword.text.toString()
        binding.errorMessage.visibility = View.GONE
        return if (userIdPassword.isEmpty()) {
            "Please enter a password"


        } else {
            null
        }
    }


    private fun performLogin() {
        val apiClient = RetrofitClient.initialize()

        val userID = binding.editTextLoginId.text.toString().trim()
        val password = binding.editTextLoginPassword.text.toString().trim()
        val loginRequest = LoginRequest(userID, password)

        binding.progressBar.visibility = View.VISIBLE

        // Using CoroutineScope for async network call
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val call: Response<LoginResponse> =
                    apiClient.apiService.login(loginRequest).execute()

                withContext(Dispatchers.Main) {
                    if (call.isSuccessful) {
                        val loginResponse = call.body()
                        binding.errorMessage.visibility = View.GONE

                        if (loginResponse?.success == true) {
                            val userData = loginResponse.data
                            val token = userData?.token
                            // Store the token securely in SharedPreferences
                            val sharedPreferences = getSharedPreferences("your_preference_name", Context.MODE_PRIVATE)
                            sharedPreferences.edit().putString("token", token).apply()

                            Toast.makeText(this@MainActivity, "You have successfully logged in", Toast.LENGTH_LONG).show()

                            binding.progressBar.visibility = View.GONE



                            // Check the role of the user and navigate accordingly
                            when (userData?.role) {
                                "receptionist" -> {
                                    val intent = Intent(this@MainActivity, DashboardActivity::class.java)
                                    // Pass the user data to the next activity
                                    intent.putExtra("accountID", userData.accountID)
                                    intent.putExtra("firstName", userData.firstName)
                                    intent.putExtra("lastName", userData.lastName)
                                    intent.putExtra("email", userData.email)
                                    intent.putExtra("phone", userData.phone)
                                    intent.putExtra("token", userData.token)
                                    intent.putExtra("isActive", userData.isActive)
                                    intent.putExtra("role", userData.role)
                                    intent.putExtra("profileImage", userData.profileImage)
                                    startActivity(intent)



                                }

                                "junior-physician" -> {
                                    val intent = Intent(this@MainActivity, DashboardActivity::class.java)
                                    // Pass the user data to the next activity
                                    intent.putExtra("accountID", userData.accountID)
                                    intent.putExtra("firstName", userData.firstName)
                                    intent.putExtra("lastName", userData.lastName)
                                    intent.putExtra("email", userData.email)
                                    intent.putExtra("phone", userData.phone)
                                    intent.putExtra("token", userData.token)
                                    intent.putExtra("isActive", userData.isActive)
                                    intent.putExtra("role", userData.role)
                                    intent.putExtra("profileImage", userData.profileImage)
                                    startActivity(intent)
                                }

                                else -> {
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Unknown role: ${userData?.role}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    binding.progressBar.visibility = View.GONE

                                }
                            }

                        }  else {
                            // Handle unsuccessful login response (show error message)
                            val errorMessage = loginResponse?.error ?: "Unknown error"
                            binding.progressBar.visibility = View.GONE



                            if (errorMessage.isNotEmpty()) {
                                when {
                                    errorMessage.contains("Account with above user id is not found") -> {
                                        Toast.makeText(this@MainActivity,errorMessage,Toast.LENGTH_LONG).show()
                                    }
                                    errorMessage.contains("Invalid credentials") -> {
                                        Toast.makeText(this@MainActivity,errorMessage,Toast.LENGTH_LONG).show()

                                    }
                                    else -> {

                                        Toast.makeText(this@MainActivity,errorMessage,Toast.LENGTH_LONG).show()

                                    }
                                }
                            } else {
                                binding.errorMessage.visibility = View.GONE
                            }

                            binding.progressBar.visibility = View.GONE

                        }
                    } else {
                        Toast.makeText(
                            this@MainActivity, "Error: ${call.code()}", Toast.LENGTH_LONG
                        ).show()
                        binding.progressBar.visibility = View.GONE

                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }
}







