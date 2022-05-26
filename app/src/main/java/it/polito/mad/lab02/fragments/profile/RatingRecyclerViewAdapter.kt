package it.polito.mad.lab02.fragments.profile

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
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

    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentRatingsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val ratingBar: RatingBar = binding.ratingBar
        val rater: TextView = binding.rater
        val comment: TextView = binding.comment
        val timestamp: TextView = binding.timestamp

        fun bind(rating: Rating){
            ratingBar.rating = rating.stars.toFloat()
            rater.text = rating.rater
            comment.text = rating.comment
            timestamp.text = rating.timestamp
        }

    }

}