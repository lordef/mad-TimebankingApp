package it.polito.mad.lab02.fragments.profile

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
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

    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentRatingsBinding) :
        RecyclerView.ViewHolder(binding.root) {



    }

}