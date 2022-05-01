package it.polito.mad.lab02.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import it.polito.mad.lab02.R
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

        val listView = inflater.inflate(R.layout.fragment_time_slot_list, container, false)
        val recyclerView = listView.findViewById<RecyclerView>(R.id.list)
        if (recyclerView is RecyclerView) {
            with(recyclerView) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter = TimeSlotsListRecyclerViewAdapter(vm.getTimeSlotList().value!!)
            }
        }
        if(vm.getTimeSlotList().value!!.size == 0){
            recyclerView.visibility = View.GONE
        }
        else{
            val textView = listView.findViewById<TextView>(R.id.text_advertisements)
            textView.visibility = View.GONE
        }

        return listView

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}