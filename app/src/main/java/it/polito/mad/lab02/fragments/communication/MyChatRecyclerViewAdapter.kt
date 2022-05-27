package it.polito.mad.lab02.fragments.communication

import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import it.polito.mad.lab02.R
import it.polito.mad.lab02.databinding.FragmentChatBinding
import it.polito.mad.lab02.models.Chat

class MyChatRecyclerViewAdapter(
    values: List<Chat>
) : RecyclerView.Adapter<MyChatRecyclerViewAdapter.ViewHolder>() {

    private var displayData = values.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            FragmentChatBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(
            displayData[position],
         {
            val bundle = Bundle()
            bundle.putString("id", displayData[position].id)
            it.findNavController()
                .navigate(
                    R.id.action_chatFragment_to_nav_single_message,
                    bundle
                )
        }
        ) {
            val bundle = Bundle()
            if (displayData[position].publisher.uid == FirebaseAuth.getInstance().currentUser?.uid ?: false) {
                bundle.putString("user", Gson().toJson(displayData[position].requester))
            } else {
                bundle.putString("user", Gson().toJson(displayData[position].publisher))
            }

            it.findNavController()
                .navigate(
                    R.id.action_nav_chats_to_publicShowProfileFragment,
                    bundle
                )
        }
    }

    override fun getItemCount(): Int = displayData.size

    inner class ViewHolder(binding: FragmentChatBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val cardTitle: TextView = binding.cardTitle
        val cardProfile: TextView = binding.cardProfile
        val cardChat: CardView = binding.cardChat

        fun bind(chat: Chat, action1: (v: View) -> Unit, action2: (v: View) -> Unit) {
            cardChat.setOnClickListener(action1)
            cardTitle.text = chat.timeSlot.title
            cardProfile.setOnClickListener(action2)
            if (chat.publisher.uid == FirebaseAuth.getInstance().currentUser?.uid ?: false) {
                cardProfile.text = chat.requester.nickname
            }
            else{
                cardProfile.text = chat.publisher.nickname
            }
        }

        fun unbind() {
            cardChat.setOnClickListener(null)
        }
    }

}