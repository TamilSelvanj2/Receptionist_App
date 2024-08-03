package com.bics.expense.receptionistmodule.fragment.appointment


import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.bics.expense.receptionistmodule.R
import com.bics.expense.receptionistmodule.fragment.PastHistory.PastHistoryFragment
import com.bics.expense.receptionistmodule.fragment.Upcoming.UpcomingFragment
import com.bics.expense.receptionistmodule.fragment.document.DocumentFragment
import com.bics.expense.receptionistmodule.fragment.newRequest.NewRequestFragment
import com.bics.expense.receptionistmodule.fragment.rejected.RejectedFragment
import com.google.android.material.tabs.TabLayout

class AppointmentFragment : Fragment() {

    lateinit var tabLayout: TabLayout
    lateinit var viewPager: ViewPager
    private lateinit var adapter: AppointmentAdapter
    private lateinit var upcomingFragment: UpcomingFragment
    private lateinit var pastHistoryFragment: PastHistoryFragment
    private lateinit var rejectedFragment: RejectedFragment
    private lateinit var newrequest: NewRequestFragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_appointment, container, false)
        tabLayout = view.findViewById(R.id.tabLayout)
        viewPager = view.findViewById(R.id.viewPager)


        // Initialize adapter
        adapter = AppointmentAdapter(childFragmentManager)

        // Initialize fragments
        upcomingFragment = UpcomingFragment()
        pastHistoryFragment = PastHistoryFragment()
        rejectedFragment = RejectedFragment()
        newrequest = NewRequestFragment()

        // Add fragments to adapter
        adapter.addFragment(newrequest, "NewRequest")
        adapter.addFragment(upcomingFragment, "Upcoming")
        adapter.addFragment(pastHistoryFragment, "PastHistory")
        adapter.addFragment(rejectedFragment, "Rejected")


        // Set adapter to viewPager
        viewPager.adapter = adapter

        // Connect viewPager with tabLayout
        tabLayout.setupWithViewPager(viewPager)


        // Add TabSelectedListener to handle tab selection events
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    val position = it.position
                    viewPager.currentItem = position
                    // Trigger API call here
                    triggerApiCallForSelectedTab(position)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {
                tab?.let {
                    val position = it.position
                    // Trigger API call here
                    triggerApiCallForSelectedTab(position)


                }
            }
        })

        return view
    }


    private fun triggerApiCallForSelectedTab(position: Int) {
        Log.d("AppointmentFragment", "Tab selected: $position")

        when (position) {
            0 -> {
                // Trigger API call for NewRequest tab
                Log.d("AppointmentFragment", "Refreshing NewRequest data")

                newrequest.refreshData()
            }

            1 -> {
                // Trigger API call for Upcoming tab
                Log.d("AppointmentFragment", "Refreshing Upcoming data")

                upcomingFragment.refreshData()
            }

            2 -> {
                // Trigger API call for PastHistory tab
                Log.d("AppointmentFragment", "Refreshing PastHistory data")

                pastHistoryFragment.refreshData()

            }

            3 -> {
                // Trigger API call for Rejected tab
                Log.d("AppointmentFragment", "Refreshing Rejected data")

                rejectedFragment.refreshData()

            }
        }
    }
}