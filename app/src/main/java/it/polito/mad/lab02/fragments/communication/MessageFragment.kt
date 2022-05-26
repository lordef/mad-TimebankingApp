package it.polito.mad.lab02.fragments.communication

import android.os.Bundle
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
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.squareup.okhttp.MediaType
import it.polito.mad.lab02.R
import it.polito.mad.lab02.models.Message
import it.polito.mad.lab02.models.Profile
import it.polito.mad.lab02.viewmodels.MainActivityViewModel
import java.util.*

class MessageFragment : Fragment(R.layout.message_chat_list) {

    private var columnCount = 1

    private val vm by activityViewModels<MainActivityViewModel>()

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)?.supportActionBar?.title = "Chat"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_gchat)

        val chatId = arguments?.getString("id")
        if (chatId != null) {
            vm.setMessagesListener(chatId)

            val textView = view.findViewById<TextView>(R.id.text_no_messages)

            vm.messageList.observe(viewLifecycleOwner) { messageList ->
                if (messageList.isEmpty()) {
                    recyclerView.visibility = View.GONE
                    textView.visibility = View.VISIBLE
                } else {
                    textView.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
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

            val composedMessage = view.findViewById<TextView>(R.id.edit_gchat_message)
            val send = view.findViewById<Button>(R.id.button_gchat_send)
            send.setOnClickListener{
                if(vm.sendMessage(chatId, composedMessage.text.toString())){
                    composedMessage.text = ""
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