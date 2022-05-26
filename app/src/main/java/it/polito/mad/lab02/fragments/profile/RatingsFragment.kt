package it.polito.mad.lab02.fragments.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import it.polito.mad.lab02.R
import it.polito.mad.lab02.viewmodels.MainActivityViewModel

class RatingsFragment : Fragment() {

    private var columnCount = 1

    private val vm by activityViewModels<MainActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_ratings_list_with_no_ratings, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.ratings_list)


        val userUid = arguments?.getString("userUid")

        if (userUid != null) {

            vm.setRatingsListenerByUserUid(userUid)

            vm.ratingList.observe(viewLifecycleOwner) { ratings ->
                if (view is RecyclerView) {
                    with(view) {
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


        // Set the adapter
        return view
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            RatingsFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}