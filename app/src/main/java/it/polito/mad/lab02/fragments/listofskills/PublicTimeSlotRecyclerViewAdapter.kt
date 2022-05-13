package it.polito.mad.lab02.fragments.listofskills

import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.findNavController
import it.polito.mad.lab02.R
import it.polito.mad.lab02.databinding.FragmentPublicTimeSlotBinding
import it.polito.mad.lab02.models.Profile
import it.polito.mad.lab02.models.TimeSlot

class PublicTimeSlotRecyclerViewAdapter(
    private val values: List<TimeSlot>
) : RecyclerView.Adapter<PublicTimeSlotRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            FragmentPublicTimeSlotBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(
            values[position],
            {
                val bundle = Bundle()
                bundle.putString("id", values[position].id)
                it.findNavController()
                    .navigate(
                        R.id.action_publicTimeSlotFragment_to_publicTimeSlotDetailsFragment,
                        bundle
                    )
            },
            {
                val bundle = Bundle()
                bundle.putString("id", values[position].id)
                it.findNavController()
                    .navigate(
                        R.id.action_publicTimeSlotFragment_to_publicShowProfileFragment,
                        bundle
                    )
            }
        )
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentPublicTimeSlotBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val cardTitle: TextView = binding.cardTitle
        val cardLocation: TextView = binding.cardLocation
        val cardDate: TextView = binding.cardDate
        val cardDuration: TextView = binding.cardDuration
        val card: CardView = binding.cardAdvertisement
        val cardProfile: TextView = binding.profilePublicAdv
        val publisher: TextView = binding.profilePublicAdv

        fun bind(timeSlot: TimeSlot, action1: (v: View) -> Unit, action2: (v: View) -> Unit) {
            card.setOnClickListener(action1)
            publisher.setOnClickListener(action2)
            cardTitle.text = timeSlot.title
            cardLocation.text = timeSlot.location
            cardDate.text = timeSlot.dateTime
            cardDuration.text = timeSlot.duration
            cardProfile.text = timeSlot.userProfile.nickname
        }

        fun unbind() {
            card.setOnClickListener(null)
        }
    }

}