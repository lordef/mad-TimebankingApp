package it.polito.mad.lab02.fragments.myadvertisements

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.DocumentReference
import it.polito.mad.lab02.R
import it.polito.mad.lab02.models.TimeSlot
import it.polito.mad.lab02.viewmodels.TimeSlotListViewModel
import java.text.SimpleDateFormat
import java.util.*

class TimeSlotEditFragment : Fragment(R.layout.fragment_time_slot_edit) {

    private val vm by activityViewModels<TimeSlotListViewModel>()

    private var isEdit = false
    private var tempID = "0" //useful when isEdit and we must retrieve an existing id

    override fun onResume() {
        super.onResume()
        if(isEdit){
            (activity as AppCompatActivity?)?.supportActionBar?.title = "Edit advertisement"
        }
        else{
            (activity as AppCompatActivity?)?.supportActionBar?.title = "Create advertisement"
        }
    }

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

        val dur = duration.text.split(":")
        var h = 0
        var m = 0
        if (dur.size == 2) {
            h = dur[0].toInt()
            m = dur[1].toInt()
        }
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
            hoursPicker.setOnValueChangedListener(NumberPicker.OnValueChangeListener { _, _, newVal ->
                h = newVal
                calendar.set(Calendar.HOUR_OF_DAY, h)
                calendar.set(Calendar.MINUTE, m)
                durationTextView.text =
                    SimpleDateFormat("HH:mm").format(calendar.time)//"" + h + ":" + m
            })
            minutesPicker.setOnValueChangedListener(NumberPicker.OnValueChangeListener { _, _, newVal ->
                m = newVal
                calendar.set(Calendar.HOUR_OF_DAY, h)
                calendar.set(Calendar.MINUTE, m)
                durationTextView.text = SimpleDateFormat("HH:mm").format(calendar.time)

            })
            val alertDialog = builder.show()

            val button = dialog.findViewById<Button>(R.id.button)
            button.setOnClickListener {
                alertDialog.dismiss()
            }

        }


        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val bundle = addTimeSlot()
                findNavController().navigate(
                    R.id.action_timeSlotEditFragment_to_timeSlotDetailsFragment,
                    bundle
                )
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val date = view?.findViewById<TextView>(R.id.dateEdit)
        val time = view?.findViewById<TextView>(R.id.timeEdit)
        val duration = view?.findViewById<TextView>(R.id.durationEditText)
        outState.putString("date", date?.text.toString())
        outState.putString("time", time?.text.toString())
        outState.putString("duration", duration?.text.toString())

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            val bundle = addTimeSlot()
            findNavController().navigate(
                R.id.action_timeSlotEditFragment_to_timeSlotDetailsFragment,
                bundle
            )

            return true
        }
        return true
    }

    private fun getTimeSlotFromTimeSlotDetailsFragment() {

        val title = view?.findViewById<TextView>(R.id.titleEditText)
        val description = view?.findViewById<TextView>(R.id.descriptionEditText)
        val date = view?.findViewById<TextView>(R.id.dateEdit)
        val time = view?.findViewById<TextView>(R.id.timeEdit)
        val duration = view?.findViewById<TextView>(R.id.durationEditText)
        val location = view?.findViewById<TextView>(R.id.locationEditText)

        val id = arguments?.getString("id")
        if (id == null) {
            isEdit = false
        } else {
            isEdit = true
            tempID = id

            vm.timeslotList.observe(viewLifecycleOwner){
                val ts = it.first { t -> id == t.id }
                title?.text = ts?.title
                description?.text = ts?.description

                val dateTime = ts?.dateTime?.split(" ")
                var d = ""
                var t = ""
                if (dateTime?.size == 2) {
                    d = dateTime[0]
                    t = dateTime[1]
                }

                date?.text = d
                time?.text = t
                duration?.text = ts?.duration
                location?.text = ts?.location
            }
        }

    }

    private fun putDatePicker() {
        val displayDateTextView = view?.findViewById<TextView>(R.id.dateEdit)
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        displayDateTextView!!.setOnClickListener(View.OnClickListener {

            val dialog = DatePickerDialog(
                this.requireContext(),
                DatePickerDialog.OnDateSetListener { _, year, month, day ->
                    val month1 = 1 + month
                    displayDateTextView.text = "" + day + "/" + month1 + "/" + year
                },
                year,
                month,
                day
            )

            dialog.show()
        })
    }

    private fun putTimePicker() {
        val displayTimeTextView = view?.findViewById<TextView>(R.id.timeEdit)
        val calendar = Calendar.getInstance()
        displayTimeTextView!!.setOnClickListener {
            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                displayTimeTextView.text = SimpleDateFormat("HH:mm").format(calendar.time)
            }
            TimePickerDialog(
                this.context,
                timeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }
    }

    private fun addTimeSlot(): Bundle {
        val title = view?.findViewById<EditText>(R.id.titleEditText)
        val description = view?.findViewById<EditText>(R.id.descriptionEditText)
        val date = view?.findViewById<TextView>(R.id.dateEdit)
        val time = view?.findViewById<TextView>(R.id.timeEdit)
        val dateTime = "" + date?.text.toString() + " " + time?.text.toString()
        val duration = view?.findViewById<TextView>(R.id.durationEditText)
        val location = view?.findViewById<EditText>(R.id.locationEditText)
        val id = if (!isEdit) "1" else tempID
        val newTimeSlot = TimeSlot(
            id,
            title?.text.toString(),
            description?.text.toString(),
            dateTime,
            duration?.text.toString(),
            location?.text.toString(),
            "skill1" //TODO:
        )
        val bundle = Bundle()
        val bundleId = vm.updateTimeSlot(newTimeSlot, isEdit)
        if(isEdit){
            bundle.putString("id", tempID)
        }
        else{
            bundle.putString("id", bundleId)
        }
        return bundle
    }

}