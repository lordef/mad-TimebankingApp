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
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import it.polito.mad.lab02.R
import it.polito.mad.lab02.viewmodels.MainActivityViewModel


class TimeSlotAssignedAndAcceptedFragment : Fragment(R.layout.fragment_time_slot_assigned_and_accepted_list) {

    private var columnCount = 1

    private val vm by activityViewModels<MainActivityViewModel>()

    private var selector = 1

//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        val view = inflater.inflate(
//            R.layout.fragment_time_slot_assigned_and_accepted_list,
//            container,
//            false
//        )
//
//
//        return view
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val nAccText = view.findViewById<TextView>(R.id.text_no_adv_accepted)
        val nAssTxt = view.findViewById<TextView>(R.id.text_no_adv_assigned)
        val recyclerView = view.findViewById<RecyclerView>(R.id.aaAdvlist)

        if (selector == 0) {
            nAccText.visibility = View.GONE
            vm.setMyAssignedTimeSlotListListener()

            vm.myAssignedTimeSlotList.observe(viewLifecycleOwner) { timeSlotList ->
                if (timeSlotList.isEmpty()) {
                    recyclerView.visibility = View.GONE
                    nAssTxt.visibility = View.VISIBLE
                } else {
                    nAssTxt.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                }

                if (view is RecyclerView) {
                    with(view) {
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
            nAssTxt.visibility = View.GONE
            vm.loggedUserTimeSlotList.observe(viewLifecycleOwner) { timeSlotList ->

                if (timeSlotList.isEmpty()) {
                    recyclerView.visibility = View.GONE
                    nAccText.visibility = View.VISIBLE
                } else {
                    nAccText.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                }
                timeSlotList.filter { ts -> ts.state == "ACCEPTED" }

                if (view is RecyclerView) {
                    with(view) {
                        layoutManager = when {
                            columnCount <= 1 -> LinearLayoutManager(context)
                            else -> GridLayoutManager(context, columnCount)
                        }
                        adapter =
                            TimeSlotAssignedAndAcceptedRecyclerViewAdapter(timeSlotList.toMutableList())
                    }
                }
            }
        }
    }


}