package it.polito.mad.lab02.fragments.communication

import android.R
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.firebase.auth.FirebaseAuth
import it.polito.mad.lab02.databinding.ItemChatMeBinding
import it.polito.mad.lab02.models.Message
import java.util.*

import it.polito.mad.lab02.databinding.ItemChatOtherBinding
import java.text.SimpleDateFormat


class MessageRecyclerViewAdapter(messageList: List<Message>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val mMessageList: List<Message>

    companion object {
        private const val VIEW_TYPE_MESSAGE_SENT = 1
        private const val VIEW_TYPE_MESSAGE_RECEIVED = 2
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
        return if (message.user.uid == FirebaseAuth.getInstance().currentUser?.uid) {
            // If the current user is the sender of the message
            VIEW_TYPE_MESSAGE_SENT
        } else {
            // If some other user sent the message
            VIEW_TYPE_MESSAGE_RECEIVED
        }
    }

    // Inflates the appropriate layout according to the ViewType.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            return SentMessageHolder(ItemChatMeBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            return ReceivedMessageHolder(ItemChatOtherBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
         return SentMessageHolder(ItemChatMeBinding.inflate(LayoutInflater.from(parent.context)))

    }

    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message: Message = mMessageList[position]
        when (holder!!.itemViewType) {
            VIEW_TYPE_MESSAGE_SENT -> (holder as SentMessageHolder?)!!.bind(message)
            VIEW_TYPE_MESSAGE_RECEIVED -> (holder as ReceivedMessageHolder?)!!.bind(message)
        }
    }


    private inner class SentMessageHolder (binding: ItemChatMeBinding) :
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

    private inner class ReceivedMessageHolder (binding: ItemChatOtherBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var messageText: TextView = binding.textGchatMessageOther
        var timeText: TextView = binding.textGchatTimestampOther
        var nameText: TextView = binding.textGchatUserOther
        var profileImage: ImageView = binding.imageGchatProfileOther
        var dateText: TextView = binding.textGchatDateOther

        fun bind(message: Message) {
            messageText.text = message.text
            // Format the stored timestamp into a readable String using method.
            timeText.text = SimpleDateFormat("HH:mm").format(message.timestamp.toDate())
            dateText.text = SimpleDateFormat("MMMM dd").format(message.timestamp.toDate())

            nameText.text = message.user.nickname

            // Insert the profile image from the URL into the ImageView.
            profileImage.load(Uri.parse(message.user.imageUri))
        }
    }

}