package com.bics.expense.receptionistmodule.fragment.document

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

class DocumentsFragment : Fragment() {


    private lateinit var progressBar: ProgressBar
    private lateinit var documentAdapter: DocumentsApater
    private lateinit var recyclerView: RecyclerView
    private lateinit var noDataTextView: TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_documents, container, false)
        val appointmentID = arguments?.getString("APPOINTMENT_ID")

        recyclerView = view.findViewById(R.id.recyclersView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        progressBar = view.findViewById(R.id.progressBarsDocument)
        noDataTextView = view.findViewById(R.id.noDataTextView)


        if (appointmentID != null) {

            fetchDocuments(appointmentID) // Fetch documents for the given appointment ID

        } else {
            Toast.makeText(requireContext(), "Invalid appointment ID", Toast.LENGTH_SHORT).show()
        }

        documentAdapter = DocumentsApater(requireContext(), emptyList(), object : DocumentsApater.ImageClickListener {
            override fun onImageClicked(imageUrl: String) {
                Toast.makeText(requireContext(), "Image clicked: $imageUrl", Toast.LENGTH_SHORT).show()
            }
        }, this)


        recyclerView.adapter = documentAdapter


        return view
    }

    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        progressBar.visibility = View.GONE
    }

    private fun fetchDocuments(appointmentID: String) {
        showProgressBar()

        if (!isAdded) {
            return
        }

        val sharedPreferences = requireContext().getSharedPreferences("your_preference_name", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", "")

        val apiService = RetrofitClient.apiService

        token?.let { authToken ->
            RetrofitClient.setAuthToken(authToken)
        }
        apiService.getDocuments(appointmentID)
            .enqueue(object : Callback<DocumentResponse> {
                override fun onResponse(
                    call: Call<DocumentResponse>,
                    response: Response<DocumentResponse>
                ) {
                    hideProgressBar()

                    if (response.isSuccessful) {
                        val documentResponse = response.body()
                        documentResponse?.let { response ->
                            if (response.success) {
                                documentAdapter.updateDocuments(response.data)


                            } else {
                                Toast.makeText(requireContext(), "Failed to fetch documents: ${response.error}", Toast.LENGTH_SHORT).show()
                            }
                        } ?: run {
                            Toast.makeText(
                                requireContext(),
                                "Empty response body",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Failed to fetch documents: ${response.message()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<DocumentResponse>, t: Throwable) {
                    hideProgressBar()

                    Toast.makeText(
                        requireContext(),
                        "Failed to fetch documents: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
    fun onDocumentDeleted() {
        // Fetch the documents again after a document is deleted
        val appointmentID = arguments?.getString("APPOINTMENT_ID")
        if (appointmentID != null) {
            fetchDocuments(appointmentID)
        }
    }
}