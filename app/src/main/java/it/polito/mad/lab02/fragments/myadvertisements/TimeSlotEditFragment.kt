package it.polito.mad.lab02.fragments.myadvertisements

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import it.polito.mad.lab02.R
import it.polito.mad.lab02.Utils
import it.polito.mad.lab02.Utils.fromHHMMToString
import it.polito.mad.lab02.Utils.fromStringToHHMM
import it.polito.mad.lab02.models.Profile
import it.polito.mad.lab02.models.TimeSlot
import it.polito.mad.lab02.viewmodels.MainActivityViewModel
import java.text.SimpleDateFormat
import java.util.*

class TimeSlotEditFragment : Fragment(R.layout.fragment_time_slot_edit) {

    private val vm by activityViewModels<MainActivityViewModel>()
    private val _optionsMenu = MutableLiveData<Menu?>()
    private val optionsMenu: LiveData<Menu?> = _optionsMenu

    private var isEdit = false
    private var tempID = "0" //useful when isEdit and we must retrieve an existing id
    private var timeSlotState = ""
    private var timeSlotAssignee = ""

    private var fieldsOk = false

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

        getTimeSlotFromTimeSlotDetailsFragment(savedInstanceState)

        val skillText = view.findViewById<TextView>(R.id.skillEditText)
        val skillCard = view.findViewById<CardView>(R.id.skillCardView)

        vm.profile.observe(viewLifecycleOwner) { profile ->
            val skills = profile.skills
            if(skills.isEmpty()){
                if(skillText.text.isEmpty()) skillText.text = "No skill available"
            }
            else{
                if(skillText.text.isEmpty()) skillText.text = skills[0]
                skillCard.setOnClickListener {

                    val dialog = this.layoutInflater.inflate(R.layout.dialog_skills, null)
                    val builder = AlertDialog.Builder(this.context).setView(dialog)


                    val skillsPicker = dialog.findViewById<NumberPicker>(R.id.skillsPicker)
                    skillsPicker.minValue = 0
                    skillsPicker.maxValue = skills.size - 1
                    skillsPicker.displayedValues = skills.toTypedArray()
                    skillsPicker.wrapSelectorWheel = false
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        skillsPicker.textSize = 64F
                    }

                    var temp = skills[0]
                    skillsPicker.setOnValueChangedListener(NumberPicker.OnValueChangeListener { _, _, newVal ->

                        temp = skills[newVal]
                    })


                    val alertDialog = builder.show()

                    val button = dialog.findViewById<Button>(R.id.button)
                    button.setOnClickListener {
                        skillText.text = temp
                        alertDialog.dismiss()
                    }
                }
            }
        }

        val duration = view.findViewById<TextView>(R.id.durationEditText)

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

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                hoursPicker.textSize = 56F
                minutesPicker.textSize = 56F
            }

            val durationTextView = view.findViewById<TextView>(R.id.durationEditText)
            val calendar = Calendar.getInstance()
            hoursPicker.setOnValueChangedListener(NumberPicker.OnValueChangeListener { _, _, newVal ->
                h = newVal
                calendar.set(Calendar.HOUR_OF_DAY, h)
                calendar.set(Calendar.MINUTE, m)
                durationTextView.text = fromHHMMToString(SimpleDateFormat("HH:mm").format(calendar.time))
            })
            minutesPicker.setOnValueChangedListener(NumberPicker.OnValueChangeListener { _, _, newVal ->
                m = newVal
                calendar.set(Calendar.HOUR_OF_DAY, h)
                calendar.set(Calendar.MINUTE, m)
                durationTextView.text = fromHHMMToString(SimpleDateFormat("HH:mm").format(calendar.time))

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
                if (fieldsOk) {
                    findNavController().navigate(
                        R.id.action_timeSlotEditFragment_to_timeSlotDetailsFragment,
                        bundle
                    )
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)
    }



        override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val date = view?.findViewById<TextView>(R.id.dateEdit)
        val time = view?.findViewById<TextView>(R.id.timeEdit)
        val duration = view?.findViewById<TextView>(R.id.durationEditText)
        val skillText = view?.findViewById<TextView>(R.id.skillEditText)
        val location = view?.findViewById<TextView>(R.id.locationEditText)
        val description = view?.findViewById<TextView>(R.id.descriptionEditText)
        val title = view?.findViewById<TextView>(R.id.titleEditText)

        outState.putString("date", date?.text.toString())
        outState.putString("time", time?.text.toString())
        outState.putString("duration", duration?.text.toString())
        outState.putString("skill", skillText?.text.toString())
        outState.putString("location", location?.text.toString())
        outState.putString("description", description?.text.toString())
        outState.putString("title", title?.text.toString())
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            val bundle = addTimeSlot()
            if(fieldsOk) {
                findNavController().navigate(
                    R.id.action_timeSlotEditFragment_to_timeSlotDetailsFragment,
                    bundle
                )
            }
            return true
        } else {
            val builder: androidx.appcompat.app.AlertDialog.Builder =
                androidx.appcompat.app.AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)

            builder.setCancelable(true)
            builder.setTitle("Warning")
            builder.setMessage("Are you sure you want to go back? Your modifications will be lost")

            builder.setNeutralButton("Cancel",
                DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.cancel() })

            builder.setPositiveButton("GO BACK",
                DialogInterface.OnClickListener { dialogInterface, i ->
                        findNavController().navigate(
                            R.id.action_nav_timeSlotEdit_to_nav_advertisement)
                })
            builder.show()

        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.exit_menu, menu)
        _optionsMenu.value = menu
    }


    private fun getTimeSlotFromTimeSlotDetailsFragment(savedInstanceState: Bundle?) {

        val title = view?.findViewById<TextView>(R.id.titleEditText)
        val description = view?.findViewById<TextView>(R.id.descriptionEditText)
        val date = view?.findViewById<TextView>(R.id.dateEdit)
        val time = view?.findViewById<TextView>(R.id.timeEdit)
        val duration = view?.findViewById<TextView>(R.id.durationEditText)
        val location = view?.findViewById<TextView>(R.id.locationEditText)
        val skillText = view?.findViewById<TextView>(R.id.skillEditText)

        savedInstanceState?.let {
            val dateRestored = savedInstanceState.getString("date")
            val timeRestored = savedInstanceState.getString("time")
            val durationRestored = savedInstanceState.getString("duration")
            val skillRestored = savedInstanceState.getString("skill")
            val locationRestored = savedInstanceState.getString("location")
            val descriptionRestored = savedInstanceState.getString("description")
            val titleRestored = savedInstanceState.getString("title")

            date?.text = dateRestored
            time?.text = timeRestored
            duration?.text = fromHHMMToString(durationRestored!!)
            skillText?.text = skillRestored
            location?.text = locationRestored
            description?.text = descriptionRestored
            title?.text = titleRestored
        }

        val id = arguments?.getString("id")
        if (id == null) {
            isEdit = false

        } else {
            isEdit = true
            tempID = id

            vm.loggedUserTimeSlotList.observe(viewLifecycleOwner){
                timeSlotAssignee = it.first { t -> id == t.id }.assignee
                timeSlotState = it.first { t -> id == t.id }.state
                if (savedInstanceState == null) {
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

                    skillText?.text = ts.skill
                    date?.text = d
                    time?.text = t
                    duration?.text = fromHHMMToString(ts?.duration)
                    location?.text = ts?.location
                }
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
        val duration = view?.findViewById<TextView>(R.id.durationEditText)
        val location = view?.findViewById<EditText>(R.id.locationEditText)
        val skillText = view?.findViewById<TextView>(R.id.skillEditText)

        if(title?.text.toString() != "" &&
            description?.text.toString() != "" &&
            date?.text.toString() != "" &&
            time?.text.toString() != "" &&
            duration?.text.toString() != "" &&
            location?.text.toString() != "" &&
            skillText?.text.toString() != ""
        ){
            fieldsOk = true
            val dateTime = "" + date?.text.toString() + " " + time?.text.toString()
            val skillTextTmp = if (skillText?.text.toString() == "No skill available") {
                ""
            } else {
                skillText?.text.toString()
            }

            val id = if (!isEdit) "1" else tempID
            val state = if(!isEdit) "AVAILABLE" else timeSlotState
            val assignee = if(!isEdit) FirebaseAuth.getInstance().currentUser!!.uid else timeSlotAssignee

            val newTimeSlot = TimeSlot(
                id,
                title?.text.toString(),
                description?.text.toString(),
                dateTime,
                Utils.fromStringToHHMM(duration?.text.toString()),
                location?.text.toString(),
                skillTextTmp,
                "user",
                Profile("", "", "", "", "", emptyList(), "", "", 0),
                assignee,
                state,
                listOf(assignee)
            )
            val bundle = Bundle()
            val bundleId = vm.updateTimeSlot(newTimeSlot, isEdit)
            if (isEdit) {
                bundle.putString("id", tempID)
            } else {
                bundle.putString("id", bundleId)
            }
            return bundle
        } else {
            fieldsOk = false
            if (title?.text.toString() == "") {
                val l = view?.findViewById<TextInputLayout>(R.id.titleTextInputLayout)
                l?.error = "Add a title"
            }
            if (description?.text.toString() == "") {
                val l = view?.findViewById<TextInputLayout>(R.id.descriptionTextInputLayout)
                l?.error = "Add a description"
            }
            if (date?.text.toString() == "") {
                val l = view?.findViewById<TextView>(R.id.dateEdit)
                l?.error = "Add a date"
            }
            if (time?.text.toString() == "") {
                val l = view?.findViewById<TextView>(R.id.timeEdit)
                l?.error = "Add a time"
            }
            if (duration?.text.toString() == "") {
                val l = view?.findViewById<TextView>(R.id.durationEditText)
                l?.error = "Add a duration"
            }
            if (skillText?.text.toString() == "") {
                val l = view?.findViewById<TextView>(R.id.skillEditText)
                l?.error = "Add a skill"
            }
            if (location?.text.toString() == "") {
                val l = view?.findViewById<TextInputLayout>(R.id.locationTextInputLayout)
                l?.error = "Add a location"
            }
            Toast.makeText(
                this.context, "Be sure to fill the mandatory fields", Toast.LENGTH_SHORT).show()
            return Bundle()
        }
    }

}