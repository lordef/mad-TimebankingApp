package it.polito.mad.lab02.fragments.myadvertisements

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.lab02.R
import it.polito.mad.lab02.databinding.FragmentTimeSlotsListBinding
import it.polito.mad.lab02.models.TimeSlot
import androidx.recyclerview.widget.DiffUtil
import it.polito.mad.lab02.Utils.fromHHMMToString


class TimeSlotsListRecyclerViewAdapter(
    private val context: Context,
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
            }) {
            val pos = values.indexOf(timeslot)
            if (pos != -1) {
                /* Dialog before deletion */
                val builder: AlertDialog.Builder =
                    AlertDialog.Builder(context, R.style.AlertDialogTheme)

                builder.setCancelable(true)
                builder.setTitle("Delete advertisement?")
                builder.setMessage("This will permanently delete this advertisement and ALL related chats, if any. ")

                builder.setNeutralButton("Cancel"
                ) { dialogInterface, i -> dialogInterface.cancel() }

                builder.setNegativeButton("Delete"
                ) { dialogInterface, i ->
                    this.animationOnDelete(timeslot.id).also {
                        val handler = Handler()
                        val runnable = Runnable {
                            // useful to call interaction with viewModel
                            itemClickListener(timeslot.id)
                        }
                        handler.postDelayed(runnable, 250)
                    }
                }
                builder.show()
            }
        }

    }

    private fun animationOnDelete(timeslotId: String) {
        val oldData = displayData
        displayData = values.filter { it.id != timeslotId }.toMutableList()
        val diffs = DiffUtil.calculateDiff(MyDiffCallback(oldData, displayData))
        diffs.dispatchUpdatesTo(this)
    }

    class MyDiffCallback(val old: List<TimeSlot>, val new: List<TimeSlot>) : DiffUtil.Callback() {
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
        val edit: ConstraintLayout = binding.editConstraintLayout
        val delete: ConstraintLayout = binding.deleteConstraintLayout
        val editButton: ImageButton = binding.editTimeSlotButton
        val deleteButton: ImageButton = binding.deleteTimeSlotImageButton
        val assignedText: Button = binding.acceptedButton

        fun bind(
            timeSlot: TimeSlot,
            action_toTimeSlotDetailsFragment: (v: View) -> Unit,
            action_toTimeSlotEditFragment: (v: View) -> Unit,
            action_deleteTimeSlot: (v: View) -> Unit
        ) {
            card.setOnClickListener(action_toTimeSlotDetailsFragment)
            editButton.setOnClickListener(action_toTimeSlotEditFragment)
            cardTitle.text = timeSlot.title
            cardLocation.text = timeSlot.location
            cardDate.text = timeSlot.dateTime
            cardDuration.text = fromHHMMToString(timeSlot.duration)
            deleteButton.setOnClickListener(action_deleteTimeSlot)

            if (timeSlot.state == "ACCEPTED") {
                //assignedText.text = "ACCEPTED"
                edit.visibility = View.GONE
                delete.visibility = View.GONE
                assignedText.visibility = View.VISIBLE
            } else {
                edit.visibility = View.VISIBLE
                delete.visibility = View.VISIBLE
                assignedText.visibility = View.GONE
            }

        }

        fun unbind() {
            card.setOnClickListener(null)
        }
    }

}