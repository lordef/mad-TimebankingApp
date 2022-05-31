package it.polito.mad.lab02.fragments.profile

import android.net.Uri
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.findNavController
import coil.load
import com.google.gson.Gson
import it.polito.mad.lab02.R
import it.polito.mad.lab02.databinding.FragmentRatingsBinding
import it.polito.mad.lab02.models.Rating

class RatingRecyclerViewAdapter(
    private val values: List<Rating>
) : RecyclerView.Adapter<RatingRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentRatingsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val rating = values[position]
        holder.bind(rating)
        {
            val bundle = Bundle()
            bundle.putString("id", values[position].timeslot.id)
            bundle.putString("origin", "ratings")
            bundle.putString("profileRater", Gson().toJson(values[position].rater))
            it.findNavController()
                .navigate(
                    R.id.action_ratingsFragment_to_publicShowProfileFragment,
                    bundle
                )
        }

    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentRatingsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val ratingBar: RatingBar = binding.ratingBar
        val rater: TextView = binding.rater
        val raterImage : ImageView = binding.imageViewRater
        val comment: TextView = binding.comment
        val timestamp: TextView = binding.timestamp
        val profileRater : ConstraintLayout = binding.profileLayout

        fun bind(rating: Rating, action1: (v: View) -> Unit){
            ratingBar.rating = rating.starsNum.toFloat()
            rater.text = rating.rater!!.nickname
            if(rating.comment.isNotEmpty())
                comment.text = rating.comment
            else
                comment.visibility = View.GONE
            timestamp.text = rating.timestamp

            raterImage.load(Uri.parse(rating.rater.imageUri))
            profileRater.setOnClickListener(action1)
        }

    }

}