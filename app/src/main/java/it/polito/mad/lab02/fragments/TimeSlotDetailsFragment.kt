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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import it.polito.mad.lab02.R
import it.polito.mad.lab02.SharedPreference
import it.polito.mad.lab02.models.TimeSlotDetailsModel
import it.polito.mad.lab02.viewmodels.TimeSlotDetailsViewModel


class TimeSlotDetailsFragment : Fragment(R.layout.fragment_time_slot_details) {

    private val vm by viewModels<TimeSlotDetailsViewModel>()

    private val title = "timeslot1"
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        var obj: TimeSlotDetailsModel

        //TODO: trying to retrieve content from ViewModel
        vm.getTimeSlotDetails().observe(viewLifecycleOwner) { tsd ->
            // update UI
            obj = tsd

            // Put it into the TextViews
            val title = view.findViewById<TextView>(R.id.titleTextView)
            val description = view.findViewById<TextView>(R.id.descriptionTextView)
            val dateTime = view.findViewById<TextView>(R.id.dateTimeTextView)
            val duration = view.findViewById<TextView>(R.id.durationTextView)
            val location = view.findViewById<TextView>(R.id.locationTextView)

            title.text = obj.title
            description.text = obj.description
            dateTime.text = obj.dateTime
            duration.text = obj.duration
            location.text = obj.location

        }


        //TODO: old code to manage
        // Retrieve json object of class TimeSlotClass
        /*
        val pref = this.context?.let { SharedPreference(it) }
        val gson = Gson()
        val json = pref?.getTimeSlotDetails(title)
        if (!json.equals("")) {
            val obj = gson.fromJson(json, TimeSlotDetailsModel::class.java)

            // Put it into the TextViews
            val title = view.findViewById<TextView>(R.id.titleTextView)
            val description = view.findViewById<TextView>(R.id.descriptionTextView)
            val dateTime = view.findViewById<TextView>(R.id.dateTimeTextView)
            val duration = view.findViewById<TextView>(R.id.durationTextView)
            val location = view.findViewById<TextView>(R.id.locationTextView)

            if (obj !== null) {
                title.text = obj.title
                description.text = obj.description
                dateTime.text = obj.dateTime
                duration.text = obj.duration
                location.text = obj.location
            }
        }
         */
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.pencil_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.editItem -> {
                Toast.makeText(this.context, "Edit TimeSlotDetails selected", Toast.LENGTH_SHORT)
                    .show()
                // TODO: manage transition to TimeSlotEditFragment
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }


}