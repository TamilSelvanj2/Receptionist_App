package com.bics.expense.receptionistmodule.fragment.patient

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bics.expense.receptionistmodule.R
import com.bics.expense.receptionistmodule.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PatientDetailsFragment : Fragment() {


    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PatientAdapter
    private lateinit var count: TextView
    private lateinit var progressBar: ProgressBar

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_patient_details, container, false)
        progressBar = view.findViewById(R.id.progressBarPatientDetails)

        recyclerView = view.findViewById(R.id.recyclerViewPatientDetails)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = PatientAdapter(emptyList()) // Initialize with an empty list
        recyclerView.adapter = adapter
        count = view.findViewById(R.id.count)


        loadPatientDetail()
        return view
    }

    private fun loadPatientDetail() {
        progressBar.visibility = View.VISIBLE
        if (!isAdded) {
            return
        }

        val sharedPreferences = requireContext().getSharedPreferences("your_preference_name", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", "")

        val apiServices = RetrofitClient.apiService

        // Set the obtained token as the authentication token
        token?.let { authToken ->
            RetrofitClient.setAuthToken(authToken)
        }

        apiServices.getPatients().enqueue(object : Callback<PatientsResponse> {
            override fun onResponse(call: Call<PatientsResponse>, response: Response<PatientsResponse>) {

                progressBar.visibility = View.GONE

                if (response.isSuccessful) {
                    val responseData = response.body()

                    if (responseData?.success == true) {
                        val patients = responseData.data

                        patients?.let {
                            adapter.updatePatients(it)
                            count.text = "No.of Patient Count: ${it.size}"


                        }
                    } else {
                        // Handle the case where responseData.success is false
                        Toast.makeText(requireContext(), "Failed to get patient details", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Handle the unsuccessful response (e.g., show a Toast or log the error)
                    Toast.makeText(requireContext(), "Failed to get response", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<PatientsResponse>, t: Throwable) {
                if (!isAdded) {
                    return
                }
                progressBar.visibility = View.GONE

                t.printStackTrace()
                // Handle the failure (e.g., show a Toast or log the error)
                Toast.makeText(requireContext(), "Failed: " + t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
}