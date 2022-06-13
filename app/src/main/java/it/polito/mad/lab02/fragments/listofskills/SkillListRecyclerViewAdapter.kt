package it.polito.mad.lab02.fragments.listofskills

import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import it.polito.mad.lab02.R
import it.polito.mad.lab02.databinding.FragmentSkillListBinding
import it.polito.mad.lab02.models.Skill
import it.polito.mad.lab02.models.TimeSlot
import java.util.*

class SkillListRecyclerViewAdapter(
    private var values: List<Skill>
) : RecyclerView.Adapter<SkillListRecyclerViewAdapter.ViewHolder>() {

    private var displayData = values.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            FragmentSkillListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ).apply {
                root.setOnClickListener {
                    it.isActivated = !it.isActivated
                }
            }
        )
    }

    fun setValues(newValues: List<Skill>){
        values = newValues
        displayData = newValues.toMutableList()
    }

    fun setFilter(filter: (Skill)->Boolean) {
        val oldData = displayData
        displayData = if(filter != null){
            values.filter(filter).toMutableList()
        } else{
            values.toMutableList()
        }
        val diffs = DiffUtil.calculateDiff(
            MyDiffCallback(
                oldData,
                displayData
            )
        )
        diffs.dispatchUpdatesTo(this)
    }

    class MyDiffCallback(val old: List<Skill>, val new: List<Skill>): DiffUtil.Callback() {
        override fun getOldListSize(): Int = old.size

        override fun getNewListSize(): Int = new.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return old[oldItemPosition] === new[newItemPosition]
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return old[oldItemPosition] == new[newItemPosition]
        }
    }

    override fun onViewRecycled(holder: ViewHolder) {
        holder.itemView.rotation = 0f
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(values[position])
        {
            val bundle = Bundle()
            bundle.putString("skill", values[position].ref)
            it.findNavController()
                .navigate(R.id.action_nav_all_advertisements_to_publicTimeSlotFragment, bundle)
        }
    }

    override fun getItemCount(): Int = displayData.size

    inner class ViewHolder(private val binding: FragmentSkillListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val skillName: TextView = binding.skillName
        val card: CardView = binding.cardSkillAdvertisement

        fun bind(skill: Skill, action1: (v: View) -> Unit) {
            skillName.text =
                skill.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
            card.setOnClickListener(action1)
            binding.executePendingBindings()
        }
    }

}