package it.polito.mad.lab02.fragments

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import it.polito.mad.lab02.databinding.FragmentSkillsBinding

import it.polito.mad.lab02.fragments.placeholder.PlaceholderContent.PlaceholderItem

/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class SkillRecyclerViewAdapter(
    private val values: MutableList<String>
) : RecyclerView.Adapter<SkillRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentSkillsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(values[position])

    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentSkillsBinding) : RecyclerView.ViewHolder(binding.root) {
        val skill: TextView = binding.skill


        fun bind(sk: String){
            skill.text = sk
        }
    }

}