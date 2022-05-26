package it.polito.mad.lab02.fragments.myadvertisements

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.annotation.MenuRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import it.polito.mad.lab02.R
import it.polito.mad.lab02.fragments.listofskills.PublicTimeSlotRecyclerViewAdapter
import it.polito.mad.lab02.viewmodels.MainActivityViewModel
import kotlin.system.exitProcess

/**
 * A fragment representing a list of Items.
 */
class TimeSlotsOfInterestListFragment : Fragment(R.layout.fragment_time_slots_of_interest_list) {

    private var columnCount = 1

    private val vm by activityViewModels<MainActivityViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        val recyclerView = view.findViewById<RecyclerView>(R.id.public_time_slot_list)
        val recyclerView = view.findViewById<RecyclerView>(R.id.list)

        (activity as AppCompatActivity?)?.supportActionBar?.title = "Interests"

        vm.timeslotList.observe(viewLifecycleOwner) { timeSlotList ->

            val myAdapter = PublicTimeSlotRecyclerViewAdapter(timeSlotList)
            if (recyclerView is RecyclerView) {
                with(recyclerView) {
                    layoutManager = when {
                        columnCount <= 1 -> LinearLayoutManager(context)
                        else -> GridLayoutManager(context, columnCount)
                    }
                    adapter = myAdapter
                }
            }

            val textView = view.findViewById<TextView>(R.id.text_pub_advertisements)
            if (timeSlotList.isEmpty()) {
                recyclerView.visibility = View.GONE
                textView.visibility = View.VISIBLE
            } else {
                textView.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }

        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                view.findNavController().navigateUp()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)

    }

}