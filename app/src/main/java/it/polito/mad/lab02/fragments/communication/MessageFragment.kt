package it.polito.mad.lab02.fragments.communication

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.MenuItem
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import it.polito.mad.lab02.R
import it.polito.mad.lab02.models.Message
import it.polito.mad.lab02.models.Profile
import it.polito.mad.lab02.viewmodels.MainActivityViewModel
import java.util.*

class MessageFragment : Fragment(R.layout.message_chat_list) {

    private var columnCount = 1

    private val vm by activityViewModels<MainActivityViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_gchat)

        vm.setChatsListener()

        val myAdapter = MessageRecyclerViewAdapter(
            listOf(
                Message(
                    "Hello",
                    Timestamp(Calendar.getInstance().time),
                    Profile(
                        "android.resource://it.polito.mad.lab02/drawable/profile_image",
                        "Grinton",
                        "Grinton",
                        "",
                        "",
                        emptyList(),
                        "",
                        FirebaseAuth.getInstance().currentUser!!.uid
                    )
                ),
                Message(
                    "Hello",
                    Timestamp(Calendar.getInstance().time),
                    Profile(
                        "android.resource://it.polito.mad.lab02/drawable/profile_image",
                        "Toni",
                        "Toni",
                        "", "",
                        emptyList(),
                        "",
                        "111"
                    )
                ),
                Message(
                    "Hello",
                    Timestamp(Calendar.getInstance().time),
                    Profile(
                        "android.resource://it.polito.mad.lab02/drawable/profile_image",
                        "Grinton",
                        "Grinton",
                        "",
                        "",
                        emptyList(),
                        "",
                        FirebaseAuth.getInstance().currentUser!!.uid
                    )
                )
            )
        )
        if (recyclerView is RecyclerView) {
            with(recyclerView) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter = myAdapter
            }
        }


//        val skillRefToString = arguments?.getString("skill")

//        if (skillRefToString != null) {
//            vm.setPublicAdvsListenerBySkill(skillRefToString)
//
//            (activity as AppCompatActivity?)?.supportActionBar?.title =
//                "Chat"
//
//            vm.timeslotList.observe(viewLifecycleOwner) { timeSlotList ->
//
//                val allFilterButton = view.findViewById<Button>(R.id.sortButton)
//                allFilterButton.setOnClickListener {
//                    vm.addFilter {
//                        true
//                    }
//                }
//
//                val myAdapter = PublicTimeSlotRecyclerViewAdapter(timeSlotList)
//                if (recyclerView is RecyclerView) {
//                    with(recyclerView) {
//                        layoutManager = when {
//                            columnCount <= 1 -> LinearLayoutManager(context)
//                            else -> GridLayoutManager(context, columnCount)
//                        }
//                        adapter = myAdapter
//                    }
//                }
//
//
//
//                val textView = view.findViewById<TextView>(R.id.text_pub_advertisements)
//                if (timeSlotList.isEmpty()) {
//                    recyclerView.visibility = View.GONE
//                    textView.visibility = View.VISIBLE
//                } else {
//                    textView.visibility = View.GONE
//                    recyclerView.visibility = View.VISIBLE
//                }
//            }
//        }

        val callback = object : OnBackPressedCallback(true) {
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