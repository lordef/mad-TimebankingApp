package it.polito.mad.lab02.fragments.myadvertisements

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.firebase.Timestamp
import it.polito.mad.lab02.R
import it.polito.mad.lab02.fragments.communication.MyChatRecyclerViewAdapter
import it.polito.mad.lab02.viewmodels.MainActivityViewModel
import java.util.*


class TimeSlotAssignedAndAcceptedFragment : Fragment(R.layout.fragment_time_slot_assigned_and_accepted_list) {

    private var columnCount = 1

    private val vm by activityViewModels<MainActivityViewModel>()

    private var selector = 0


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity?)?.supportActionBar?.title = "Timeslots you accepted"
        showAssignedOrAccepted()

        val toggleButton = view.findViewById<MaterialButtonToggleGroup>(R.id.toggleButton)

        toggleButton.addOnButtonCheckedListener { toggleButton, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.publisherButton -> {
                        (activity as AppCompatActivity?)?.supportActionBar?.title = "Timeslots assigned to you"
                        selector = 0
                        showAssignedOrAccepted()
                    }

                    R.id.requesterButton -> {
                        (activity as AppCompatActivity?)?.supportActionBar?.title = "Timeslots you accepted"
                        selector = 1
                        showAssignedOrAccepted()
                    }
                }
            } else {
                if (toggleButton.checkedButtonId == View.NO_ID) {

                }
            }
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                view.findNavController().navigateUp()
                onBackPressed()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)

    }

    private fun showAssignedOrAccepted(){
        val nAccText = view?.findViewById<TextView>(R.id.text_no_adv_accepted)
        val nAssTxt = view?.findViewById<TextView>(R.id.text_no_adv_assigned)
        val recyclerView = view?.findViewById<RecyclerView>(R.id.aaAdvlist)
        if (selector == 0) { //timeslot assegnati -> timeslot di altri che io ho prenotato e loro hanno accettato di farmi
            nAccText?.visibility = View.GONE
            vm.setMyAssignedTimeSlotListListener()

            vm.myAssignedTimeSlotList.observe(viewLifecycleOwner) { timeSlotList ->
                if (timeSlotList.isEmpty()) {
                    recyclerView?.visibility = View.GONE
                    nAssTxt?.visibility = View.VISIBLE
                } else {
                    nAssTxt?.visibility = View.GONE
                    recyclerView?.visibility = View.VISIBLE
                }

                if (recyclerView is RecyclerView) {
                    with(recyclerView) {
                        layoutManager = when {
                            columnCount <= 1 -> LinearLayoutManager(context)
                            else -> GridLayoutManager(context, columnCount)
                        }
                        adapter =
                            TimeSlotAssignedAndAcceptedRecyclerViewAdapter(timeSlotList.toMutableList().sortedByDescending { Timestamp(Date(it.dateTime)) }, selector)
                    }
                }
            }
        }else { //timeslot accettati -> timeslot miei che io ho acettato di fare ad altri
            nAssTxt?.visibility = View.GONE
            vm.setAdvsListenerByCurrentUser()
            vm.loggedUserTimeSlotList.observe(viewLifecycleOwner) { timeSlotList ->
                val timeSlotsAccepted = timeSlotList.filter { ts -> ts.state == "ACCEPTED" }
                if (timeSlotsAccepted.isEmpty()) {
                    recyclerView?.visibility = View.GONE
                    nAccText?.visibility = View.VISIBLE
                } else {
                    nAccText?.visibility = View.GONE
                    recyclerView?.visibility = View.VISIBLE
                }

                if (recyclerView is RecyclerView) {
                    with(recyclerView) {
                        layoutManager = when {
                            columnCount <= 1 -> LinearLayoutManager(context)
                            else -> GridLayoutManager(context, columnCount)
                        }
                        adapter =
                            TimeSlotAssignedAndAcceptedRecyclerViewAdapter(timeSlotsAccepted.toMutableList().sortedByDescending { Timestamp(Date(it.dateTime))}, selector)
                    }
                }
            }
        }
    }


    private fun onBackPressed(){
        val runnable = Runnable {
            // useful to call interaction with viewModel
            vm.removeAdvsListenerByCurrentUser()
            vm.removeMyAssignedTimeSlotListListener()
        }
        // Perform persistence changes after 250 millis
        Handler().postDelayed(runnable, 250)
    }
}