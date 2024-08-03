package com.bics.expense.receptionistmodule.fragment.document

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bics.expense.receptionistmodule.R
import com.bics.expense.receptionistmodule.api.RetrofitClient
import com.bics.expense.receptionistmodule.fragment.appointment.AppointmentFragment
import com.bics.expense.receptionistmodule.fragment.appointment.NewRequestAppointmentFragment
import com.google.android.material.tabs.TabLayout
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


class DocumentFragment : Fragment() {

    private lateinit var chooseDocument: Button
    private lateinit var chooseTextView: TextView
    private lateinit var choosetype: AutoCompleteTextView
    private lateinit var uploadDocumentBtn: Button
    private lateinit var viewMore: TextView
    private lateinit var documentAdapter: DocumentAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var line: LinearLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var contract: ActivityResultLauncher<String>
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>



    private var currentPhotoPath: String? = null
    private lateinit var selectedImageUri: Uri // Uri for the selected image
    private lateinit var documentTypeID: String

//    private val requestPermissionLauncher =
//        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
//            if (isGranted) {
//                // Permission is granted. Continue the action or workflow in your app.
//                contract.launch("image/*")
//            } else {
//                // Explain to the user that the feature is unavailable because the feature requires a permission that the user has denied.
//                Toast.makeText(requireContext(), "Permission denied to access gallery", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//
//    private val contract = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
//        uri?.let {
//            selectedImageUri = it
//            chooseTextView.text = getFileName(it)
//        } ?: run {
//            Toast.makeText(requireContext(), "No document selected", Toast.LENGTH_SHORT).show()
//        }
//    }


    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_document, container, false)



        val appointmentID = arguments?.getString("APPOINTMENT_ID")


        chooseDocument = view.findViewById(R.id.chooseDocumentBtn)
        chooseTextView = view.findViewById(R.id.chooseDocument)
        choosetype = view.findViewById(R.id.editTextDocumentType)
        uploadDocumentBtn = view.findViewById(R.id.upLoadDocumentBtn)
        viewMore = view.findViewById(R.id.viewMore)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        progressBar = view.findViewById(R.id.progressBarDocument)


        if (appointmentID != null) {
            fetchDocuments(appointmentID) // Fetch documents for the given appointment ID
        } else {
            Toast.makeText(requireContext(), "Invalid appointment ID", Toast.LENGTH_SHORT).show()
        }


        appointmentID?.let { fetchDocuments(it) }
        line = view.findViewById(R.id.lines)

        documentAdapter = DocumentAdapter(requireContext(), emptyList(), object : DocumentAdapter.ImageClickListener {
            override fun onImageClicked(imageUrl: String) {
                Toast.makeText(requireContext(), "Image clicked: $imageUrl", Toast.LENGTH_SHORT).show()
            }
        }, this)

        recyclerView.adapter = documentAdapter

        contract = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                selectedImageUri = it
                chooseTextView.text = getFileName(it)



            } ?: run {
                Toast.makeText(requireContext(), "No document selected", Toast.LENGTH_SHORT).show()
            }
        }

        // Initialize ActivityResultLauncher for RequestPermission
        requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                contract.launch("image/*")
            } else {
                Toast.makeText(requireContext(), "Permission denied to access gallery", Toast.LENGTH_SHORT).show()
            }
        }



        chooseDocument.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                contract.launch("image/*")
            } else {
                requestPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }


        uploadDocumentBtn.setOnClickListener {
            appointmentID?.let { appointmentID ->
                if (::selectedImageUri.isInitialized && ::documentTypeID.isInitialized) {
                    uploadDocument(selectedImageUri, appointmentID, documentTypeID)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Please select an image and document type",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } ?: run {
                Toast.makeText(
                    requireContext(),
                    "Error: Appointment ID is null",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


        choosetype.setOnClickListener {
            fetchDocumentTypes()
        }

        return view
    }



    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        progressBar.visibility = View.GONE
    }
    private fun resetUI() {
        // Clear selected image URI
        selectedImageUri = Uri.EMPTY
        chooseTextView.text = getString(R.string.choosedocument)

        // Clear document type selection
        choosetype.text.clear()

        // Clear RecyclerView adapter data
//        documentAdapter.updateDocuments(emptyList())
    }

    private fun fetchDocumentTypes() {

        val sharedPreferences = requireContext().getSharedPreferences("your_preference_name", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", "")

        RetrofitClient.setAuthToken(token)

        // Make API call to fetch document types
        val call = RetrofitClient.apiService.getDocumentTypes()
        call.enqueue(object : Callback<DocumentTypesResponse> {
            override fun onResponse(call: Call<DocumentTypesResponse>, response: Response<DocumentTypesResponse>) {

                if (response.isSuccessful) {

                    val documentTypes = response.body()
                    documentTypes?.let {
                        if (it.success == true) {
                            documentTypes
                            setupDocumentTypeDropdown(it.data)
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Failed to fetch document types",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } ?: run {
                        Toast.makeText(
                            requireContext(),
                            "No document types available",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Error: ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<DocumentTypesResponse>, t: Throwable) {

                Toast.makeText(requireContext(), "Failed to fetch document types: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun setupDocumentTypeDropdown(documentTypes: List<DocumentType>?) {
        documentTypes?.let {
            val documentTypeNames = it.map { documentType -> documentType.documentType }
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                documentTypeNames
            )
            choosetype.setAdapter(adapter)
            choosetype.setOnItemClickListener { parent, _, position, _ ->
                documentTypeID = documentTypes[position].documentTypeID
            }

        }
    }

    private fun uploadDocument(
        selectedImageUri: Uri,
        appointmentID: String,
        documentTypeID: String) {
        showProgressBar()

        try {
            val inputStream = requireActivity().contentResolver.openInputStream(selectedImageUri)
                ?: throw FileNotFoundException("Uri $selectedImageUri not found")

            val fileDir = requireContext().applicationContext.filesDir
            val file = File(fileDir, "image.png")

            FileOutputStream(file).use { outputStream ->
                inputStream.copyTo(outputStream)
            }

            val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData("document", file.name, requestBody)

            val appointmentIDBody = RequestBody.create("text/plain".toMediaTypeOrNull(), appointmentID)
            val documentTypeIDBody = RequestBody.create("text/plain".toMediaTypeOrNull(), documentTypeID)

            RetrofitClient.apiService.uploadDocument(appointmentIDBody, part, documentTypeIDBody)
                .enqueue(object : Callback<AddDocumentRequest> {
                    override fun onResponse(
                        call: Call<AddDocumentRequest>,
                        response: Response<AddDocumentRequest>
                    ) {
                        hideProgressBar()
                        resetUI()
                        if (response.isSuccessful) {
                            Toast.makeText(
                                requireContext(),
                                "Document uploaded successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            fetchDocuments(appointmentID)

                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Failed to upload document",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<AddDocumentRequest>, t: Throwable) {
                        hideProgressBar()
                        resetUI()
                        Log.e("Upload error:", t.message ?: "unknown error")
                        Toast.makeText(
                            requireContext(),
                            "Error uploading document",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })

        } catch (e: FileNotFoundException) {
            hideProgressBar()
            resetUI()
            Toast.makeText(requireContext(), "No document selected", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            hideProgressBar()
            resetUI()
            Toast.makeText(requireContext(), "Error reading file: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    @SuppressLint("Range")
    private fun getFileName(uri: Uri): String {
        var result: String? = null
        requireContext().contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            }
        }
        return result ?: uri.pathSegments.last()
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
                            Toast.makeText(
                                requireContext(),
                                "Failed to fetch documents: ${response.error}",
                                Toast.LENGTH_SHORT
                            ).show()
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
    // Function to delete document
}