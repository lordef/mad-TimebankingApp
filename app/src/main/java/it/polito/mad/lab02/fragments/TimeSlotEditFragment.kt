package it.polito.mad.lab02.fragments

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import it.polito.mad.lab02.R
import it.polito.mad.lab02.models.TimeSlot
import it.polito.mad.lab02.viewmodels.TimeSlotDetailsViewModel
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class TimeSlotEditFragment : Fragment(R.layout.fragment_time_slot_edit) {

    private val vm by viewModels<TimeSlotDetailsViewModel>()
    //edit is -1 for a new adv, else it is the id of the edited adv
    private var edit = -1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        getTimeSlotFromTimeSlotDetailsFragment()
        val date = view.findViewById<TextView>(R.id.dateEdit)
        val time = view.findViewById<TextView>(R.id.timeEdit)
        val duration = view.findViewById<TextView>(R.id.durationEditText)
        savedInstanceState?.let {
            val dateRestored = savedInstanceState.getString("date")
            val timeRestored = savedInstanceState.getString("time")
            val durationRestored = savedInstanceState.getString("duration")
            date?.text = dateRestored
            time?.text = timeRestored
            duration?.text = durationRestored
        }

        putDatePicker()
        putTimePicker()

        val durationTextView = view.findViewById<TextView>(R.id.durationEditText)

        var h = duration.text.split(":")[0].toInt()
        var m = duration.text.split(":")[1].toInt()
        durationTextView.setOnClickListener {
            val dialog = this.layoutInflater.inflate(R.layout.dialog_duration, null)
            val builder = AlertDialog.Builder(this.context).setView(dialog)

            val hoursPicker = dialog.findViewById<NumberPicker>(R.id.hoursPicker)
            hoursPicker.minValue = 0
            hoursPicker.maxValue = 23
            val minutesPicker = dialog.findViewById<NumberPicker>(R.id.minutesPicker)
            minutesPicker.minValue = 0
            minutesPicker.maxValue = 59

            hoursPicker.value = h
            minutesPicker.value = m

            val durationTextView = view.findViewById<TextView>(R.id.durationEditText)
            val calendar = Calendar.getInstance()
            hoursPicker.setOnValueChangedListener(NumberPicker.OnValueChangeListener(){ _, _, newVal ->
                h = newVal
                calendar.set(Calendar.HOUR_OF_DAY, h)
                calendar.set(Calendar.MINUTE, m)
                durationTextView.text = SimpleDateFormat("HH:mm").format(calendar.time)//"" + h + ":" + m
            })
            minutesPicker.setOnValueChangedListener(NumberPicker.OnValueChangeListener(){ _, _, newVal ->
                m = newVal
                calendar.set(Calendar.HOUR_OF_DAY, h)
                calendar.set(Calendar.MINUTE, m)
                durationTextView.text = SimpleDateFormat("HH:mm").format(calendar.time)

            })
            val alertDialog = builder.show()

            val button = dialog.findViewById<Button>(R.id.button)
            button.setOnClickListener{
                alertDialog.dismiss()
            }

        }


        val callback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                val timeslot = addTimeSlot()
                val bundle = Bundle()
                val timeslotJson = Gson().toJson(timeslot)
                bundle.putString("JSON", timeslotJson.toString())
                findNavController().navigate(R.id.action_timeSlotEditFragment_to_timeSlotDetailsFragment, bundle)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        val date = view?.findViewById<TextView>(R.id.dateEdit)
        val time = view?.findViewById<TextView>(R.id.timeEdit)
        val duration = view?.findViewById<TextView>(R.id.durationEditText)
        outState.putString("date", date?.text.toString())
        outState.putString("time", time?.text.toString())
        outState.putString("duration", duration?.text.toString())
        super.onSaveInstanceState(outState)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            val timeslot = addTimeSlot()
            val bundle = Bundle()
            val timeslotJson = Gson().toJson(timeslot)
            bundle.putString("JSON", timeslotJson.toString())
            findNavController().navigate(R.id.action_timeSlotEditFragment_to_timeSlotDetailsFragment, bundle)

            return true
        }
        return true
    }

    private fun getTimeSlotFromTimeSlotDetailsFragment(){

        val timeslot = arguments?.getString("JSON")
        if(timeslot == null){
            edit = -1
            (activity as AppCompatActivity?)?.supportActionBar?.title = "Create advertisement"
            return
        }
        (activity as AppCompatActivity?)?.supportActionBar?.title = "Edit advertisement"
        val timeSlotDetailsString = JSONObject(timeslot).toString()
        val timeSlotDetails = Gson().fromJson(timeSlotDetailsString, TimeSlot::class.java)
        edit = timeSlotDetails.id.toInt()
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

            val dialog = DatePickerDialog(this.requireContext(), DatePickerDialog.OnDateSetListener { _, year, month, day ->
                val month1 = 1 + month
                displayDateTextView.text = "" + day + "/" + month1 + "/" + year
            }, year, month, day)

            dialog.show()
        })
    }

    private fun putTimePicker(){
        val displayTimeTextView = view?.findViewById<TextView>(R.id.timeEdit)
        val calendar = Calendar.getInstance()
        displayTimeTextView!!.setOnClickListener {
            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                displayTimeTextView.text = SimpleDateFormat("HH:mm").format(calendar.time)
            }
            TimePickerDialog(this.context, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
        }
    }

    private fun addTimeSlot() : TimeSlot{
        val title = view?.findViewById<EditText>(R.id.titleEditText)
        val description = view?.findViewById<EditText>(R.id.descriptionEditText)
        val date = view?.findViewById<TextView>(R.id.dateEdit)
        val time = view?.findViewById<TextView>(R.id.timeEdit)
        val dateTime = "" + date?.text.toString() + " " + time?.text.toString()
        val duration = view?.findViewById<TextView>(R.id.durationEditText)
        val location = view?.findViewById<EditText>(R.id.locationEditText)
        val id = if(edit == -1) vm.getMaxId() else edit
        val obj = TimeSlot(
            id.toString(),
            title?.text.toString(),
            description?.text.toString(),
            dateTime,
            duration?.text.toString(),
            location?.text.toString()
        )
        vm.updateTimeSlot(obj, edit != -1)
        return obj
    }

}