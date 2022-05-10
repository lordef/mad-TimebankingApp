package it.polito.mad.lab02.fragments

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import it.polito.mad.lab02.databinding.FragmentSkillListBinding
import it.polito.mad.lab02.models.Skill

class SkillListRecyclerViewAdapter(
    private val values: List<Skill>
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
        holder.bind(values[position].name,
            {
                //TODO: add navigation to list of adv for that skill
            })
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentSkillListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val skillName: TextView = binding.skillName
        val card: CardView = binding.cardSkillAdvertisement

        fun bind(skill: String, action1: (v: View) -> Unit){
            skillName.text = skill
            card.setOnClickListener(action1)
        }

    }

}