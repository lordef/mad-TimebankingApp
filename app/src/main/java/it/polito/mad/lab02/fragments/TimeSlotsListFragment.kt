package it.polito.mad.lab02.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import it.polito.mad.lab02.R
import it.polito.mad.lab02.viewmodels.TimeSlotDetailsViewModel
import it.polito.mad.lab02.viewmodels.TimeSlotListViewModel

/**
 * A fragment representing a list of Items.
 */
class TimeSlotsListFragment : Fragment() {

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_time_slots_list_list, container, false)

        val fab = view.findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            it.findNavController()
                .navigate(R.id.action_nav_advertisement_to_timeSlotEditFragment)
        }

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter = TimeSlotsListRecyclerViewAdapter(vm.getTimeSlotList().value!!)
            }
        }
        return view
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            TimeSlotsListFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}