package it.polito.mad.lab02.fragments.listofskills

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.MenuItem
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import it.polito.mad.lab02.R
import it.polito.mad.lab02.viewmodels.PublicTimeSlotListViewModel

/**
 * A fragment representing a list of Items.
 */
class PublicTimeSlotFragment : Fragment(R.layout.fragment_public_time_slot_list) {

    private var columnCount = 1

    private val vm by viewModels<PublicTimeSlotListViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.public_time_slot_list)

        val skill = arguments?.getString("skill")
        if(skill != null){
            Log.d("MYTAG", "Passed skill: ${skill}")
            vm.getTimeSlotList(skill).observe(viewLifecycleOwner){timeSlotList ->

                Log.d("MYTAG", "Doc ref: ${timeSlotList}")
                if (recyclerView is RecyclerView) {
                    with(recyclerView) {
                        layoutManager = when {
                            columnCount <= 1 -> LinearLayoutManager(context)
                            else -> GridLayoutManager(context, columnCount)
                        }
                        adapter = PublicTimeSlotRecyclerViewAdapter(timeSlotList)
                    }
                }
            }
        }

        val callback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                view.findNavController().navigateUp()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {

            android.R.id.home -> {
                findNavController().navigateUp()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }
}