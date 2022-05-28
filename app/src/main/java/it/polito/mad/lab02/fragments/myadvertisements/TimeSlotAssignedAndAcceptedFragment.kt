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
import androidx.fragment.app.activityViewModels
import it.polito.mad.lab02.R
import it.polito.mad.lab02.viewmodels.MainActivityViewModel


class TimeSlotAssignedAndAcceptedFragment : Fragment(R.layout.fragment_time_slot_assigned_and_accepted_list) {

    private var columnCount = 1

    private val vm by activityViewModels<MainActivityViewModel>()

    private var selector = 0


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showAssignedOrAccepted()
        val acceptedButton = view.findViewById<Button>(R.id.requesterButton)
        val assignedButton = view.findViewById<Button>(R.id.publisherButton)

        acceptedButton.setOnClickListener{
            selector = 0
            showAssignedOrAccepted()
        }
        assignedButton.setOnClickListener{
            selector = 1
            showAssignedOrAccepted()
        }


    }

    fun showAssignedOrAccepted(){
        val nAccText = view?.findViewById<TextView>(R.id.text_no_adv_accepted)
        val nAssTxt = view?.findViewById<TextView>(R.id.text_no_adv_assigned)
        val recyclerView = view?.findViewById<RecyclerView>(R.id.aaAdvlist)
        if (selector == 0) {
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
                            TimeSlotAssignedAndAcceptedRecyclerViewAdapter(timeSlotList.toMutableList())
                    }
                }
            }
        }else {
            nAssTxt?.visibility = View.GONE
            vm.setAdvsListenerByCurrentUser()
            vm.loggedUserTimeSlotList.observe(viewLifecycleOwner) { timeSlotList ->
                Log.d("mytaggg", timeSlotList.toString())
                val timeSlotsAccepted = timeSlotList.filter { ts -> ts.state == "ACCEPTED" }
                Log.d("mytaggg", "accepted "+timeSlotsAccepted.toString())
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
                            TimeSlotAssignedAndAcceptedRecyclerViewAdapter(timeSlotsAccepted.toMutableList())
                    }
                }
            }
        }
    }

// TODO: ci va il remove
}