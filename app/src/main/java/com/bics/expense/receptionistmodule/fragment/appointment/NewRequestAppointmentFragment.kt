package com.bics.expense.receptionistmodule.fragment.appointment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.bics.expense.receptionistmodule.R
import com.bics.expense.receptionistmodule.fragment.doctor.DoctorDetailsFragment
import com.bics.expense.receptionistmodule.fragment.document.DocumentFragment
import com.bics.expense.receptionistmodule.fragment.newRequestPatient.NewRequestPatientFragment
import com.bics.expense.receptionistmodule.fragment.requestAppointment.AppointmentBookingFragment
import com.google.android.material.tabs.TabLayout


class   NewRequestAppointmentFragment : Fragment() {

    private lateinit var tabLayout: TabLayout
    lateinit var viewPager: ViewPager
    private lateinit var adapter: NewRequestAppointment
    private lateinit var appointmentBooking: AppointmentBookingFragment
    private lateinit var newRequestPatient: NewRequestPatientFragment
    private lateinit var doctorDetails: DoctorDetailsFragment
    private lateinit var document: DocumentFragment


    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_new_request_appointment, container, false)
        tabLayout = view.findViewById(R.id.tabLayoutNewRequest)
        viewPager = view.findViewById(R.id.viewPagerNewRequest)

        adapter = NewRequestAppointment(childFragmentManager)

        val appointmentID = arguments?.getString("APPOINTMENT_ID")

        // Create and pass the appointment ID to each fragment
        appointmentBooking = AppointmentBookingFragment().apply {
            arguments = Bundle().apply {
                putString("APPOINTMENT_ID", appointmentID)
            }
        }

        newRequestPatient = NewRequestPatientFragment().apply {
            arguments = Bundle().apply {
                putString("APPOINTMENT_ID", appointmentID)
            }
        }

        doctorDetails = DoctorDetailsFragment().apply {
            arguments = Bundle().apply {
                putString("APPOINTMENT_ID", appointmentID)
            }
        }
        document = DocumentFragment().apply {
            arguments = Bundle().apply {
                putString("APPOINTMENT_ID", appointmentID)
            }
        }

        adapter.addFragments(appointmentBooking,"Appointment")
        adapter.addFragments(newRequestPatient,"Patient")
        adapter.addFragments(doctorDetails,"Doctor")
        adapter.addFragments(document,"Document")



        viewPager.adapter = adapter

        // Connect viewPager with tabLayout
        tabLayout.setupWithViewPager(viewPager)


        return view
    }
}
