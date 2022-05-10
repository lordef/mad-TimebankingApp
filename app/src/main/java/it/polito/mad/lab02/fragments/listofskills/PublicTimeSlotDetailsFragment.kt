package it.polito.mad.lab02.fragments.listofskills

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import it.polito.mad.lab02.R
import it.polito.mad.lab02.viewmodels.PublicTimeSlotListViewModel
import it.polito.mad.lab02.viewmodels.TimeSlotDetailsViewModel


class PublicTimeSlotDetailsFragment : Fragment(R.layout.fragment_public_time_slot_details) {

    private val vm by viewModels<PublicTimeSlotListViewModel>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        val title = view.findViewById<TextView>(R.id.titleTextView)
        val description = view.findViewById<TextView>(R.id.descriptionTextView)
        val dateTime = view.findViewById<TextView>(R.id.dateTimeTextView)
        val duration = view.findViewById<TextView>(R.id.durationTextView)
        val location = view.findViewById<TextView>(R.id.locationTextView)

        val profile = view.findViewById<TextView>(R.id.publisherTextView)

        val id = arguments?.getString("id")
        if(id != null){
            vm.getTimeSlot(id).observe(viewLifecycleOwner){
                title.text = it.title
                description.text = it.description
                dateTime.text = it.dateTime
                val d = it.duration.split(":")

                if(d.size == 2){
                    duration.text = "" + d[0] + "h " + d[1] + "min"
                }
                else{
                    duration.text = ""
                }
                location.text = it.location
                //profile.text =
            }
        }

        val callback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                findNavController().popBackStack(R.id.nav_advertisement,false)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {

            android.R.id.home -> {
                findNavController().popBackStack(R.id.nav_advertisement,false)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }


}