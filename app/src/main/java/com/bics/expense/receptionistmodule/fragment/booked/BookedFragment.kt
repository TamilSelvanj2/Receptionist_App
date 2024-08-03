package com.bics.expense.receptionistmodule.fragment.booked


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bics.expense.receptionistmodule.R
import com.bics.expense.receptionistmodule.api.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.recyclerview.widget.ListAdapter

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BookedFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BookedAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_booked, container, false)

//        loadAppointments()
        return view
    }

//    private fun loadAppointments() {
//        // Call the Retrofit service method to fetch appointments
//        val apiService = RetrofitClient.apiService
//        apiService.getAppointments().enqueue(object : Callback<BookResponse> {
//            override fun onResponse(call: Call<BookResponse>, response: Response<BookResponse>
//            ) {
//                if (response.isSuccessful) {
//                    val responseData = response.body()
//                    if (responseData?.success == true) {
//                        val appointments = responseData.data
//                        appointments?.let {
//                            adapter.submitList(it)
//                        }
//                    } else {
//                        // Handle unsuccessful response
//                    }
//                } else {
//                    // Handle unsuccessful response
//                }
//            }
//
//            override fun onFailure(call: Call<BookResponse>, t: Throwable) {
//                // Handle network failure
//                t.printStackTrace()
//            }
//        })
//    }

}
