package it.polito.mad.lab02.fragments

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import it.polito.mad.lab02.R
import it.polito.mad.lab02.Utils
import it.polito.mad.lab02.models.TimeSlot
import it.polito.mad.lab02.viewmodels.TimeSlotDetailsViewModel
import org.json.JSONObject


class TimeSlotDetailsFragment : Fragment(R.layout.fragment_time_slot_details) {

    private val vm by viewModels<TimeSlotDetailsViewModel>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        val title = view.findViewById<TextView>(R.id.titleTextView)
        val description = view.findViewById<TextView>(R.id.descriptionTextView)
        val dateTime = view.findViewById<TextView>(R.id.dateTimeTextView)
        val duration = view.findViewById<TextView>(R.id.durationTextView)
        val location = view.findViewById<TextView>(R.id.locationTextView)
        // TODO: forse è inutile
        val timeSlotId = arguments?.getString("timeslotID")

        if(timeSlotId != null){
            vm.getTimeSlot(timeSlotId!!).observe(viewLifecycleOwner) { timeSlot ->
                title.text = timeSlot.title
                description.text = timeSlot.description
                dateTime.text = timeSlot.dateTime
                duration.text = timeSlot.duration
                location.text = timeSlot.location
            }
            println("shared pref")
        }
        else{
            val timeslot = arguments?.getString("JSON")
            val timeSlotDetailsString = JSONObject(timeslot).toString()
            val timeSlotDetails = Gson().fromJson(timeSlotDetailsString, TimeSlot::class.java)
            title.text = timeSlotDetails.title
            description.text = timeSlotDetails.description
            dateTime.text = timeSlotDetails.dateTime
            duration.text = timeSlotDetails.duration
            location.text = timeSlotDetails.location
            println("bundle")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.pencil_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.editItem -> {
                Toast.makeText(this.context, "Edit TimeSlotDetails selected", Toast.LENGTH_SHORT).show()
                val bundle = editTimeSlot()
                findNavController().navigate(R.id.action_timeSlotDetailsFragment_to_nav_timeSlotEdit, bundle)

                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

    private fun editTimeSlot() : Bundle{
        val title = view?.findViewById<TextView>(R.id.titleTextView)
        val description = view?.findViewById<TextView>(R.id.descriptionTextView)
        val dateTime = view?.findViewById<TextView>(R.id.dateTimeTextView)
        val duration = view?.findViewById<TextView>(R.id.durationTextView)
        val location = view?.findViewById<TextView>(R.id.locationTextView)

        val bundle = Bundle()

        val timeslot = TimeSlot(
            title?.text.toString(),
            description?.text.toString(),
            dateTime?.text.toString(),
            duration?.text.toString(),
            location?.text.toString()
        )

        val timeslotJson = Gson().toJson(timeslot)
        bundle.putString("JSON", timeslotJson.toString())

        return bundle
    }



}