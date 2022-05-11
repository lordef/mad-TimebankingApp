package it.polito.mad.lab02.fragments.myadvertisements

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.lab02.R
import it.polito.mad.lab02.databinding.FragmentTimeSlotsListBinding
import it.polito.mad.lab02.models.TimeSlot

class TimeSlotsListRecyclerViewAdapter(
    private val values: MutableList<TimeSlot>
) : RecyclerView.Adapter<TimeSlotsListRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            FragmentTimeSlotsListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(values[position],
            {
                val bundle = Bundle()
                bundle.putString("id", values[position].id)
                it.findNavController()
                    .navigate(R.id.action_nav_advertisement_to_timeSlotDetailsFragment, bundle)
            }, {
                val bundle = Bundle()
                bundle.putString("id", values[position].id)
                it.findNavController()
                    .navigate(R.id.action_nav_advertisement_to_timeSlotEditFragment, bundle)
            })

    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentTimeSlotsListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val cardTitle: TextView = binding.cardTitle
        val cardLocation: TextView = binding.cardLocation
        val cardDate: TextView = binding.cardDate
        val cardDuration: TextView = binding.cardDuration
        val card: CardView = binding.cardAdvertisement
        val button: Button = binding.editTimeSlotButton

        fun bind(timeSlot: TimeSlot, action1: (v: View) -> Unit, action2: (v: View) -> Unit) {
            card.setOnClickListener(action1)
            button.setOnClickListener(action2)
            cardTitle.text = timeSlot.title
            cardLocation.text = timeSlot.location
            cardDate.text = timeSlot.dateTime
            cardDuration.text = timeSlot.duration
        }

        fun unbind() {
            card.setOnClickListener(null)
        }
    }

}