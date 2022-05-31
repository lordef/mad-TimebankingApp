package it.polito.mad.lab02.fragments.profile

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import it.polito.mad.lab02.R
import it.polito.mad.lab02.viewmodels.MainActivityViewModel

class RatingsFragment : Fragment(R.layout.fragment_ratings_list_with_no_ratings_2) {

    private var columnCount = 1

    private val vm by activityViewModels<MainActivityViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)


        val recyclerView = view.findViewById<RecyclerView>(R.id.ratings_list)


        val userUid = arguments?.getString("userUid")


        if (userUid != null) {

            vm.setRatingsListenerByUserUid(userUid,)

            vm.ratingList.observe(viewLifecycleOwner) { ratings ->
                if (recyclerView is RecyclerView) {
                    with(recyclerView) {
                        layoutManager = when {
                            columnCount <= 1 -> LinearLayoutManager(context)
                            else -> GridLayoutManager(context, columnCount)
                        }
                        adapter = RatingRecyclerViewAdapter(ratings)
                    }
                }

                val textView = view.findViewById<TextView>(R.id.no_ratings_text)
                if (ratings.isEmpty()) {
                    recyclerView.visibility = View.GONE
                    textView.visibility = View.VISIBLE
                } else {
                    textView.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                }
            }
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                view.findNavController().navigateUp()
                onBackPressed()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            android.R.id.home -> {
                findNavController().navigateUp()
                onBackPressed()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }

    }

    private fun onBackPressed(){
        val runnable = Runnable {
            // useful to call interaction with viewModel
            vm.removeRatingsListener()
        }
        // Perform persistence changes after 250 millis
        Handler().postDelayed(runnable, 250)
    }

}