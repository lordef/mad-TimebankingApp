package it.polito.mad.lab02.fragments.myadvertisements

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import it.polito.mad.lab02.R
import it.polito.mad.lab02.viewmodels.MainActivityViewModel


class TimeSlotAssignedAndAcceptedFragment : Fragment() {

    private var columnCount = 1

    private val vm by activityViewModels<MainActivityViewModel>()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.fragment_time_slot_assigned_and_accepted_list,
            container,
            false
        )


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm.setAdvsListenerByCurrentUser()
        vm.loggedUserTimeSlotList.observe(viewLifecycleOwner) { timeSlotList ->
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