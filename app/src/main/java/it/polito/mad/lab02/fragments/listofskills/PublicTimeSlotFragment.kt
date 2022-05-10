package it.polito.mad.lab02.fragments.listofskills

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import it.polito.mad.lab02.R
import it.polito.mad.lab02.fragments.myadvertisements.TimeSlotsListRecyclerViewAdapter
import it.polito.mad.lab02.viewmodels.TimeSlotListViewModel
import kotlin.system.exitProcess

/**
 * A fragment representing a list of Items.
 */
class PublicTimeSlotFragment : Fragment(R.layout.fragment_public_time_slot_list) {

    private var columnCount = 1

    private val vm by viewModels<TimeSlotListViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.list)
        vm.getTimeSlotList().observe(viewLifecycleOwner){timeSlotList ->
            if (recyclerView is RecyclerView) {
                with(recyclerView) {
                    layoutManager = when {
                        columnCount <= 1 -> LinearLayoutManager(context)
                        else -> GridLayoutManager(context, columnCount)
                    }
                    adapter = TimeSlotsListRecyclerViewAdapter(timeSlotList)
                }
            }
            val fab = view.findViewById<FloatingActionButton>(R.id.fab2)
            fab.setOnClickListener { view ->
                view.findNavController()
                    .navigate(R.id.action_nav_advertisement_to_timeSlotEditFragment)
            }
            val textView = view.findViewById<TextView>(R.id.text_advertisements)
            if (timeSlotList.isEmpty()) {
                recyclerView.visibility = View.GONE
                textView.visibility = View.VISIBLE
            } else {
                textView.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
        }

        val callback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                if(!view.findNavController().navigateUp()){
                    exitProcess(1)
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)
    }
}