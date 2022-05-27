package it.polito.mad.lab02.fragments.myadvertisements

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import it.polito.mad.lab02.R


import it.polito.mad.lab02.databinding.FragmentTimeSlotAssignedAndAcceptedBinding
import it.polito.mad.lab02.models.TimeSlot
import java.text.SimpleDateFormat
import java.util.*

/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class TimeSlotAssignedAndAcceptedRecyclerViewAdapter(
    private val values: List<TimeSlot>
) : RecyclerView.Adapter<TimeSlotAssignedAndAcceptedRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentTimeSlotAssignedAndAcceptedBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val timeslot = values[position]
        holder.bind(timeslot, {}, {})

    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentTimeSlotAssignedAndAcceptedBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val cardTitle: TextView = binding.cardTitle
        val cardLocation: TextView = binding.cardLocation
        val cardDate: TextView = binding.cardDate
        val cardDuration: TextView = binding.cardDuration
        val rateButton: ImageButton = binding.rateButton

        fun bind(timeSlot: TimeSlot, action1: (v: View) -> Unit, action2: (v: View) -> Unit){
            cardTitle.text = timeSlot.title
            cardLocation.text = timeSlot.location
            cardDate.text = timeSlot.dateTime
            cardDuration.text = timeSlot.duration

            if (isTimeslotPassed(timeSlot.dateTime, timeSlot.duration)){
                rateButton.visibility = View.VISIBLE
            }else{
                rateButton.visibility = View.GONE
            }

        }

    }

    fun isTimeslotPassed(dateTime: String, duration: String): Boolean{
        val date = dateTime.split(" ")[0]
        val time = dateTime.split(" ")[1]

        val dd = date.split("/")[0].toInt()
        val mm = date.split("/")[1].toInt()
        val yy = date.split("/")[2].toInt()
        val hh = time.split(":")[0].toInt() + duration.split(":")[0].toInt()
        val min = time.split(":")[1].toInt() + duration.split(":")[1].toInt()


        val todayDate = SimpleDateFormat("yyyy/MM/dd").format(Calendar.getInstance().time)
        val nowTime = SimpleDateFormat("HH:mm").format(Calendar.getInstance().time)

        val todayDD = todayDate.split("/")[2].toInt()
        val todayMM = todayDate.split("/")[1].toInt()
        val todayYY = todayDate.split("/")[0].toInt()
        val nowHH = nowTime.split(":")[0].toInt()
        val nowMIN = nowTime.split(":")[1].toInt()

        return if(todayYY>yy) true
        else if (todayYY==yy){
            if(todayMM>mm) true
            else if (todayMM==mm){
                if(todayDD>dd) true
                else if (todayDD==dd){
                    if(nowHH>hh) true
                    else if (nowHH==hh){
                        nowMIN>min
                    }else false
                }else false
            }else false
        }else false



    }

}