package it.polito.mad.lab02.fragments.myadvertisements

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import it.polito.mad.lab02.R
import it.polito.mad.lab02.viewmodels.MainActivityViewModel


class TimeSlotDetailsFragment : Fragment(R.layout.fragment_time_slot_details) {

    private val vm by activityViewModels<MainActivityViewModel>()
    private val _optionsMenu = MutableLiveData<Menu?>()
    private val optionsMenu: LiveData<Menu?> = _optionsMenu

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        val title = view.findViewById<TextView>(R.id.titleTextView)
        val description = view.findViewById<TextView>(R.id.descriptionTextView)
        val dateTime = view.findViewById<TextView>(R.id.dateTimeTextView)
        val duration = view.findViewById<TextView>(R.id.durationTextView)
        val location = view.findViewById<TextView>(R.id.locationTextView)
        val skill = view.findViewById<TextView>(R.id.skillTextView)

        val id = arguments?.getString("id")
        if(id != null){
            vm.loggedUserTimeSlotList.observe(viewLifecycleOwner){
                val timeSlotTmp = it.filter { ts -> id == ts.id }
                if(timeSlotTmp.isNotEmpty()){
                    val timeSlot = timeSlotTmp.first()

                    optionsMenu.observe(viewLifecycleOwner){ menu ->
                        if(timeSlot.state == "ACCEPTED" && menu != null && menu.findItem(R.id.editItem) != null){
                            menu.findItem(R.id.editItem).isVisible = false
                        }
                        else if(timeSlot.state == "AVAILABLE" && menu != null && menu.findItem(R.id.editItem) != null){
                            menu.findItem(R.id.editItem).isVisible = true
                        }
                    }


                    title.text = timeSlot.title
                    description.text = timeSlot.description
                    dateTime.text = timeSlot.dateTime
                    val d = timeSlot.duration.split(":")

                    if(d.size == 2){
                        duration.text = "" + d[0] + "h " + d[1] + "min"
                    }
                    else{
                        duration.text = ""
                    }
                    location.text = timeSlot.location
                    skill.text = timeSlot.skill
                }
            }
        }

        val callback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                findNavController().popBackStack(R.id.nav_advertisement,false)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.pencil_menu, menu)
        _optionsMenu.value = menu
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.editItem -> {
                Toast.makeText(this.context, "Edit TimeSlotDetails selected", Toast.LENGTH_SHORT)
                    .show()
                val bundle = editTimeSlot()
                findNavController().navigate(
                    R.id.action_timeSlotDetailsFragment_to_nav_timeSlotEdit,
                    bundle
                )

                true
            }
            android.R.id.home -> {
                findNavController().popBackStack(R.id.nav_advertisement,false)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

    private fun editTimeSlot(): Bundle {
        val bundle = Bundle()
        bundle.putString("id", arguments?.getString("id"))

        return bundle
    }


}