package it.polito.mad.lab02.fragments.myadvertisements

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
import it.polito.mad.lab02.databinding.FragmentTimeSlotOfInterestBinding
import it.polito.mad.lab02.models.TimeSlot

/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class TimeSlotsOfInterestRecyclerViewAdapter(
    private val values: List<TimeSlot>
) : RecyclerView.Adapter<TimeSlotsOfInterestRecyclerViewAdapter.ViewHolder>() {

    private var displayData = values.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            FragmentTimeSlotOfInterestBinding.inflate(
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
                bundle.putString("timeslot", Gson().toJson(displayData[position]))
                it.findNavController()
                    .navigate(
                        R.id.publicTimeSlotDetailsFragment,
                        bundle
                    )
            },
            {
                val bundle = Bundle()
                bundle.putString("id", displayData[position].id)
                it.findNavController()
                    .navigate(
                        R.id.action_nav_timeSlotsOfInterestFragment_to_publicShowProfileFragment,
                        bundle
                    )
            }
        )
    }

    override fun getItemCount(): Int = displayData.size

    inner class ViewHolder(binding: FragmentTimeSlotOfInterestBinding) :
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