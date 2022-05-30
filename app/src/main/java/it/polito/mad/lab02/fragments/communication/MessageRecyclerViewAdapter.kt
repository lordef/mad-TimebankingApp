package it.polito.mad.lab02.fragments.communication

import android.R
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.Navigator
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.get
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import it.polito.mad.lab02.databinding.ItemChatMeBinding
import it.polito.mad.lab02.models.Message
import java.util.*

import it.polito.mad.lab02.databinding.ItemChatOtherBinding
import it.polito.mad.lab02.databinding.ItemChatSystemBinding
import it.polito.mad.lab02.viewmodels.MainActivityViewModel
import java.text.SimpleDateFormat


class MessageRecyclerViewAdapter(messageList: List<Message>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val mMessageList: List<Message>

    companion object {
        private const val VIEW_TYPE_MESSAGE_SENT = 1
        private const val VIEW_TYPE_MESSAGE_RECEIVED = 2
        private const val VIEW_TYPE_MESSAGE_SYSTEM = 3
    }

    init {
        mMessageList = messageList
    }


    override fun getItemCount(): Int {
        return mMessageList.size
    }

    // Determines the appropriate ViewType according to the sender of the message.
    override fun getItemViewType(position: Int): Int {
        val message: Message = mMessageList[position]
        return if (message.system) {
            VIEW_TYPE_MESSAGE_SYSTEM
        } else {
            if (message.user.uid == FirebaseAuth.getInstance().currentUser?.uid) {
                // If the current user is the sender of the message
                VIEW_TYPE_MESSAGE_SENT
            } else {
                // If some other user sent the message
                VIEW_TYPE_MESSAGE_RECEIVED
            }
        }
    }

    // Inflates the appropriate layout according to the ViewType.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            return SentMessageHolder(
                ItemChatMeBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            return ReceivedMessageHolder(
                ItemChatOtherBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else if (viewType == VIEW_TYPE_MESSAGE_SYSTEM) {
            return SystemMessageHolder(
                ItemChatSystemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
        return SentMessageHolder(ItemChatMeBinding.inflate(LayoutInflater.from(parent.context)))

    }

    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message: Message = mMessageList[position]
        when (holder!!.itemViewType) {
            VIEW_TYPE_MESSAGE_SENT -> (holder as SentMessageHolder?)!!.bind(message)
            VIEW_TYPE_MESSAGE_SYSTEM -> (holder as SystemMessageHolder?)!!.bind(message)
            VIEW_TYPE_MESSAGE_RECEIVED -> (holder as ReceivedMessageHolder?)!!
                .bind(message) {
                    val bundle = Bundle()
                    bundle.putString("user", Gson().toJson(mMessageList[position].user))
                    it.findNavController()
                        .navigate(
                            it.resources.getIdentifier(
                                "publicShowProfileFragment",
                                "id",
                                "it.polito.mad.lab02"
                            ),
                            bundle
                        )
                }

        }
    }


    private inner class SentMessageHolder(binding: ItemChatMeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var messageText: TextView = binding.textGchatMessageMe
        var timeText: TextView = binding.textGchatTimestampMe
        var dateText: TextView = binding.textGchatDateMe


        fun bind(message: Message) {
            messageText.text = message.text

            // Format the stored timestamp into a readable String using method.
            timeText.text = SimpleDateFormat("HH:mm").format(message.timestamp.toDate())
            dateText.text = SimpleDateFormat("MMMM dd").format(message.timestamp.toDate())
        }
    }

    private inner class ReceivedMessageHolder(binding: ItemChatOtherBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var messageText: TextView = binding.textGchatMessageOther
        var timeText: TextView = binding.textGchatTimestampOther
        var nameText: TextView = binding.textGchatUserOther
        var profileImage: ImageView = binding.imageGchatProfileOther
        var dateText: TextView = binding.textGchatDateOther

        fun bind(message: Message, action1: (v: View) -> Unit) {
            messageText.text = message.text
            // Format the stored timestamp into a readable String using method.
            timeText.text = SimpleDateFormat("HH:mm").format(message.timestamp.toDate())
            dateText.text = SimpleDateFormat("MMMM dd").format(message.timestamp.toDate())

            nameText.text = message.user.nickname

            // Insert the profile image from the URL into the ImageView.
            profileImage.load(Uri.parse(message.user.imageUri))
            profileImage.setOnClickListener(action1)
        }
    }

    private inner class SystemMessageHolder(binding: ItemChatSystemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var messageText: TextView = binding.textGchatMessageSystem

        fun bind(message: Message) {
            messageText.text = message.text
        }
    }

}