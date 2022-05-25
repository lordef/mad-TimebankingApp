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

        vm.publisherChatList.observe(viewLifecycleOwner) { chatList ->
            if (selector == 0) {
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

            Log.d("MYTAG", "Hello ${vm.publisherChatList.value!!}")
            selector = 0
            if (recyclerView is RecyclerView) {
                with(recyclerView) {
                    layoutManager = when {
                        columnCount <= 1 -> LinearLayoutManager(context)
                        else -> GridLayoutManager(context, columnCount)
                    }
                    adapter = MyChatRecyclerViewAdapter(vm.publisherChatList.value!!)
                }
            }
        }

        requesterButton.setOnClickListener{

            Log.d("MYTAG", "Hello1 ${vm.requesterChatList.value!!}")
            selector = 1
            if (recyclerView is RecyclerView) {
                with(recyclerView) {
                    layoutManager = when {
                        columnCount <= 1 -> LinearLayoutManager(context)
                        else -> GridLayoutManager(context, columnCount)
                    }
                    adapter = MyChatRecyclerViewAdapter(vm.requesterChatList.value!!)
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