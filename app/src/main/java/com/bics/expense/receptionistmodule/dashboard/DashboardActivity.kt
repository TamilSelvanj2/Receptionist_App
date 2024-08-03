package com.bics.expense.receptionistmodule.dashboard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.size
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import com.bics.expense.receptionistmodule.R
import com.bics.expense.receptionistmodule.databinding.ActivityDashboardBinding

import com.bics.expense.receptionistmodule.fragment.appointment.AppointmentFragment
import com.bics.expense.receptionistmodule.fragment.booked.BookedFragment
import com.bics.expense.receptionistmodule.fragment.home.HomeFragment
import com.bics.expense.receptionistmodule.fragment.patient.PatientDetailsFragment
import com.bics.expense.receptionistmodule.fragment.profile.ProfileFragment
import com.bics.expense.receptionistmodule.login.MainActivity
import com.bics.expense.receptionistmodule.notification.NotificationActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.google.android.material.navigation.NavigationView
import com.squareup.picasso.Picasso


class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var notificationCountTextView: TextView
    private var notificationCount: Int = 0 // T
    private lateinit var notification: ImageView
    private lateinit var notificationSymbol: ImageView
    private var BASE_URL = "https://myclinicsapi.bicsglobal.com"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard)


        val navigationView = findViewById<NavigationView>(R.id.nav_view)

        navigationView?.let { navView ->
            val headerView = navView.getHeaderView(0)
            val profileNameTextView = headerView.findViewById<TextView>(R.id.profileName)
            val profileEmailTextView = headerView.findViewById<TextView>(R.id.profileEmail)
            val profileImageView = headerView.findViewById<ImageView>(R.id.doctor_icon)



            notification = findViewById(R.id.notificationImageView)
            notificationSymbol = findViewById(R.id.notificationSymbolImageView)
            notificationCountTextView = findViewById(R.id.notificationCountTextView)


            // Retrieve profile name and email from intent extras

            val accountId = intent.getStringExtra("accountID")
            val firstName = intent.getStringExtra("firstName")
            val lastName = intent.getStringExtra("lastName")
            val profileName = "$firstName $lastName"
            val profileEmail = intent.getStringExtra("role")
            val profileImage = intent.getStringExtra("profileImage")


            // Set profile name and email to TextViews
            profileNameTextView.text = profileName
            profileEmailTextView.text = profileEmail
// Load profile image using Glide with base URL and image URL concatenation
            profileImage.let { imageUrl ->
                val fullImageUrl = BASE_URL + imageUrl // Concatenate base URL with image URL
                Glide.with(this)
                    .load(fullImageUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .transform(CircleCrop()) // Apply CircleCrop transformation
                    .into(profileImageView)
            }
            notification.setOnClickListener {

                val intent = Intent(this@DashboardActivity, NotificationActivity::class.java)
                intent.putExtra("accountID", accountId)
                startActivity(intent)
            }
            notificationSymbol.setOnClickListener {

                val intent = Intent(this@DashboardActivity, NotificationActivity::class.java)
                intent.putExtra("accountID", accountId)
                startActivity(intent)
            }
            checkForNewData()
        }
        // Setup drawer and initial fragment
        setupDrawer()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, BookedFragment())
            .commit()
    }


    private fun checkForNewData() {
        val sharedPreferences = getSharedPreferences("notification_data", Context.MODE_PRIVATE)
        val newDataAvailable = sharedPreferences.getBoolean("new_data_available", false)

        if (newDataAvailable) {
            notificationSymbol.visibility = ImageView.VISIBLE
            notification.visibility = ImageView.GONE
        } else {
            notificationSymbol.visibility = ImageView.GONE
            notification.visibility = ImageView.VISIBLE
        }

        // Clear the flag after handling it
        if (newDataAvailable) {
            val editor = sharedPreferences.edit()
            editor.putBoolean("new_data_available", false)
            editor.apply()
        }
    }

    private fun setNotificationCount(count: Int) {
        if (count > 0) {
            notificationCountTextView.text = count.toString()
            notificationCountTextView.visibility = View.VISIBLE
        } else {
            notificationCountTextView.visibility = View.GONE
        }
    }

    // Example function to update notification count
    private fun updateNotificationCount(newCount: Int) {
        notificationCount = newCount
        setNotificationCount(notificationCount)
    }

    private fun updateToolbar(title: String, color: Int) {
        binding.toolbar.title = title
        binding.toolbar.setBackgroundColor(color)
    }


    private fun setupDrawer() {


        val toggle = androidx.appcompat.app.ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.open_nav,
            R.string.close_nav
        )
        binding.drawerLayout?.addDrawerListener(toggle)
        toggle.syncState()


        binding.navView?.findViewById<TextView>(R.id.nav_home)?.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commit()
                updateToolbar(
                "Request Appointment",
                ContextCompat.getColor(this, R.color.nav_item_selected_color)

            )
            binding.drawerLayout?.closeDrawer(GravityCompat.START)

        }

        binding.navView?.findViewById<TextView>(R.id.appointment)?.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AppointmentFragment())
                .commit()
            updateToolbar("Appointments", ContextCompat.getColor(this, R.color.white))
            binding.drawerLayout?.closeDrawer(GravityCompat.START)

        }

        binding.navView?.findViewById<TextView>(R.id.booking)?.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, BookedFragment())
                .commit()
            updateToolbar("Dashboard", ContextCompat.getColor(this, R.color.white))
            binding.drawerLayout?.closeDrawer(GravityCompat.START)

        }

        binding.navView?.findViewById<TextView>(R.id.patient)?.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, PatientDetailsFragment())
                .commit()
            updateToolbar(
                "Patient Details",
                ContextCompat.getColor(this, R.color.nav_item_selected_color)
            )
            binding.drawerLayout?.closeDrawer(GravityCompat.START)

        }

        binding.navView?.findViewById<TextView>(R.id.nav_profile)?.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ProfileFragment())
                .commit()
            updateToolbar("Profile", ContextCompat.getColor(this, R.color.white))
            binding.drawerLayout?.closeDrawer(GravityCompat.START)

        }

        binding.navView?.findViewById<TextView>(R.id.nav_logout)?.setOnClickListener {
            // Handle logout action
            finish() // Finish the current activity to return to the login screen
            Toast.makeText(this, "Logout!", Toast.LENGTH_SHORT).show()
            binding.drawerLayout?.closeDrawer(GravityCompat.START)


        }

        // Initialize drawerLayout using binding
        drawerLayout = binding.drawerLayout!!
    }
}