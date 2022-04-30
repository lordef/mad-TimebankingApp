package it.polito.mad.lab02.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import it.polito.mad.lab02.R
import it.polito.mad.lab02.viewmodels.TimeSlotListViewModel
import it.polito.mad.lab02.databinding.FragmentTimeSlotListBinding

class TimeSlotListFragment : Fragment(R.layout.fragment_time_slot_list) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        super.onViewCreated(view, savedInstanceState)

        val tsb = view.findViewById<Button>(R.id.button3)
        tsb.setOnClickListener{findNavController().navigate(R.id.action_nav_advertisement_to_timeSlotDetailsFragment)}
    }
}