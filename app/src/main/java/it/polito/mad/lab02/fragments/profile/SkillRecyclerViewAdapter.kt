package it.polito.mad.lab02.fragments.profile

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import it.polito.mad.lab02.databinding.FragmentSkillsBinding


class SkillRecyclerViewAdapter(
    private val values: MutableList<String>,
    private val itemClickListener: (skill: String) -> Unit // notice here
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
        val skill = values[position]
        holder.bind(skill)
        {
            val pos = values.indexOf(skill)
            if (pos != -1){
                itemClickListener(skill)
                //values.removeAt(pos)
                //notifyItemRemoved(pos)

            }
        }

    }

    override fun getItemCount(): Int = values.size


    inner class ViewHolder(binding: FragmentSkillsBinding) : RecyclerView.ViewHolder(binding.root) {
        val skill: TextView = binding.skill
        val deleteButton : ImageButton = binding.deleteButton


        fun bind(sk: String, action1: (v: View) -> Unit){
            skill.text = sk
            deleteButton.setOnClickListener(action1)
        }
    }


}