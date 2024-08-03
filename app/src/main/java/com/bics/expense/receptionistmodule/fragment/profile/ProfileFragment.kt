package com.bics.expense.receptionistmodule.fragment.profile

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bics.expense.receptionistmodule.R
import com.bics.expense.receptionistmodule.api.RetrofitClient
import com.bics.expense.receptionistmodule.login.MainActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern

class ProfileFragment : Fragment() {

    private lateinit var firstNameTextView: TextView
    private lateinit var lastNameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var phoneTextView: TextView
    private lateinit var userIdTextView: TextView
    private lateinit var imageProfile: ImageView
    private lateinit var roleProfile: TextView
    private lateinit var button: Button
    private lateinit var clickHere: TextView
    private var profileImage: String? = null
    private var accountID: String? = null
    private var BASE_URL = "https://myclinicsapi.bicsglobal.com"




    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)



        val sharedPreferences = requireActivity().getSharedPreferences("your_preference_name", Context.MODE_PRIVATE)
        // Retrieve required data from SharedPreferences
        val token = sharedPreferences.getString("token", "")
        // Set the token to RetrofitClient
        RetrofitClient.setAuthToken(token)



        firstNameTextView = view.findViewById(R.id.nameEditText)
        lastNameTextView = view.findViewById(R.id.lastNameEditText)
        emailTextView = view.findViewById(R.id.emailEditText)
        phoneTextView = view.findViewById(R.id.phoneEditText)
        userIdTextView = view.findViewById(R.id.userIdEditText)
        imageProfile = view.findViewById(R.id.profielImage)
        roleProfile = view.findViewById(R.id.roleTextProfile)
        button = view.findViewById(R.id.buttonUpdate)
        clickHere = view.findViewById(R.id.clickHere)

        // Make API call to retrieve profile details
        getProfileDetails()




        button.setOnClickListener{
            updateProfileDetails()
        }
        clickHere.setOnClickListener {
            showPasswordUpdateDialog()
        }

        return view
    }

    @SuppressLint("MissingInflatedId")
    private fun showPasswordUpdateDialog() {


        val builder = AlertDialog.Builder(requireContext(),R.style.RoundedTabLayoutStyle)
        val inflater = LayoutInflater.from(requireContext())
        val paymentView = inflater.inflate(R.layout.dialog_password_update, null)

        val newPasswordEditText = paymentView.findViewById<EditText>(R.id.newPasswordEditText)
        val confirmPasswordEditText = paymentView.findViewById<EditText>(R.id.confirmPasswordEditText)
        val confirmpasswordtextinputlayout = paymentView.findViewById<TextInputLayout>(R.id.confirmpasswordtextinputlayout)
        val confirmtextinputlayout = paymentView.findViewById<TextInputLayout>(R.id.confirmtextinputlayout)
        val closeBtn = paymentView.findViewById<Button>(R.id.closeButton)
        val updateBtn = paymentView.findViewById<Button>(R.id.updateBtn)

        builder.setView(paymentView)
        val dialog = builder.create()
        dialog.show()

        updateBtn.setOnClickListener {
            val newPassword = newPasswordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()


            if (newPassword.isEmpty()) {
                Toast.makeText(context,"Password cannot be empty",Toast.LENGTH_LONG).show()
            } else if (!isValidPasswordFormat(newPassword)) {

                Toast.makeText(context,"Password must be at least 8 " +
                        "characters long, include uppercase, lowercase, digit, and special character",Toast.LENGTH_LONG).show()
            } else if (newPassword != confirmPassword) {

                Toast.makeText(context,"Passwords do not match",Toast.LENGTH_LONG).show()


            } else {
                // Clear error messages
                confirmpasswordtextinputlayout.error = null
                confirmtextinputlayout.error = null

                // Perform password update API call
                updatePassword(newPassword, confirmPassword)
                dialog.dismiss() // Dismiss dialog after updating password
            }
        }

        closeBtn.setOnClickListener {
            dialog.dismiss()
        }

    }
    private fun isValidPasswordFormat(password: String): Boolean {
        val passwordPattern = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#\$%^&+=!])(?=\\S+$).{8,}$"
        val pattern = Pattern.compile(passwordPattern)
        val matcher = pattern.matcher(password)
        return matcher.matches()
    }


    private fun getProfileDetails() {

        val apiService = RetrofitClient.apiService
        val call = apiService.getProfileDetails()

        call.enqueue(object : Callback<ProfileRequest> {
            override fun onResponse(call: Call<ProfileRequest>, response: Response<ProfileRequest>) {
                if (response.isSuccessful) {
                    val profileResponse = response.body()?.data
                    profileResponse?.let {
                        // Update UI with profile details
                        accountID = it.accountID
                        firstNameTextView.text = it.firstName
                        lastNameTextView.text = it.lastName
                        emailTextView.text = it.email
                        phoneTextView.text = it.phone
                        userIdTextView.text = it.userID
                        roleProfile.text =it.role
                        profileImage = it.profileImage

                        profileImage?.let { imageUrl ->
                            val fullImageUrl = "$BASE_URL$imageUrl"
                            Glide.with(this@ProfileFragment)
                                .load(fullImageUrl)
                                .placeholder(R.drawable.placeholder_image)
                                .error(R.drawable.error_image)
                                .transform(CircleCrop())
                                .into(imageProfile)
                        }

                    } ?: run {
                        // Handle null response
                        showToast("Failed to get profile details")
                    }
                } else {
                    // Handle unsuccessful response
                    showToast("Failed to get profile details")
                }
            }
            override fun onFailure(call: Call<ProfileRequest>, t: Throwable) {
                // Handle failure
                showToast("Failed to get profile details: ${t.message}")
            }
        })
    }

    private fun updateProfileDetails() {
        val newFirstName = firstNameTextView.text.toString()
        val newLastName = lastNameTextView.text.toString()

        accountID?.let { accountId ->
            val apiService = RetrofitClient.apiService
            val call = apiService.updateProfileDetails(
                ProfileUpdateRequest(
                    accountID = accountId,
                    firstName = newFirstName,
                    lastName = newLastName
                )
            )

            call.enqueue(object : Callback<ProfileUpdateRequest> {
                override fun onResponse(
                    call: Call<ProfileUpdateRequest>,
                    response: Response<ProfileUpdateRequest>
                ) {
                    if (response.isSuccessful) {
                        // Update successful, handle response
                        val updateResponse = response.body()
                        showToast("Profile updated successfully")
                    } else {
                        // Handle unsuccessful response
                        showToast("Failed to update profile")
                    }
                }
                override fun onFailure(call: Call<ProfileUpdateRequest>, t: Throwable) {
                    // Handle failure
                    showToast("Failed to update profile: ${t.message}")
                }
            })
        }
    }

        private fun updatePassword(newPassword: String, confirmPassword: String) {
            accountID?.let { accountId ->
                val apiService = RetrofitClient.apiService
                val call = apiService.updatePassword(
                    PasswordUpdateRequest(
                        accountId,
                        newPassword,
                        confirmPassword
                    )
                )

                call.enqueue(object : Callback<PasswordUpdateResponse> {
                    override fun onResponse(call: Call<PasswordUpdateResponse>, response: Response<PasswordUpdateResponse>) {
                        if (response.isSuccessful) {

                            showToast("Password updated successfully")
                            logoutUser()

                        } else {
                            // Handle unsuccessful response
                            showToast("Failed to update password")
                        }
                    }
                    override fun onFailure(call: Call<PasswordUpdateResponse>, t: Throwable) {
                        // Handle failure
                        showToast("Failed to update password: ${t.message}")
                    }
                })
            }
        }




    private fun logoutUser() {
        val sharedPreferences = requireActivity().getSharedPreferences("your_preference_name", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear() // Clear all session data
        editor.apply()

        // Navigate to the login screen
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish() // Close the current activity
    }
    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
