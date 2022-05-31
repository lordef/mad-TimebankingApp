package it.polito.mad.lab02.fragments.myadvertisements

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import it.polito.mad.lab02.R
import it.polito.mad.lab02.fragments.communication.MyChatRecyclerViewAdapter
import it.polito.mad.lab02.fragments.profile.RatingRecyclerViewAdapter
import it.polito.mad.lab02.models.TimeSlot
import it.polito.mad.lab02.viewmodels.MainActivityViewModel

/**
 * A fragment representing a list of Items.
 */
class TimeSlotsOfInterestFragment : Fragment(R.layout.fragment_time_slots_of_interest) {

    private val vm by activityViewModels<MainActivityViewModel>()

    private var columnCount = 1

    private var selector = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.list)



        val textView = view.findViewById<TextView>(R.id.text_pub_advertisements)

        vm.setRequesterChatsListener()

        vm.requesterChatList.observe(viewLifecycleOwner) { chatList ->
            if (selector == 0) {
                (activity as AppCompatActivity?)?.supportActionBar?.title = "What are you interested in"
                if (chatList.isEmpty()) {
                    recyclerView.visibility = View.GONE
                    textView.visibility = View.VISIBLE
                } else {
                    textView.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                }

                if (recyclerView is RecyclerView) {
                    with(recyclerView) {
                        layoutManager = when {
                            columnCount <= 1 -> LinearLayoutManager(context)
                            else -> GridLayoutManager(context, columnCount)
                        }
                        adapter = TimeSlotsOfInterestRecyclerViewAdapter(chatList.map{it.timeSlot})
                    }
                }
            }
        }
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                view.findNavController().navigateUp()
                onBackPressed()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)
    }

    private fun onBackPressed(){
        val runnable = Runnable {
            // useful to call interaction with viewModel
            vm.removeRequesterChatsListener()
        }
        // Perform persistence changes after 250 millis
        Handler().postDelayed(runnable, 250)
    }

}