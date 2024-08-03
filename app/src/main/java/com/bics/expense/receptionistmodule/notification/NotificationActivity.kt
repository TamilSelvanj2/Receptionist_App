package com.bics.expense.receptionistmodule.notification

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bics.expense.receptionistmodule.R
import com.bics.expense.receptionistmodule.api.RetrofitClient
import com.bics.expense.receptionistmodule.databinding.ActivityNotificationBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var appointmentAdapter: NotificationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notification)


        recyclerView = binding.notificationRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        val accountId = intent.getStringExtra("accountID")
        val sharedPreferences = getSharedPreferences("your_preference_name", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", "")

        if (!token.isNullOrEmpty()) {
            RetrofitClient.setAuthToken(token)
            if (accountId != null) {
                fetchNotifications(accountId)
            } else {
                Toast.makeText(this, "Account ID is missing", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Auth token is missing", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchNotifications(accountID: String) {
        val newNotificationsAvailable = true // Replace with your logic to check if new data is available

        val service = RetrofitClient.apiService
        val call = service.getNotificationsByAccountID(accountID)
        call.enqueue(object : Callback<NotificationResponse> {
            override fun onResponse(
                call: Call<NotificationResponse>,
                response: Response<NotificationResponse>
            ) {
                if (response.isSuccessful) {
                    val notificationResponse = response.body()
                    if (notificationResponse != null && notificationResponse.success) {
                        val appointments = notificationResponse.data ?: emptyList()
                        appointmentAdapter = NotificationAdapter(appointments)
                        recyclerView.adapter = appointmentAdapter


                        if (newNotificationsAvailable) {
                            val sharedPreferences = getSharedPreferences("notification_data", Context.MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putBoolean("new_data_available", true)
                            editor.apply()
                        }

                    } else {
                        Toast.makeText(
                            this@NotificationActivity,
                            "Failed to get notifications",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                } else {
                    Toast.makeText(
                        this@NotificationActivity,
                        "Error: ${response.message()}",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }

            override fun onFailure(call: Call<NotificationResponse>, t: Throwable) {
                Toast.makeText(
                    this@NotificationActivity,
                    "Failed to get notifications: ${t.message}",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        })
    }
}