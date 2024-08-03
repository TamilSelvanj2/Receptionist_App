package com.bics.expense.receptionistmodule.fragment.patientDetailsFragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bics.expense.receptionistmodule.R
import com.bics.expense.receptionistmodule.api.RetrofitClient
import com.bics.expense.receptionistmodule.fragment.Upcoming.UpcomingAdapter
import com.bics.expense.receptionistmodule.fragment.Upcoming.UpcomingModule
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpcomingPatientFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UpcomingPatientAdapter
    private lateinit var progressBar: ProgressBar
    private var patientID: String? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_upcoming_patient, container, false)

        patientID = arguments?.getString("PATIENT_ID")

        recyclerView = view.findViewById(R.id.upcomingPatientRecyclerView)
        adapter = UpcomingPatientAdapter(emptyList()) // Initialize the adapter with an empty list
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
        progressBar = view.findViewById(R.id.progressBarUpcomingPatient)
        fetchUpcomingAppointments(patientID)
        return view

    }

    fun fetchUpcomingAppointments(
        patientID: String?,
    ) {
        progressBar.visibility = View.VISIBLE // Show progress bar

        if (!isAdded) {
            return
        }

        val sharedPreferences =
            requireContext().getSharedPreferences("your_preference_name", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", "")

        val apiService = RetrofitClient.apiService

        token?.let { authToken ->
            RetrofitClient.setAuthToken(authToken)
        }

        if (patientID != null) {
            apiService.getUpcomingAppointmentDetails(patientID)
                .enqueue(object : Callback<CustomAppointment> {
                    override fun onResponse(
                        call: Call<CustomAppointment>,
                        response: Response<CustomAppointment>
                    ) {
                        progressBar.visibility = View.GONE // Hide progress bar

                        if (response.isSuccessful) {
                            val upcomingAppointments =
                                response.body()?.data?.appointmentDetails?.upcomingAppointment
                                    ?: emptyList()
                            adapter.submitList(upcomingAppointments)
                        } else {
                            // Handle unsuccessful response
                        }
                    }

                    override fun onFailure(call: Call<CustomAppointment>, t: Throwable) {
                        progressBar.visibility = View.GONE // Hide progress bar

                        // Handle failure
                    }
                })
        }
    }

}