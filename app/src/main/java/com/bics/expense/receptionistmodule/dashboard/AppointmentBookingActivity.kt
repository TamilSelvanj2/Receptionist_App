package com.bics.expense.receptionistmodule.dashboard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bics.expense.receptionistmodule.R
import com.bics.expense.receptionistmodule.databinding.ActivityAppointmentBookingBinding
import com.bics.expense.receptionistmodule.fragment.appointment.NewRequestAppointmentFragment


class AppointmentBookingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAppointmentBookingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = DataBindingUtil.setContentView(this, R.layout.activity_appointment_booking)

        setSupportActionBar(binding.toolbarhead)

        supportActionBar?.title = "APPOINTMENT DETAILS"

        // Enable the back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbarhead.setNavigationOnClickListener {
            onBackPressed()
        }


        if (savedInstanceState == null) {
            val appointmentID = intent.getStringExtra("APPOINTMENT_ID")
            val fragment = NewRequestAppointmentFragment().apply {
                arguments = Bundle().apply { putString("APPOINTMENT_ID", appointmentID)

                }
            }
            setFragment(fragment)
        }
    }


    // Function to set the fragment
    private fun setFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_containers, fragment)
        fragmentTransaction.commit()
    }
}