package it.polito.mad.lab02

import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.TextView
import com.google.gson.Gson


/**
 * A simple [Fragment] subclass.
 * Use the [TimeSlotDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TimeSlotDetailsFragment : Fragment(R.layout.fragment_time_slot_details) {

    val title = "timeslot1"
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Retrieve json object of class TimeSlotClass
        val pref = this.context?.let { SharedPreference(it) }
        val gson = Gson()
        val json = pref?.getTimeSlot(title)
        if (!json.equals("")) {
            val obj = gson.fromJson(json, TimeSlotClass::class.java)
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
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.pencil_menu, menu)
    }


}