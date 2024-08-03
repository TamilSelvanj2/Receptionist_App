package com.bics.expense.receptionistmodule.fragment.requestAppointment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bics.expense.receptionistmodule.R
import com.bics.expense.receptionistmodule.api.RetrofitClient
import com.bics.expense.receptionistmodule.fragment.AppointmentDetail
import com.bics.expense.receptionistmodule.fragment.AppointmentDetails
import com.bics.expense.receptionistmodule.fragment.patientDetailsFragments.CancelAppointmentResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone


class AppointmentBookingsFragment : Fragment() {


    private lateinit var appointmentIdTextView: TextView
    private lateinit var appointmentDateTextView: TextView
    private lateinit var specialityTextView: TextView
    private lateinit var notesTextView: TextView
    private lateinit var statusTextView: TextView
//    private lateinit var timerTextView: TextView
//    private lateinit var requestAppointmentBtn: Button
    private lateinit var cancleAppointmentBtn: Button
    private lateinit var duration: TextView
    private lateinit var appointmentTime: TextView
//    private lateinit var linearLayout: LinearLayout
    private lateinit var progressBar: ProgressBar

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_appointment_bookings, container, false)



        appointmentIdTextView = view.findViewById(R.id.appointmentIdsTextView)
        appointmentDateTextView = view.findViewById(R.id.appointmentDatesTextView)
        specialityTextView = view.findViewById(R.id.specialitysTextView)
        notesTextView = view.findViewById(R.id.notesTextsViews)
        statusTextView = view.findViewById(R.id.statusTextsView)
//        timerTextView = view.findViewById(R.id.timerTextView)
//        requestAppointmentBtn = view.findViewById(R.id.requestAppointmentsBtn)
        cancleAppointmentBtn = view.findViewById(R.id.cancelAppointmentsBtn)
        duration = view.findViewById(R.id.durationsTextViews)
        appointmentTime = view.findViewById(R.id.appointmentTimesTextView)
//        linearLayout = view.findViewById(R.id.linearLayout6Appointment)
        progressBar = view.findViewById(R.id.progressBarAppointmentBooks)

        cancleAppointmentBtn.visibility = View.GONE

        val appointmentID = arguments?.getString("APPOINTMENT_ID")

        if (appointmentID != null) {
            fetchAppointmentDetails(appointmentID)
        } else {
            Toast.makeText(requireContext(), "Invalid appointment ID", Toast.LENGTH_SHORT).show()
        }
        return view

    }
    private fun fetchAppointmentDetails(appointmentID: String) {
        progressBar.visibility = View.VISIBLE // Show progress bar

        if (!isAdded) {
            return
        }

        val sharedPreferences = requireContext().getSharedPreferences("your_preference_name", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", "")

        val apiService = RetrofitClient.apiService

        token?.let { authToken ->
            RetrofitClient.setAuthToken(authToken)
        }
        apiService.getAppointmentDetails(appointmentID).enqueue(object :
            Callback<AppointmentDetails> {
            override fun onResponse(call: Call<AppointmentDetails>, response: Response<AppointmentDetails>) {

                if (!isAdded) {
                    return
                }

                progressBar.visibility = View.GONE // Hide progress bar

                if (response.isSuccessful) {
                    val appointmentDetails = response.body()
                    if (appointmentDetails != null && appointmentDetails.success == true) {
                        displayAppointmentDetails(appointmentDetails.data?.appointmentDetail)

                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Failed to get details",
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

            override fun onFailure(call: Call<AppointmentDetails>, t: Throwable) {
                if (!isAdded) {
                    return
                }
                progressBar.visibility = View.GONE // Hide progress bar
                Toast.makeText(requireContext(), "Failed to get details: ${t.message}", Toast.LENGTH_SHORT).show()

            }

            private fun displayAppointmentDetails(appointmentDetail: AppointmentDetail?) {
                appointmentDetail?.let {


                    cancleAppointmentBtn.setOnClickListener {

                        val appointmentID = appointmentDetail.appointmentID ?: ""
                        cancelAppointment(appointmentID)
                    }


//                    requestAppointmentBtn.setOnClickListener {
//                        val doctorAcceptedTime = appointmentDetail.doctorAvailability ?: ""
//                        val appointmentId = appointmentDetail.appointmentID ?: ""
//                        val consultingFee = appointmentDetail.consultingFees
//
//                        showTimePickerDialog(
//                            doctorAcceptedTime,
//                            appointmentId,
//                            consultingFee.toString()
//                        )
//
//                    }

                    appointmentDetail?.let {
                        val startTime = extractTime(it.appointmentStartDateandTime ?: "")
                        val endTime = extractTime(it.appointmentEndDateandTime ?: "")

                        // Format the time range
                        val timeRange = if (!startTime.isNullOrBlank() && !endTime.isNullOrBlank()) {
                            "$startTime - $endTime"
                        } else {
                            " -- : --"
                        }
                        appointmentIdTextView.text = it.appointmentID
                        appointmentDateTextView.text = it.appointmentDate
                        specialityTextView.text = it.speciality
                        notesTextView.text =
                            it.notes?.joinToString("\n") { note -> note.notes.toString() }
                        statusTextView.text = it.status
                        appointmentTime.text = timeRange


                        // Assuming you have access to SharedPreferences in your Fragment or Activity


                        when {
                            it.status.equals("Accepted", ignoreCase = true) -> {
                                val green = ContextCompat.getColor(requireContext(), R.color.green)
                                statusTextView.setTextColor(green)

                                cancleAppointmentBtn.visibility = View.VISIBLE
                            }

                            it.status.equals("PreBookAppointment", ignoreCase = true) -> {
                                val orange =
                                    ContextCompat.getColor(requireContext(), R.color.orange)
                                statusTextView.text = "Waiting for doctor acceptance "
                                statusTextView.setTextColor(orange)
                                cancleAppointmentBtn.visibility = View.VISIBLE

                            }

                            it.status.equals("PaymentCompleted", ignoreCase = true) -> {
                                val greenColor =
                                    ContextCompat.getColor(requireContext(), R.color.green)
                                statusTextView.setTextColor(greenColor)
                                cancleAppointmentBtn.visibility = View.GONE
                            }

                            it.status.equals("Rejected", ignoreCase = true) -> {
                                val redColor =
                                    ContextCompat.getColor(requireContext(), R.color.rejectedColor)
                                statusTextView.setTextColor(redColor)
                                cancleAppointmentBtn.visibility = View.GONE
                            }

                            it.status.equals("Cancelled", ignoreCase = true) -> {
                                val redColor =
                                    ContextCompat.getColor(requireContext(), R.color.rejectedColor)
                                statusTextView.setTextColor(redColor)
                                cancleAppointmentBtn.visibility = View.GONE
                            }

                            else -> {
                                // Handle other statuses if necessary
                                cancleAppointmentBtn.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            }

                private fun cancelAppointment(appointmentID: String) {

                    progressBar.visibility = View.VISIBLE // Show progress bar

                    if (!isAdded) {
                        return
                    }

                    val sharedPreferences = requireContext().getSharedPreferences("your_preference_name", Context.MODE_PRIVATE)
                    val token = sharedPreferences.getString("token", "")

                    val apiService = RetrofitClient.apiService

                    token?.let { authToken ->
                        RetrofitClient.setAuthToken(authToken)
                    }
                    val call = apiService.cancelAppointment(appointmentID)
                    call.enqueue(object : Callback<CancelAppointmentResponse> {
                        override fun onResponse(
                            call: Call<CancelAppointmentResponse>,
                            response: Response<CancelAppointmentResponse>
                        ) {
                            progressBar.visibility = View.GONE
                            if (response.isSuccessful) {
                                // Handle successful response (e.g., notify user, update UI)
                                Toast.makeText(
                                    requireContext(),
                                    "Appointment canceled successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                // Optionally, you can notify the adapter if needed
                                // notifyDataSetChanged()
                            } else {
                                // Handle unsuccessful response (e.g., notify user, log error)
                                Toast.makeText(
                                    requireContext(),
                                    "Failed to cancel appointment",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onFailure(call: Call<CancelAppointmentResponse>, t: Throwable) {
                            // Handle network errors or API call failure (e.g., notify user, log error)
                            progressBar.visibility = View.GONE

                            Toast.makeText(
                                requireContext(),
                                "Failed to cancel appointment: ${t.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
                }

                fun extractTime(dateTime: String): String {
                    if (dateTime.isBlank()) return ""

                    return try {
                        // Parse the date-time string
                        val isoFormat =
                            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault())
                        isoFormat.timeZone = TimeZone.getTimeZone("UTC")
                        val parsedDate = isoFormat.parse(dateTime)

                        // Format the parsed time to "h:mm aa"
                        val outputFormat = SimpleDateFormat("h:mm aa", Locale.getDefault())
                        outputFormat.format(parsedDate)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        ""
                    }
                }
            })
        }
    }
