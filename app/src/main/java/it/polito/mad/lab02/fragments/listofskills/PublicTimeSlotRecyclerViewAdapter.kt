package it.polito.mad.lab02.fragments.listofskills

import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import it.polito.mad.lab02.R
import it.polito.mad.lab02.databinding.FragmentPublicTimeSlotBinding
import it.polito.mad.lab02.models.Profile
import it.polito.mad.lab02.models.TimeSlot

class PublicTimeSlotRecyclerViewAdapter(
    private val values: List<TimeSlot>
) : RecyclerView.Adapter<PublicTimeSlotRecyclerViewAdapter.ViewHolder>() {

    private var displayData = values.toMutableList()

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
            displayData[position],
            {
                val bundle = Bundle()
                bundle.putString("id", displayData[position].id)
                it.findNavController()
                    .navigate(
                        R.id.action_publicTimeSlotFragment_to_publicTimeSlotDetailsFragment,
                        bundle
                    )
            },
            {
                val bundle = Bundle()
                bundle.putString("id", displayData[position].id)
                it.findNavController()
                    .navigate(
                        R.id.action_publicTimeSlotFragment_to_publicShowProfileFragment,
                        bundle
                    )
            }
        )
    }

    override fun getItemCount(): Int = displayData.size

    fun setFilter(filter: (TimeSlot)->Boolean) {
        val oldData = displayData
        displayData = if(filter != null){
            values.filter(filter).toMutableList()
        } else{
            values.toMutableList()
        }
        val diffs = DiffUtil.calculateDiff(MyDiffCallback(oldData, displayData))
        diffs.dispatchUpdatesTo(this)
    }

    class MyDiffCallback(val old: List<TimeSlot>, val new: List<TimeSlot>): DiffUtil.Callback() {
        override fun getOldListSize(): Int = old.size

        override fun getNewListSize(): Int = new.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return old[oldItemPosition] === new[newItemPosition]
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return old[oldItemPosition] == new[newItemPosition]
        }
    }

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