package it.polito.mad.lab02.fragments.communication

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import it.polito.mad.lab02.R
import it.polito.mad.lab02.models.TimeSlot
import it.polito.mad.lab02.viewmodels.MainActivityViewModel


class MessageFragment : Fragment(R.layout.message_chat_list) {

    private var columnCount = 1
    private var chatIdReal = ""

    private val vm by activityViewModels<MainActivityViewModel>()

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)?.supportActionBar?.title = "Chat"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_gchat)
        val timeslotCard = view.findViewById<CardView>(R.id.gchat_timeslot_card)
        val timeslotCardText = view.findViewById<TextView>(R.id.timeslot_card_text)
        val requestButton = view.findViewById<Button>(R.id.gchat_request_button)
        val refuseButton = view.findViewById<Button>(R.id.gchat_refuse_button)
        val textView = view.findViewById<TextView>(R.id.text_no_messages)

        val chatId = arguments?.getString("id")
        val timeSlot = arguments?.getString("timeslot")

        if (timeSlot != null) {
            val ts = Gson().fromJson(timeSlot, TimeSlot::class.java)
            vm.setTimeSlotListener(ts)

            timeslotCardText.text = ts.title
            timeslotCard.setOnClickListener {
                findNavController()
                    .navigate(
                        R.id.action_nav_single_message_to_publicTimeSlotDetailsFragment,
                        arguments
                    )
            }

            vm.isChatListenerSet.observe(viewLifecycleOwner) {
                if (chatId != null || chatIdReal != "") {
                    if (chatId != null) {
                        chatIdReal = chatId
                    }
                    vm.setMessagesListener(chatIdReal)

                    vm.messageList.observe(viewLifecycleOwner) { messageList ->
                        if (messageList.isEmpty()) {
                            requestButton.visibility = View.GONE
                            refuseButton.visibility = View.GONE
                            recyclerView.visibility = View.GONE
                            textView.visibility = View.VISIBLE
                        } else {
                            textView.visibility = View.GONE
                            recyclerView.visibility = View.VISIBLE

                            vm.timeSlot.observe(viewLifecycleOwner) { timeSlotObs ->
                                if (timeSlotObs != null) {
                                    requestButton.visibility = View.VISIBLE
                                    refuseButton.visibility = View.VISIBLE
                                    if (timeSlotObs.user == FirebaseAuth.getInstance().currentUser?.uid.toString()) {
                                        //case in which the timeslot is requested by the other user
                                        if (timeSlotObs.pendingRequests.contains(messageList[0].user.uid)) {
                                            if (timeSlotObs.state == "AVAILABLE") {
                                                requestButton.text = "Accept offer"
                                                requestButton.setOnClickListener {
                                                    if (!(vm.setTimeSlotAssignee(
                                                            messageList[0].user.uid,
                                                            timeSlotObs,
                                                            messageList[0].user
                                                        ))
                                                    ) {
                                                        vm.removeTimeSlotRequest(
                                                            messageList[0].user.uid,
                                                            timeSlotObs
                                                        )
                                                        vm.sendMessage(
                                                            chatIdReal,
                                                            "The user has not enough money",
                                                            messageList[0].user.uid,
                                                            true
                                                        )
//                                                        Toast.makeText(
//                                                            this.context,
//                                                            "The user has not enough money",
//                                                            Toast.LENGTH_SHORT
//                                                        ).show()
                                                    }
                                                }
                                                refuseButton.text = "Reject offer"
                                                refuseButton.setOnClickListener {
                                                    vm.removeTimeSlotRequest(
                                                        messageList[0].user.uid,
                                                        timeSlotObs
                                                    )
                                                }
                                            }
                                            if (timeSlotObs.state == "ACCEPTED" && timeSlotObs.assignee == messageList[0].user.uid) {
                                                requestButton.text = "Accepted"
                                                refuseButton.visibility = View.GONE
                                            } else if (timeSlotObs.state == "ACCEPTED") {
                                                requestButton.text = "Accepted other user"
                                                refuseButton.visibility = View.GONE
                                            }
                                        } else {
                                            requestButton.visibility = View.GONE
                                            refuseButton.visibility = View.GONE
                                        }
                                    } else {
                                        if (!timeSlotObs.pendingRequests.contains(messageList[0].user.uid)) {
                                            if (timeSlotObs.state == "AVAILABLE") {
                                                refuseButton.visibility = View.GONE
                                                requestButton.text = "Send offer"
                                                requestButton.setOnClickListener {
                                                    vm.setTimeSlotRequest(
                                                        FirebaseAuth.getInstance().currentUser?.uid.toString(),
                                                        timeSlotObs
                                                    )
                                                }
                                            } else {
                                                requestButton.visibility = View.GONE
                                                refuseButton.text = "Not available"
                                            }
                                        } else {
                                            refuseButton.visibility = View.GONE
                                            if (timeSlotObs.state == "AVAILABLE") {
                                                requestButton.text = "Requested"
                                            }
                                            if (timeSlotObs.state == "ACCEPTED") {
                                                if (timeSlotObs.assignee == FirebaseAuth.getInstance().currentUser?.uid) {
                                                    requestButton.text = "Accepted"
                                                } else {
                                                    requestButton.visibility = View.GONE
                                                    refuseButton.visibility = View.VISIBLE
                                                    refuseButton.text = "Not available"
                                                }
                                            }
                                        }
                                    }
                                }

                                val myAdapter = MessageRecyclerViewAdapter(messageList)

                                if (recyclerView is RecyclerView) {
                                    with(recyclerView) {
                                        layoutManager = when {
                                            columnCount <= 1 -> LinearLayoutManager(context)
                                            else -> GridLayoutManager(context, columnCount)
                                        }
                                        adapter = myAdapter
                                        recyclerView.scrollToPosition((adapter as MessageRecyclerViewAdapter).itemCount - 1)
                                    }
                                }
                            }

                            val composedMessage =
                                view.findViewById<TextView>(R.id.edit_gchat_message)
                            val send = view.findViewById<Button>(R.id.button_gchat_send)
                            send.setOnClickListener {
                                if (ts.userProfile.uid == FirebaseAuth.getInstance().currentUser?.uid) {
                                    if (vm.sendMessage(
                                            chatIdReal,
                                            composedMessage.text.toString(),
                                            messageList[0].user.uid,
                                            false
                                        )
                                    ) {
                                        composedMessage.text = ""
                                        (activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?)?.hideSoftInputFromWindow(
                                            composedMessage.windowToken,
                                            InputMethodManager.RESULT_UNCHANGED_SHOWN
                                        )
                                    }
                                } else {
                                    if (vm.sendMessage(
                                            chatIdReal,
                                            composedMessage.text.toString(),
                                            ts.user,
                                            false
                                        )
                                    ) {
                                        composedMessage.text = ""
                                        (activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?)?.hideSoftInputFromWindow(
                                            composedMessage.windowToken,
                                            InputMethodManager.RESULT_UNCHANGED_SHOWN
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else { //new chat
                    requestButton.visibility = View.GONE
                    refuseButton.visibility = View.GONE
                    recyclerView.visibility = View.GONE
                    textView.visibility = View.VISIBLE
                    vm.timeSlot.observe(viewLifecycleOwner) { timeSlotObs ->
                        val composedMessage = view.findViewById<TextView>(R.id.edit_gchat_message)
                        val send = view.findViewById<Button>(R.id.button_gchat_send)
                        send.setOnClickListener {
                            if (timeSlotObs != null) {
                                vm.removeMessagesListener()
                                val id = vm.createChat(timeSlotObs)
                                chatIdReal = id
                                if (vm.sendMessage(
                                        id,
                                        composedMessage.text.toString(),
                                        timeSlotObs.user,
                                        false
                                    )
                                ) {
                                    composedMessage.text = ""
                                    (activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?)?.hideSoftInputFromWindow(
                                        composedMessage.windowToken,
                                        InputMethodManager.RESULT_UNCHANGED_SHOWN
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBackPressed()
                view.findNavController().navigateUp()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                findNavController().navigateUp()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }

    }

    private fun onBackPressed(){
        val runnable = Runnable {
            // useful to call interaction with viewModel
            vm.removeMessagesListener()
            vm.removeTimeSlotListener()
        }
        // Perform persistence changes after 250 millis
        Handler().postDelayed(runnable, 250)
    }

}