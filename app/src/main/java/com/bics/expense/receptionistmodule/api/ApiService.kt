package com.bics.expense.receptionistmodule.api


import com.bics.expense.receptionistmodule.addPatient.AddPatientResponse
import com.bics.expense.receptionistmodule.addPatient.PatientDetails
import com.bics.expense.receptionistmodule.forgotpassword.ForgotPasswordResponse
import com.bics.expense.receptionistmodule.fragment.AppointmentDetails
import com.bics.expense.receptionistmodule.fragment.PastHistory.PastAppointmentsRequest
import com.bics.expense.receptionistmodule.fragment.PastHistory.PastHistoryModule
import com.bics.expense.receptionistmodule.fragment.Upcoming.UpcomingModule
import com.bics.expense.receptionistmodule.fragment.booked.BookResponse
import com.bics.expense.receptionistmodule.fragment.home.DashboardResponse
import com.bics.expense.receptionistmodule.fragment.newRequest.NewRequestModule
import com.bics.expense.receptionistmodule.fragment.patient.PatientsResponse
import com.bics.expense.receptionistmodule.fragment.patientDetailsFragments.CancelAppointmentResponse
import com.bics.expense.receptionistmodule.fragment.patientDetailsFragments.CustomAppointment
import com.bics.expense.receptionistmodule.fragment.profile.PasswordUpdateRequest
import com.bics.expense.receptionistmodule.fragment.profile.PasswordUpdateResponse
import com.bics.expense.receptionistmodule.fragment.profile.ProfileRequest
import com.bics.expense.receptionistmodule.fragment.profile.ProfileUpdateRequest
import com.bics.expense.receptionistmodule.fragment.rejected.RejectAppoinmentRequest
import com.bics.expense.receptionistmodule.fragment.rejected.RejectedModel
import com.bics.expense.receptionistmodule.fragment.document.AddDocumentRequest
import com.bics.expense.receptionistmodule.fragment.document.DocumentResponse
import com.bics.expense.receptionistmodule.fragment.document.DocumentTypesResponse
import com.bics.expense.receptionistmodule.login.LoginRequest
import com.bics.expense.receptionistmodule.login.LoginResponse
import com.bics.expense.receptionistmodule.notification.NotificationResponse
import com.bics.expense.receptionistmodule.patientDetails.PatientDetailsModel
import com.bics.expense.receptionistmodule.fragment.requestAppointment.PaymentRequest
import com.bics.expense.receptionistmodule.scheduleDoctor.AppointmentResponse
import com.bics.expense.receptionistmodule.scheduleDoctor.CreateAppointmentRequest
import com.bics.expense.receptionistmodule.scheduleDoctor.DoctorResponse
import com.bics.expense.receptionistmodule.scheduleDoctor.PatientResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {

    @POST("/Authentication/ClinicalUserLogin")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @GET("/Patient/GetPatientDetailsBasedOnPhone")
    fun getPatientDetailsBasedOnPhone(@Query("Phone") phoneNumber: String): Call<DashboardResponse>

    @GET("/LookUp/GetAllSpeciality")
    fun getAllSpecialities(@Query("Authorization") token: String): Call<PatientResponse>

    @GET("/Doctor/GetDoctorsBasedOnSpeciality")
    fun getDoctorsBasedOnSpeciality(
        @Query("SpecialityId") specialityId: String,
        @Query("Authorization") token: String
    ): Call<DoctorResponse>

    @POST("/Patient/CreateNewPatientBasedOnPhone")
    fun addPatient(@Body patientDetails: PatientDetails): Call<AddPatientResponse>

    @POST("/Appointment/CreateAppointmentRequest")
    fun createAppointment(
        @Header("Authorization") token: String,
        @Body request: CreateAppointmentRequest
    ): Call<AppointmentResponse>

    @GET("/Appointment/GetAppointmentDetailsBasedOnClinic")
    fun getAppointments(): Call<BookResponse>

    @GET("/Patient/GetPatientDetailsBasedOnPatientID")
    fun getPatientDetails(@Query("PatientID") patientID: String): Call<PatientDetailsModel>

    @GET("/Appointment/GetAppointmentDetailsBasedOnPatientID")
    fun getUpcomingAppointmentDetails(@Query("PatientID") patientID: String): Call<CustomAppointment>

    @GET("/Authentication/GetProfileDetails")
    fun getProfileDetails(): Call<ProfileRequest>

    @PUT("/Authentication/UpdateProfileDetails")
    fun updateProfileDetails(@Body profileUpdateRequest: ProfileUpdateRequest): Call<ProfileUpdateRequest>


    @POST("/Appointment/GetPastAppointmentDetailsBasedOnClinic")
    fun getPastAppointments(@Body requestBody: PastAppointmentsRequest): Call<PastHistoryModule>

    @GET("/Appointment/GetUpcomingAppointmentDetailsBasedOnClinic")
    fun getUpcomingAppointments(): Call<UpcomingModule>


    @POST("/Appointment/GetRejectedAppointmentDetailsBasedOnClinic")
    fun getRejectedAppointments(@Body requestBody: RejectAppoinmentRequest): Call<RejectedModel>

    @PUT("/Appointment/UpdateAppointmentPayment")
    fun updateAppointmentPayment(@Body request: PaymentRequest): Call<Void>

    @PUT("Appointment/CancelAppointment")
    fun cancelAppointment(@Query("AppointmentID") appointmentID: String): Call<CancelAppointmentResponse>

    @GET("/Patient/GetPatientDetailsBasedOnClinic")
    fun getPatients(): Call<PatientsResponse>

    @GET("/Appointment/GetNewAppointmentRequestBasedOnClinic")
    fun getNewRequest(): Call<NewRequestModule>
////
    @GET("Appointment/GetAppointmentDetailsBasedOnAppointmentID")
    fun getAppointmentDetails(@Query("AppointmentID") appointmentID: String): Call<AppointmentDetails>

    @GET("Notifications/GetNotificationsByAccountID")
    fun getNotificationsByAccountID(@Query("AccountID") accountID: String): Call<NotificationResponse>

    @GET("Authentication/ForgotPassword")
    fun forgotPassword(@Query("userId") userId: String): Call<ForgotPasswordResponse>


    @Multipart
    @POST("/Document/AddDocumentBasedOnAppointmentID")
    fun uploadDocument(
        @Part("AppointmentID") appointmentID: RequestBody,
        @Part document: MultipartBody.Part,
        @Part("DocumentTypeID") documentTypeID: RequestBody
    ): Call<AddDocumentRequest>

    @GET("Document/GetDocumentTypes")
    fun getDocumentTypes(): Call<DocumentTypesResponse>

    @GET("Document/GetDocumentsBasedOnAppointmentID")
    fun getDocuments(@Query("AppointmentID") appointmentID: String): Call<DocumentResponse>

    @HTTP(method = "DELETE", path = "/Document/DeleteDocumentBasedOnAppointmentNotesID", hasBody = true)
    fun deleteDocumentBasedOnAppointmentNotesID(@Body requestBody: RequestBody): Call<Void>


    @PUT("Authentication/UpdatePassword")
    fun updatePassword(@Body passwordUpdateRequest: PasswordUpdateRequest): Call<PasswordUpdateResponse>
}



