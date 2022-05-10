package it.polito.mad.lab02.fragments

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import it.polito.mad.lab02.databinding.FragmentSkillListBinding
import it.polito.mad.lab02.models.Skill
import it.polito.mad.lab02.models.TimeSlot


/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class SkillListRecyclerViewAdapter(
    private val values: MutableList<Skill>
) : RecyclerView.Adapter<SkillListRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentSkillListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
//        holder.idView.text = item.id
//        holder.contentView.text = item.content
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentSkillListBinding) :
        RecyclerView.ViewHolder(binding.root) {
//        val idView: TextView = binding.itemNumber
//        val contentView: TextView = binding.content
        val skillName: TextView = binding.skillName

        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }
    }

}