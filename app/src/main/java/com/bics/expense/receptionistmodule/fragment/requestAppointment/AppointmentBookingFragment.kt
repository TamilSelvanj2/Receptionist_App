package com.bics.expense.receptionistmodule.fragment.requestAppointment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.*
import androidx.fragment.app.Fragment
import com.bics.expense.receptionistmodule.R
import com.bics.expense.receptionistmodule.api.RetrofitClient
import com.bics.expense.receptionistmodule.fragment.AppointmentDetail
import com.bics.expense.receptionistmodule.fragment.AppointmentDetails
import com.bics.expense.receptionistmodule.fragment.patientDetailsFragments.CancelAppointmentResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeParseException
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone


class AppointmentBookingFragment : Fragment() {

    private lateinit var appointmentIdTextView: TextView
    private lateinit var appointmentDateTextView: TextView
    private lateinit var specialityTextView: TextView
    private lateinit var notesTextView: TextView
    private lateinit var statusTextView: TextView
    private lateinit var timerTextView: TextView
    private lateinit var requestAppointmentBtn: Button
    private lateinit var cancleAppointmentBtn: Button
    private lateinit var duration: TextView
    private lateinit var appointmentTime: TextView
    private lateinit var linearLayout: LinearLayout
    private lateinit var progressBar: ProgressBar





    private var countDownTimer: CountDownTimer? = null
    private var sourceTab: String = ""


    // Define getContent outside onCreateView

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {


        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_appointment_booking, container, false)

        appointmentIdTextView = view.findViewById(R.id.appointmentIdTextView)
        appointmentDateTextView = view.findViewById(R.id.appointmentDateTextView)
        specialityTextView = view.findViewById(R.id.specialityTextView)
        notesTextView = view.findViewById(R.id.notesTextView)
        statusTextView = view.findViewById(R.id.statusTextView)
        timerTextView = view.findViewById(R.id.timerTextView)
        requestAppointmentBtn = view.findViewById(R.id.requestAppointmentBtn)
        cancleAppointmentBtn = view.findViewById(R.id.cancelAppointmentBtn)
        duration = view.findViewById(R.id.durationTextView)
        appointmentTime = view.findViewById(R.id.appointmentTimeTextView)
        linearLayout = view.findViewById(R.id.linearLayout6Appointment)
        progressBar = view.findViewById(R.id.progressBarAppointmentBook)

        sourceTab = arguments?.getString("SOURCE_TAB") ?: ""
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
      apiService.getAppointmentDetails(appointmentID).enqueue(object : Callback<AppointmentDetails> {
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


                    requestAppointmentBtn.setOnClickListener {
                        val doctorAcceptedTime = appointmentDetail.doctorAvailability ?: ""
                        val appointmentId = appointmentDetail.appointmentID ?: ""
                        val consultingFee = appointmentDetail.consultingFees

                        showTimePickerDialog(
                            doctorAcceptedTime,
                            appointmentId,
                            consultingFee.toString()
                        )

                    }

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
                        notesTextView.text = it.notes?.joinToString("\n") { note -> note.notes.toString() }
                        statusTextView.text = it.status
                        appointmentTime.text = timeRange


                        // Assuming you have access to SharedPreferences in your Fragment or Activity


                        when {
                            it.status.equals("Accepted", ignoreCase = true) -> {
                                val green = getColor(requireContext(), R.color.green)
                                statusTextView.setTextColor(green)

                                requestAppointmentBtn.visibility = View.VISIBLE
                                cancleAppointmentBtn.visibility = View.GONE
                            }

                            it.status.equals("PreBookAppointment", ignoreCase = true) -> {
                                val orange = getColor(requireContext(), R.color.orange)
                                statusTextView.text = "Waiting for doctor acceptance "
                                statusTextView.setTextColor(orange)
                                requestAppointmentBtn.visibility = View.GONE
                                linearLayout.visibility = View.GONE

                            }
                            it.status.equals("PaymentCompleted", ignoreCase = true) -> {
                                val greenColor = getColor(requireContext(), R.color.green)
                                statusTextView.setTextColor(greenColor)
                                requestAppointmentBtn.visibility = View.GONE
                                cancleAppointmentBtn.visibility = View.GONE
                                linearLayout.visibility = View.GONE
                            }

                            it.status.equals("Rejected", ignoreCase = true) -> {
                                val redColor = getColor(requireContext(), R.color.rejectedColor)
                                statusTextView.setTextColor(redColor)
                                cancleAppointmentBtn.visibility = View.GONE
                            }

                            it.status.equals("Cancelled", ignoreCase = true) -> {
                                val redColor = getColor(requireContext(), R.color.rejectedColor)
                                statusTextView.setTextColor(redColor)
                                cancleAppointmentBtn.visibility = View.GONE
                                linearLayout.visibility = View.GONE
                            }

                            else -> {
                                // Handle other statuses if necessary
                                requestAppointmentBtn.visibility = View.GONE
                                cancleAppointmentBtn.visibility = View.VISIBLE
                            }
                        }
                    }
                }
                val currentDateTimeIst = convertUtcToIst(appointmentDetail?.currentDateAndTimeUTC)
// Convert doctorAvailabilityTime to IST if available
                val doctorAvailabilityTimeIst = appointmentDetail?.doctorAvailability?.let { convertTimeToIst(it) }

// Check if both currentDateTimeIst and doctorAvailabilityTimeIst are not null
                if (currentDateTimeIst != null && doctorAvailabilityTimeIst != null) {
                    try {
                        val currentDateTime = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            LocalDateTime.parse(currentDateTimeIst)
                        } else {
                            TODO("VERSION.SDK_INT < O")
                        }
                        val doctorAvailabilityDateTime =
                            LocalDateTime.parse(doctorAvailabilityTimeIst)

                        val duration =
                            java.time.Duration.between(currentDateTime, doctorAvailabilityDateTime)
                        val daysLeft = duration.toDays()
                        val hoursLeft = duration.toHours() % 24
                        val minutesLeft = duration.toMinutes() % 60
                        val secondsLeft = duration.seconds % 60

                        val formattedHours = String.format("%02d", hoursLeft)
                        val formattedMinutes = String.format("%02d", minutesLeft)
                        val formattedSeconds = String.format("%02d", secondsLeft)


                        if (daysLeft > 0) {
                            timerTextView.text = "$daysLeft days, $formattedHours:$formattedMinutes:$formattedSeconds"
                        } else {
                            timerTextView.text = "$formattedHours:$formattedMinutes:$formattedSeconds"
                        }

                        countDownTimer?.cancel()
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            countDownTimer = object : CountDownTimer(duration.toMillis(), 1000) {
                                override fun onTick(millisUntilFinished: Long) {
                                    val duration = java.time.Duration.between(
                                        LocalDateTime.now(),
                                        doctorAvailabilityDateTime
                                    )
                                    val daysLeft = duration.toDays()
                                    val hoursLeft = duration.toHours() % 24
                                    val minutesLeft = duration.toMinutes() % 60
                                    val secondsLeft = duration.seconds % 60

                                    val formattedHours = String.format("%02d", hoursLeft)
                                    val formattedMinutes = String.format("%02d", minutesLeft)
                                    val formattedSeconds = String.format("%02d", secondsLeft)


                                    if (daysLeft > 0) {
                                        timerTextView.text = "$daysLeft days, $formattedHours:$formattedMinutes:$formattedSeconds"
                                    } else {
                                        timerTextView.text = "$formattedHours:$formattedMinutes:$formattedSeconds"
                                    }
                                }

                                override fun onFinish() {

                                    when (statusTextView.text.toString()) {
                                        "PaymentCompleted", "Cancelled" -> {
                                            cancleAppointmentBtn.visibility = View.GONE
                                            linearLayout.visibility = View.GONE
                                        }

                                        else -> {
                                            cancleAppointmentBtn.visibility = View.VISIBLE
                                        }
                                    }
                                    requestAppointmentBtn.visibility = View.GONE
                                    linearLayout.visibility = View.GONE
                                }
                            }.start()
                        }
                    } catch (e: DateTimeParseException) {
                        // Handle parsing exceptions
                        timerTextView.text = "Error: Unable to parse date-time"
                    }
                } else {
                    timerTextView.text = " "
                }
            }


            private fun convertUtcToIst(utcTime: String?): String {
                if (utcTime == null) return "N/A"
                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                sdf.timeZone = TimeZone.getTimeZone("UTC")
                return try {
                    val date = sdf.parse(utcTime)
                    val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kolkata"))
                    calendar.timeInMillis = date.time
                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault()).format(
                        calendar.time
                    )
                } catch (e: ParseException) {
                    e.printStackTrace()
                    "N/A"
                }
            }

          private fun convertTimeToIst(time: String?): String {
              if (time == null) return "N/A"
              val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
              sdf.timeZone = TimeZone.getTimeZone("Asia/Kolkata") // Set the time zone to IST
              return try {
                  val date = sdf.parse(time)
                  SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault()).format(date)
              } catch (e: ParseException) {
                  e.printStackTrace()
                  "N/A"
              }
          }



          @SuppressLint("MissingInflatedId")
            private fun showTimePickerDialog(
                doctorAcceptedTime: String,
                appointmentId: String,
                consultingFee: String

            ) {
                val doctorAcceptedIst = convertUtcToIsts(doctorAcceptedTime)
                val builder = AlertDialog.Builder(requireContext(),R.style.RoundedTabLayoutStyle)
                val inflater = LayoutInflater.from(requireContext())
                val timerPicker = inflater.inflate(R.layout.time_cardview, null)

                val textViewTime = timerPicker.findViewById<TextView>(R.id.editTextTime)
                textViewTime.text = doctorAcceptedIst

                val nextButton = timerPicker.findViewById<Button>(R.id.buttonNext)
                val cancelButton = timerPicker.findViewById<Button>(R.id.buttonCancel)

                builder.setView(timerPicker)
                val dialog = builder.create()
                dialog.show()
                nextButton.setOnClickListener {
                    sendPaymentToServer(appointmentId, consultingFee)
                    dialog.dismiss()
                }

                cancelButton.setOnClickListener {
                    dialog.dismiss()
                }
            }

            private fun convertUtcToIsts(utcTime: String): String {
                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).apply {
                    timeZone = TimeZone.getTimeZone("Asia/Kolkata")
                }

                return try {
                    val date = sdf.parse(utcTime)
                    val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                    val timeFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault())

                    val formattedDate = dateFormatter.format(date)
                    val formattedTime = timeFormatter.format(date)

                    "$formattedDate $formattedTime"
                } catch (e: ParseException) {
                    e.printStackTrace()
                    "N/A"
                }
            }

            @SuppressLint("MissingInflatedId")
            private fun sendPaymentToServer(appointmentId: String, consultingFee: String) {
                val builder = AlertDialog.Builder(requireContext(),R.style.RoundedTabLayoutStyle)
                val inflater = LayoutInflater.from(requireContext())
                val paymentView = inflater.inflate(R.layout.custom_payment, null)


                // Get views from the custom payment layout
                val btnSubmit = paymentView.findViewById<Button>(R.id.btnSubmit)
                val btnCancel = paymentView.findViewById<Button>(R.id.btnCancel)
                val consult = paymentView.findViewById<TextView>(R.id.textViewConsultingFee)
                val cashCardView = paymentView.findViewById<CardView>(R.id.cashCardView)
                val onlineCardView = paymentView.findViewById<CardView>(R.id.onlineCardView)

                var selectedPaymentMethod: String? = null
                builder.setView(paymentView)

                // Create and show the AlertDialog
                val dialog = builder.create()
                dialog.show()

                consult.text = consultingFee
                // Set OnClickListener for the Submit button
                // Set OnClickListener for the Cash CardView
                cashCardView.setOnClickListener {
                    // Highlight the selected payment method (e.g., change background color)
                    cashCardView.setCardBackgroundColor(Color.parseColor("#9E9E9E")) // Example color
                    onlineCardView.setCardBackgroundColor(Color.WHITE) // Reset other card view color if necessary
                    // Set the selected payment method
                    selectedPaymentMethod = "CASH"
                }

                // Set OnClickListener for the Online CardView
                onlineCardView.setOnClickListener {
                    // Highlight the selected payment method (e.g., change background color)
                    onlineCardView.setCardBackgroundColor(Color.parseColor("#9E9E9E")) // Example color
                    cashCardView.setCardBackgroundColor(Color.WHITE) // Reset other card view color if necessary
                    // Set the selected payment method
                    selectedPaymentMethod = "ONLINE"

                }

                // Set OnClickListener for the Submit button
                btnSubmit.setOnClickListener {
                    // Check if a payment method is selected
                    if (selectedPaymentMethod == null) {
                        // Show an error message or toast indicating that a payment method must be selected
                        Toast.makeText(
                            requireContext(),
                            "Please select a payment method",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        // Proceed with submitting payment to server based on the selected method
                        if (selectedPaymentMethod == "CASH") {
                            processCashPayment(appointmentId)

                        } else if (selectedPaymentMethod == "ONLINE") {
                            processCashPayment(appointmentId)
                        }

                    }
                }
                btnCancel.setOnClickListener {
                    dialog.dismiss()
                }

                // Set the custom view to the AlertDialog

            }

            private fun processCashPayment(appointmentID: String) {
                progressBar.visibility = View.VISIBLE
                if (!isAdded) {
                    return
                }

                val sharedPreferences = requireContext().getSharedPreferences("your_preference_name", Context.MODE_PRIVATE)
                val token = sharedPreferences.getString("token", "")

                val apiService = RetrofitClient.apiService

                token?.let { authToken ->
                    RetrofitClient.setAuthToken(authToken)
                }

                // Create a PaymentRequest object for cash payment
                val paymentRequest = PaymentRequest(appointmentID = appointmentID, paymentType = "Cash")

                // Call the API to update appointment payment
                val call = apiService.updateAppointmentPayment(paymentRequest)
                call.enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {

                        progressBar.visibility = View.GONE

                        if (response.isSuccessful) {

                            Toast.makeText(
                                requireContext(),
                                "Payment successful",
                                Toast.LENGTH_SHORT
                            ).show()
                            requireActivity().finish()

                            // Optionally, you can notify the adapter if needed
                            // notifyDataSetChanged()
                        } else {
                            Toast.makeText(requireContext(), "Payment failed", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {

                        progressBar.visibility = View.GONE

                        Toast.makeText(
                            requireContext(),
                            "Payment failed: ${t.message}",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                })
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
