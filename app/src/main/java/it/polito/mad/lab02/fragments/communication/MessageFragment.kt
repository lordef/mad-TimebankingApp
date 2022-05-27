package it.polito.mad.lab02.fragments.communication

import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.squareup.okhttp.MediaType
import it.polito.mad.lab02.R
import it.polito.mad.lab02.models.Message
import it.polito.mad.lab02.models.Profile
import it.polito.mad.lab02.models.TimeSlot
import it.polito.mad.lab02.viewmodels.MainActivityViewModel
import org.w3c.dom.Text
import java.util.*

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
                                                    vm.setTimeSlotState("ACCEPTED", timeSlotObs)
                                                    vm.setTimeSlotAssignee(messageList[0].user.uid, timeSlotObs)
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
                                            }
                                            else if(timeSlotObs.state == "ACCEPTED"){
                                                requestButton.text = "Accepted other user"
                                                refuseButton.visibility = View.GONE
                                            }
                                        } else {
                                            requestButton.visibility = View.GONE
                                            refuseButton.visibility = View.GONE
                                        }
                                    } else {
                                        if (!timeSlotObs.pendingRequests.contains(messageList[0].user.uid)) {
                                            if(timeSlotObs.state == "AVAILABLE") {
                                                refuseButton.visibility = View.GONE
                                                requestButton.text = "Send offer"
                                                requestButton.setOnClickListener {
                                                    vm.setTimeSlotRequest(
                                                        FirebaseAuth.getInstance().currentUser?.uid.toString(),
                                                        timeSlotObs
                                                    )
                                                }
                                            }
                                            else{
                                                requestButton.visibility = View.GONE
                                                refuseButton.text = "Not available"
                                            }
                                        } else {
                                            refuseButton.visibility = View.GONE
                                            if (timeSlotObs.state == "AVAILABLE") {
                                                requestButton.text = "Requested"
                                            }
                                            if (timeSlotObs.state == "ACCEPTED") {
                                                if(timeSlotObs.assignee == FirebaseAuth.getInstance().currentUser?.uid){
                                                    requestButton.text = "Accepted"
                                                }
                                                else{
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
                                    }
                                }
                            }
                        }
                    }

                    val composedMessage = view.findViewById<TextView>(R.id.edit_gchat_message)
                    val send = view.findViewById<Button>(R.id.button_gchat_send)
                    send.setOnClickListener {
                        if (vm.sendMessage(chatIdReal, composedMessage.text.toString())) {
                            composedMessage.text = ""
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
                                vm.clearChat()
                                val id = vm.createChat(timeSlotObs)
                                chatIdReal = id
                                Log.d("MYTAG", chatIdReal)
                                if (vm.sendMessage(id, composedMessage.text.toString())) {
                                    composedMessage.text = ""
                                }
                            }
                        }
                    }
                }
            }
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                vm.clearChat()
                view.findNavController().navigateUp()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            android.R.id.home -> {
                vm.clearChat()
                findNavController().navigateUp()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }

    }

}