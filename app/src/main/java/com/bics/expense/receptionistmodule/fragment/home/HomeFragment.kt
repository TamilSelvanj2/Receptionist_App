package com.bics.expense.receptionistmodule.fragment.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bics.expense.receptionistmodule.R
import com.bics.expense.receptionistmodule.addPatient.ConsultationActivity
import com.bics.expense.receptionistmodule.api.RetrofitClient
import com.bics.expense.receptionistmodule.databinding.FragmentHomeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var dashboardAdapter: DashboardAdapter
    private var phoneNumber: String = " "

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        val sharedPreferences =
            requireActivity().getSharedPreferences("your_preference_name", Context.MODE_PRIVATE)
        // Retrieve required data from SharedPreferences
        val token = sharedPreferences.getString("token", "")
        // Set the token to RetrofitClient
        RetrofitClient.setAuthToken(token)


        binding.patientNumberEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No action needed here
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // No action needed here
            }

            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    // Check if the first character is '0'
                    if (it.isNotEmpty() && it[0] == '0') {
                        // Remove the first character
                        it.delete(0, 1)
                    }
                }
            }
        })

        binding.buttonAddNewPatient.setOnClickListener {

            val intent = Intent(requireContext(), ConsultationActivity::class.java)
            intent.putExtra("phoneNumber", phoneNumber)
            startActivity(intent)
        }

        // Set up RecyclerView and Adapter
        dashboardAdapter = DashboardAdapter()
        binding.recyclerViewData.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewData.adapter = dashboardAdapter

        // Set up button click listener
        binding.searchButton.setOnClickListener {

            phoneNumber = binding.patientNumberEditText.text.toString()




            if (isValidPhoneNumber(phoneNumber)) {
                val sharedPreferences = requireActivity().getSharedPreferences(
                    "your_preference_name",
                    Context.MODE_PRIVATE
                )
                val token = sharedPreferences.getString("token", "")
                RetrofitClient.setAuthToken(token)
                RetrofitClient.apiService.getPatientDetailsBasedOnPhone(phoneNumber)
                    .enqueue(object : Callback<DashboardResponse> {

                        override fun onResponse(
                            call: Call<DashboardResponse>,
                            response: Response<DashboardResponse>
                        ) {
                            if (response.isSuccessful) {
                                val dashboardResponse = response.body()
                                val patients = dashboardResponse?.data
                                if (!patients.isNullOrEmpty()) {

                                    dashboardAdapter.submitList(patients)
                                    binding.recyclerViewData.visibility = View.VISIBLE
                                    binding.textViewNoData.visibility = View.GONE
                                    binding.buttonAddNewPatient.visibility = View.VISIBLE

                                } else {

                                    binding.textViewNoData.visibility = View.VISIBLE
                                    binding.recyclerViewData.visibility = View.GONE
                                    binding.buttonAddNewPatient.visibility = View.VISIBLE

                                }
                            } else {
                                Toast.makeText(requireContext(), "Failed to fetch patient data", Toast.LENGTH_LONG).show()
                            }
                        }

                        override fun onFailure(call: Call<DashboardResponse>, t: Throwable) {

                            Toast.makeText(
                                requireContext(),
                                "ERROR: ${t.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    })
            } else {
                binding.textViewNoData.visibility = View.VISIBLE
                binding.recyclerViewData.visibility = View.GONE
                binding.buttonAddNewPatient.visibility = View.GONE
                Toast.makeText(requireContext(), "Enter validate mobile number", Toast.LENGTH_LONG)
                    .show()
            }
        }

        return binding.root
    }

    private fun isValidPhoneNumber(number: String): Boolean {
        return number.length == 10 && number.all { it.isDigit() } && number[0] != '0'
    }

}


