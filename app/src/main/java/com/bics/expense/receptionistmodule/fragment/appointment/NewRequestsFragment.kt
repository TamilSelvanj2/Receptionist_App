package com.bics.expense.receptionistmodule.fragment.appointment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.bics.expense.receptionistmodule.R
import com.bics.expense.receptionistmodule.fragment.doctor.DoctorDetailFragment
import com.bics.expense.receptionistmodule.fragment.document.DocumentsFragment
import com.bics.expense.receptionistmodule.fragment.newRequestPatient.NewRequestPatientsFragment
import com.bics.expense.receptionistmodule.fragment.requestAppointment.AppointmentBookingsFragment
import com.google.android.material.tabs.TabLayout


class NewRequestsFragment : Fragment() {

    private lateinit var tabLayout: TabLayout
    lateinit var viewPager: ViewPager
    private lateinit var adapter: NewRequestAppointment
    private lateinit var appointmentBooking: AppointmentBookingsFragment
    private lateinit var newRequestPatient: NewRequestPatientsFragment
    private lateinit var doctorDetails: DoctorDetailFragment
    private lateinit var document: DocumentsFragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_new_requests, container, false)
        tabLayout = view.findViewById(R.id.tabLayoutNewRequests)
        viewPager = view.findViewById(R.id.viewPagerNewRequests)

        adapter = NewRequestAppointment(childFragmentManager)

        val appointmentID = arguments?.getString("APPOINTMENT_ID")

        // Create and pass the appointment ID to each fragment
        appointmentBooking = AppointmentBookingsFragment().apply {
            arguments = Bundle().apply {
                putString("APPOINTMENT_ID", appointmentID)
            }
        }

        newRequestPatient = NewRequestPatientsFragment().apply {
            arguments = Bundle().apply {
                putString("APPOINTMENT_ID", appointmentID)
            }
        }

        doctorDetails = DoctorDetailFragment().apply {
            arguments = Bundle().apply {
                putString("APPOINTMENT_ID", appointmentID)
            }
        }
        document = DocumentsFragment().apply {
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
