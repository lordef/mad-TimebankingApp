package it.polito.mad.lab02.fragments.communication

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import it.polito.mad.lab02.R
import it.polito.mad.lab02.fragments.myadvertisements.TimeSlotsListRecyclerViewAdapter
import it.polito.mad.lab02.viewmodels.MainActivityViewModel
import kotlin.system.exitProcess


class ChatFragment : Fragment(R.layout.fragment_chat_list) {

    private var columnCount = 1

    //says if I'm seeing the chats as publisher or requester
    private var selector = 0

    private val vm by activityViewModels<MainActivityViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.chat_list)

        vm.setChatsListener()
        val textView = view.findViewById<TextView>(R.id.text_no_chats)

        vm.publisherChatList.observe(viewLifecycleOwner) { chatList ->
            if (selector == 0) {
                (activity as AppCompatActivity?)?.supportActionBar?.title = "Chats as publisher"
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
                        adapter = MyChatRecyclerViewAdapter(chatList)
                    }
                }
            }
        }

        vm.requesterChatList.observe(viewLifecycleOwner) { chatList ->

            if (selector == 1) {
                (activity as AppCompatActivity?)?.supportActionBar?.title = "Chats as requester"
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
                        adapter = MyChatRecyclerViewAdapter(chatList)
                    }
                }
            }
        }

        val publisherButton = view.findViewById<Button>(R.id.publisherButton)
        val requesterButton = view.findViewById<Button>(R.id.requesterButton)

        publisherButton.setOnClickListener{
            selector = 0
            (activity as AppCompatActivity?)?.supportActionBar?.title = "Chats as publisher"
            if (recyclerView is RecyclerView) {
                with(recyclerView) {
                    layoutManager = when {
                        columnCount <= 1 -> LinearLayoutManager(context)
                        else -> GridLayoutManager(context, columnCount)
                    }
                    if(vm.publisherChatList.value != null) {
                        adapter = MyChatRecyclerViewAdapter(vm.publisherChatList.value!!)
                        if (vm.publisherChatList.value!!.isEmpty()) {
                            recyclerView.visibility = View.GONE
                            textView.visibility = View.VISIBLE
                        } else {
                            textView.visibility = View.GONE
                            recyclerView.visibility = View.VISIBLE
                        }
                    }
                    else{
                        adapter = MyChatRecyclerViewAdapter(emptyList())
                        textView.visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE
                    }
                }
            }
        }

        requesterButton.setOnClickListener{
            selector = 1
            (activity as AppCompatActivity?)?.supportActionBar?.title = "Chats as requester"
            if (recyclerView is RecyclerView) {
                with(recyclerView) {
                    layoutManager = when {
                        columnCount <= 1 -> LinearLayoutManager(context)
                        else -> GridLayoutManager(context, columnCount)
                    }
                    if(vm.requesterChatList.value != null) {
                        adapter = MyChatRecyclerViewAdapter(vm.requesterChatList.value!!)
                        if (vm.requesterChatList.value!!.isEmpty()) {
                            recyclerView.visibility = View.GONE
                            textView.visibility = View.VISIBLE
                        } else {
                            textView.visibility = View.GONE
                            recyclerView.visibility = View.VISIBLE
                        }
                    }
                    else{
                        adapter = MyChatRecyclerViewAdapter(emptyList())
                        textView.visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE
                    }
                }
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