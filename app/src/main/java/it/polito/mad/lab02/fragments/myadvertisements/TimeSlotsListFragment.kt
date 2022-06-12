package it.polito.mad.lab02.fragments.myadvertisements

import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.annotation.MenuRes
import androidx.appcompat.app.AlertDialog
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
class TimeSlotsListFragment : Fragment(R.layout.fragment_time_slot_list) {

    private var columnCount = 1

    private val vm by activityViewModels<MainActivityViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.list)

        val fragContext = requireContext()


        //Creating listener from this fragment
        vm.setAdvsListenerByCurrentUser()

        //Accessing user logged avds
        vm.loggedUserTimeSlotList.observe(viewLifecycleOwner) { timeSlotList ->
            if (recyclerView is RecyclerView) {
                with(recyclerView) {
                    layoutManager = when {
                        columnCount <= 1 -> LinearLayoutManager(fragContext)
                        else -> GridLayoutManager(fragContext, columnCount)
                    }
                    adapter = TimeSlotsListRecyclerViewAdapter(fragContext, timeSlotList.toMutableList()) {
                        vm.deleteTimeSlot(it)
                    }
                }
            }

            val fab = view.findViewById<FloatingActionButton>(R.id.fab2)
            fab.setOnClickListener { view ->
                if (vm.profile.value!!.skills.isNotEmpty()) {
                    view.findNavController()
                        .navigate(R.id.action_nav_advertisement_to_timeSlotEditFragment)
                } else {
                    val builder: AlertDialog.Builder =
                        androidx.appcompat.app.AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)

                    builder.setCancelable(true)
                    builder.setTitle("Wait a minute!")
                    builder.setMessage("You can not create a new timeslot without define at least one skill in your profile. Click on the button below to add it!")

                    builder.setNeutralButton("Cancel",
                        DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.cancel() })

                    builder.setPositiveButton("Add skill",
                        DialogInterface.OnClickListener { dialogInterface, i ->
                            view.findNavController()
                                .navigate(R.id.action_nav_advertisement_to_editProfileFragment)
                        })
                    builder.show()
                }
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

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val ret = view.findNavController().navigateUp()
                if (ret){
                    onBackPressed()
                }
                else {
                    activity?.finish()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)
    }

    private fun showSkillConstraintMenu(
        v: View,
        @MenuRes menuRes: Int,
        adapter: PublicTimeSlotRecyclerViewAdapter
    ) {
        val popup = PopupMenu(requireContext(), v)
        popup.menuInflater.inflate(menuRes, popup.menu)
    }

    private fun onBackPressed(){
        val runnable = Runnable {
            // useful to call interaction with viewModel
            vm.removeAdvsListenerByCurrentUser()
        }
        // Perform persistence changes after 250 millis
        Handler().postDelayed(runnable, 250)
    }
}

