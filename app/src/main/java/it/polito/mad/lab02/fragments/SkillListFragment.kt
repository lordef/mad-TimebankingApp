package it.polito.mad.lab02.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import androidx.fragment.app.viewModels
import it.polito.mad.lab02.R
import it.polito.mad.lab02.viewmodels.SkillListViewModel

class SkillListFragment : Fragment() {

    private var columnCount = 1

   private val vm by viewModels<SkillListViewModel>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.allSkillsList)

        vm.getSkillList().observe(viewLifecycleOwner){
            Log.d("myTag", it.toString())
        }


    }
}