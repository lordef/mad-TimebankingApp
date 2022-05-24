package it.polito.mad.lab02.fragments.profile

import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.lab02.databinding.FragmentSkillsBinding


class SkillRecyclerViewAdapter(
    private val values: MutableList<String>,
    private val itemClickListener: (skill: String) -> Unit // notice here
) : RecyclerView.Adapter<SkillRecyclerViewAdapter.ViewHolder>() {

    private var displayData = values.toMutableList()

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
                this.animationOnDelete(skill).also {
                    val handler = Handler()
                    val runnable = Runnable {
                        // useful to call interaction with viewModel
                        itemClickListener(skill)
                    }
                    handler.postDelayed(runnable, 250)
                }
            }
        }

    }

    private fun animationOnDelete(skill: String){
        val oldData = displayData
        displayData = values.filter { it != skill }.toMutableList()
        val diffs = DiffUtil.calculateDiff(MyDiffCallback(oldData, displayData))
        diffs.dispatchUpdatesTo(this)
    }

    class MyDiffCallback(val old: List<String>, val new: List<String>): DiffUtil.Callback() {
        override fun getOldListSize(): Int = old.size

        override fun getNewListSize(): Int = new.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return old[oldItemPosition] === new[newItemPosition]
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return old[oldItemPosition] == new[newItemPosition]
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