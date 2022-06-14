package it.polito.mad.lab02.fragments.myadvertisements

import android.net.Uri
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.navigation.findNavController
import coil.load
import com.google.gson.Gson
import it.polito.mad.lab02.R
import it.polito.mad.lab02.Utils.fromHHMMToString


import it.polito.mad.lab02.databinding.FragmentTimeSlotAssignedAndAcceptedBinding
import it.polito.mad.lab02.models.TimeSlot
import java.text.SimpleDateFormat
import java.util.*

class TimeSlotAssignedAndAcceptedRecyclerViewAdapter(
    private val values: List<TimeSlot>,
    private val selector: Int
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

        holder.bind(timeslot,
            {
                val bundle = Bundle()
                val ts = Gson().toJson(timeslot)
                bundle.putString("timeslotRated", ts)

                if (selector == 0) {
                    val isLoggedUserPublisher = false
                    val rated = Gson().toJson(timeslot.userProfile) // questo è un profile
                    bundle.putBoolean("isLoggedUserPublisher", isLoggedUserPublisher)
                    bundle.putString("profileRated", rated)
                } else {
                    val isLoggedUserPublisher = true
                    val rated = timeslot.assignee // questa è una ref
                    bundle.putBoolean("isLoggedUserPublisher", isLoggedUserPublisher)
                    bundle.putString("profileRated", rated)
                }

                it.findNavController()
                    .navigate(
                        R.id.action_nav_timeSlotAssignedAndAcceptedFragment_to_rateSomeoneFragment,
                        bundle
                    )
            }, {
                val bundle = Bundle()
                bundle.putString("id", values[position].id)
                bundle.putString("timeslot", Gson().toJson(values[position]))
                if(selector == 0)
                    bundle.putString("origin", "assigned")
                else
                    bundle.putString("origin", "accepted")
                it.findNavController()
                    .navigate(
                        R.id.publicTimeSlotDetailsFragment,
                        bundle
                    )
            },
            {
                val bundle = Bundle()
                bundle.putString("id", values[position].id)

                if(selector == 0)
                    bundle.putString("origin", "assigned")
                else
                    bundle.putString("origin", "accepted")

                it.findNavController()
                    .navigate(
                        R.id.action_nav_timeSlotAssignedAndAcceptedFragment_to_publicShowProfileFragment,
                        bundle
                    )
            })

    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentTimeSlotAssignedAndAcceptedBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val cardTitle: TextView = binding.cardTitle
        private val cardLocation: TextView = binding.cardLocation
        private val cardProfile: TextView = binding.profilePublicAdv
        private val cardDate: TextView = binding.cardDate
        private val cardDuration: TextView = binding.cardDuration
        private val rateButton: Button = binding.rateButton
        private val cardAdvertisement: CardView = binding.cardAdvertisement
        val imageProfile: ImageView = binding.publisherImageView
        val ratingConstraint:ConstraintLayout = binding.ratingConstraint
        val profileConstraint:ConstraintLayout = binding.constraintLayout6
        private val publisher: TextView = binding.profilePublicAdv

        fun bind(timeSlot: TimeSlot, action1: (v: View) -> Unit, action2: (v: View) -> Unit, action3: (v: View) -> Unit) {
            cardAdvertisement.setOnClickListener(action2)
            publisher.setOnClickListener(action3)
            imageProfile.setOnClickListener(action3)

            cardTitle.text = timeSlot.title
            cardLocation.text = timeSlot.location

            cardDate.text = timeSlot.dateTime
            cardDuration.text = fromHHMMToString(timeSlot.duration)

            if(selector == 0){
                cardProfile.text = timeSlot.userProfile.nickname
                imageProfile.load(Uri.parse(timeSlot.userProfile.imageUri))
            }else{
                cardProfile.visibility = View.GONE
                imageProfile.visibility = View.GONE
                profileConstraint.visibility = View.GONE
            }

            if (isTimeslotPassed(timeSlot.dateTime, timeSlot.duration)) {
                rateButton.visibility = View.VISIBLE
                rateButton.setOnClickListener(action1)
                ratingConstraint.setOnClickListener(action1)

            } else {
                rateButton.visibility = View.GONE
            }

        }

    }

    fun isTimeslotPassed(dateTime: String, duration: String): Boolean {
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

        return if (todayYY > yy) true
        else if (todayYY == yy) {
            if (todayMM > mm) true
            else if (todayMM == mm) {
                if (todayDD > dd) true
                else if (todayDD == dd) {
                    if (nowHH > hh) true
                    else if (nowHH == hh) {
                        nowMIN > min
                    } else false
                } else false
            } else false
        } else false


    }

}