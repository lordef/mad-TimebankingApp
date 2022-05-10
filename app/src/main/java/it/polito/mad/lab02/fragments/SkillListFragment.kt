package it.polito.mad.lab02.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import it.polito.mad.lab02.R
import it.polito.mad.lab02.viewmodels.SkillListViewModel

class SkillListFragment : Fragment(R.layout.fragment_all_skills) {

    private var columnCount = 1

    private val vm by viewModels<SkillListViewModel>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.allSkillsList)

        vm.getSkillList().observe(viewLifecycleOwner) { skillList ->
            if (recyclerView is RecyclerView) {
                with(recyclerView) {
                    layoutManager = when {
                        columnCount <= 1 -> LinearLayoutManager(context)
                        else -> GridLayoutManager(context, columnCount)
                    }
                    adapter = SkillListRecyclerViewAdapter(skillList)
                }
            }

            val textView = view.findViewById<TextView>(R.id.text_skills)
            if (skillList.isEmpty()) {
                recyclerView.visibility = View.GONE
                textView.visibility = View.VISIBLE
            } else {
                textView.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
        }


    }
}