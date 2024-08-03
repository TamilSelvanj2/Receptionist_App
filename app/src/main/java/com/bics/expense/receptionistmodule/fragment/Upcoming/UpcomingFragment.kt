package com.bics.expense.receptionistmodule.fragment.Upcoming

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bics.expense.receptionistmodule.R
import com.bics.expense.receptionistmodule.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



class UpcomingFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UpcomingAdapter // You'll need to create this adapter
    private lateinit var progressBar: ProgressBar

    fun refreshData() {
        fetchUpcomingAppointments()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_upcoming, container, false)
        Log.d("_AC", "onCreateView: "+"UPCOMING")

        recyclerView = view.findViewById(R.id.upcomingRecyclerView)
        adapter = UpcomingAdapter(emptyList()) // Initialize the adapter with an empty list
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
        progressBar = view.findViewById(R.id.progressBarUpcoming)
        fetchUpcomingAppointments()
        return view
    }

    fun fetchUpcomingAppointments() {
        progressBar.visibility = View.VISIBLE // Show progress bar

        if (!isAdded) {
            return
        }

        val sharedPreferences = requireContext().getSharedPreferences("your_preference_name", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", "")

        val apiService = RetrofitClient.apiService

        token?.let { authToken ->
            RetrofitClient.setAuthToken(authToken) }
        apiService.getUpcomingAppointments().enqueue(object : Callback<UpcomingModule> {
            override fun onResponse(
                call: Call<UpcomingModule>,
                response: Response<UpcomingModule>
            ) {
                progressBar.visibility = View.GONE // Hide progress bar

                if (response.isSuccessful) {
                    val upcomingAppointments = response.body()?.data ?: emptyList()
                    adapter.submitList(upcomingAppointments)
                } else {
                    // Handle unsuccessful response
                }
            }

            override fun onFailure(call: Call<UpcomingModule>, t: Throwable) {
                progressBar.visibility = View.GONE // Hide progress bar

                // Handle failure
            }
        })
    }
}
