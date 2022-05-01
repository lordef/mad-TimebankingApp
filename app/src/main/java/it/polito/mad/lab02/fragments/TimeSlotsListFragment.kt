package it.polito.mad.lab02.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import it.polito.mad.lab02.R
import it.polito.mad.lab02.viewmodels.TimeSlotListViewModel

/**
 * A fragment representing a list of Items.
 */
class TimeSlotsListFragment : Fragment(R.layout.fragment_time_slot_list) {

    private var columnCount = 1

    private val vm by viewModels<TimeSlotListViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*
        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
        */
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.list)
        val timeSlotList = vm.getTimeSlotList().value
        if (recyclerView is RecyclerView) {
            with(recyclerView) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                //TODO: add observer as in details fragment
                adapter = TimeSlotsListRecyclerViewAdapter(timeSlotList!!)
            }
        }
        val fab = view.findViewById<FloatingActionButton>(R.id.fab2)
        fab.setOnClickListener { view ->
            view.findNavController()
                .navigate(R.id.action_nav_advertisement_to_timeSlotEditFragment)
        }
        if (timeSlotList!!.size == 0) {
            recyclerView.visibility = View.GONE
        } else {
            val textView = view.findViewById<TextView>(R.id.text_advertisements)
            textView.visibility = View.GONE
        }

        val callback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)
    }
}