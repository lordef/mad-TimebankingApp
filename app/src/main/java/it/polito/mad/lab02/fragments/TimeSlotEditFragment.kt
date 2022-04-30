package it.polito.mad.lab02.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import it.polito.mad.lab02.R
import it.polito.mad.lab02.SharedPreference
import it.polito.mad.lab02.models.TimeSlot
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class TimeSlotEditFragment : Fragment(R.layout.fragment_time_slot_edit) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        getTimeSlotFromTimeSlotDetailsFragment()

        val button = view.findViewById<Button>(R.id.button)
        button.setOnClickListener(View.OnClickListener { addSP() })

        putDatePicker()
        putTimePicker()

    }



    private fun getTimeSlotFromTimeSlotDetailsFragment(){

        val timeslot = arguments?.getString("JSON")
        val timeSlotDetailsString = JSONObject(timeslot).toString()
        val timeSlotDetails = Gson().fromJson(timeSlotDetailsString, TimeSlot::class.java)

        val title = view?.findViewById<TextView>(R.id.titleEditText)
        val description = view?.findViewById<TextView>(R.id.descriptionEditText)
        val date = view?.findViewById<TextView>(R.id.dateEdit)
        val time = view?.findViewById<TextView>(R.id.timeEdit)
        val duration = view?.findViewById<TextView>(R.id.durationEditText)
        val location = view?.findViewById<TextView>(R.id.locationEditText)

        title?.text = timeSlotDetails.title
        description?.text = timeSlotDetails.description
        date?.text = timeSlotDetails.dateTime.split(" ")[0].toString()
        time?.text = timeSlotDetails.dateTime.split(" ")[1].toString()
        duration?.text = timeSlotDetails.duration
        location?.text = timeSlotDetails.location
    }

    private fun putDatePicker(){
        val displayDateTextView = view?.findViewById<TextView>(R.id.dateEdit)
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        displayDateTextView!!.setOnClickListener(View.OnClickListener {

            val dialog = DatePickerDialog(this.requireContext(), DatePickerDialog.OnDateSetListener { view, year, month, day ->
                displayDateTextView.text = "" + day + "/" + month + "/" + year
            }, year, month, day)

            dialog.show()
        })
    }

    private fun putTimePicker(){
        val displayTimeTextView = view?.findViewById<TextView>(R.id.timeEdit)
        val calendar = Calendar.getInstance()
        displayTimeTextView!!.setOnClickListener {
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                displayTimeTextView.text = SimpleDateFormat("HH:mm").format(calendar.time)
            }
            TimePickerDialog(this.context, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
        }
    }

    private fun addSP() {
        val title = view?.findViewById<EditText>(R.id.titleEditText)
        val description = view?.findViewById<EditText>(R.id.descriptionEditText)
        val date = view?.findViewById<TextView>(R.id.dateEdit)
        val time = view?.findViewById<TextView>(R.id.timeEdit)
        val dateTime = "" + date?.text.toString() + " " + time?.text.toString()
        val duration = view?.findViewById<EditText>(R.id.durationEditText)
        val location = view?.findViewById<EditText>(R.id.locationEditText)
        val obj = TimeSlot(
            title?.text.toString(),
            description?.text.toString(),
            dateTime,
            duration?.text.toString(),
            location?.text.toString()
        )
        val sp = this.context?.let { SharedPreference(it) }
        if (sp != null) {
            if (title != null) {
                sp.setTimeSlot(title.text.toString(), obj)
            }
        }
    }

}