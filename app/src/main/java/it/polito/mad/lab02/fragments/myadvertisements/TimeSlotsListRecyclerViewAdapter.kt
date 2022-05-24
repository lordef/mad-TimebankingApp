package it.polito.mad.lab02.fragments.myadvertisements

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.lab02.R
import it.polito.mad.lab02.databinding.FragmentTimeSlotsListBinding
import it.polito.mad.lab02.models.TimeSlot
import androidx.recyclerview.widget.DiffUtil
import kotlinx.coroutines.delay


class TimeSlotsListRecyclerViewAdapter(
    private val values: MutableList<TimeSlot>,
    private val itemClickListener: (timeslot: String) -> Unit
) : RecyclerView.Adapter<TimeSlotsListRecyclerViewAdapter.ViewHolder>() {

    private var displayData = values.toMutableList()

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
        val timeslot = values[position]
        holder.bind(timeslot,
            {
                val bundle = Bundle()
                bundle.putString("id", timeslot.id)
                it.findNavController()
                    .navigate(R.id.action_nav_advertisement_to_timeSlotDetailsFragment, bundle)
            }, {
                val bundle = Bundle()
                bundle.putString("id", timeslot.id)
                it.findNavController()
                    .navigate(R.id.action_nav_advertisement_to_timeSlotEditFragment, bundle)
            }, {
                val pos = values.indexOf(timeslot)
                if (pos != -1){
                    this.animationOnDelete(timeslot.id).also {
                        val handler = Handler()
                        val runnable = Runnable {
                            // useful to call interaction with viewModel
                            itemClickListener(timeslot.id)
                        }
                        handler.postDelayed(runnable, 250)
                    }

                }
            })

    }

    private fun animationOnDelete(timeslotId: String){
        val oldData = displayData
        displayData = values.filter { it.id != timeslotId }.toMutableList()
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

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentTimeSlotsListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val cardTitle: TextView = binding.cardTitle
        val cardLocation: TextView = binding.cardLocation
        val cardDate: TextView = binding.cardDate
        val cardDuration: TextView = binding.cardDuration
        val card: CardView = binding.cardAdvertisement
        val editButton: ImageButton = binding.editTimeSlotButton
        val deleteButton: ImageButton = binding.deleteTimeSlotImageButton //TODO: delete from recycler + vm for firebase


        fun bind(timeSlot: TimeSlot, action1: (v: View) -> Unit, action2: (v: View) -> Unit, action3: (v: View) -> Unit) {
            card.setOnClickListener(action1)
            editButton.setOnClickListener(action2)
            cardTitle.text = timeSlot.title
            cardLocation.text = timeSlot.location
            cardDate.text = timeSlot.dateTime
            cardDuration.text = timeSlot.duration
            deleteButton.setOnClickListener(action3)

        }

        fun unbind() {
            card.setOnClickListener(null)
        }
    }

}