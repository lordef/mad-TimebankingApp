package it.polito.mad.lab02.fragments.myadvertisements

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import it.polito.mad.lab02.R
import it.polito.mad.lab02.viewmodels.MainActivityViewModel


class TimeSlotAssignedAndAcceptedFragment : Fragment(R.layout.fragment_time_slot_assigned_and_accepted_list) {

    private var columnCount = 1

    private val vm by activityViewModels<MainActivityViewModel>()

    private var selector = 1


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity?)?.supportActionBar?.title = "Timeslots you accepted"
        showAssignedOrAccepted()

        val acceptedButton = view.findViewById<Button>(R.id.requesterButton)
        val assignedButton = view.findViewById<Button>(R.id.publisherButton)


        acceptedButton.setOnClickListener{
            (activity as AppCompatActivity?)?.supportActionBar?.title = "Timeslots you accepted"
            selector = 0
            showAssignedOrAccepted()
        }
        assignedButton.setOnClickListener{
            (activity as AppCompatActivity?)?.supportActionBar?.title = "Timeslots assigned to you"
            selector = 1
            showAssignedOrAccepted()
        }


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
                            TimeSlotAssignedAndAcceptedRecyclerViewAdapter(timeSlotList.toMutableList(), selector)
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
                            TimeSlotAssignedAndAcceptedRecyclerViewAdapter(timeSlotsAccepted.toMutableList(), selector)
                    }
                }
            }
        }
    }

// TODO: ci va il remove
}