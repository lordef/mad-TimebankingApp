package it.polito.mad.lab02.fragments.listofskills

import it.polito.mad.lab02.models.Rating
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import it.polito.mad.lab02.R
import it.polito.mad.lab02.viewmodels.MainActivityViewModel
import java.util.*
import kotlin.math.roundToInt


class RateSomeoneFragment : Fragment(R.layout.fragment_rate_someone) {

    private val vm by activityViewModels<MainActivityViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rated = view.findViewById<TextView>(R.id.userRated)
        val timeslot = view.findViewById<TextView>(R.id.timeslotTitle)
        val ratingBar = view.findViewById<RatingBar>(R.id.ratingbar)
        val comment = view.findViewById<EditText>(R.id.comment)
        val button = view.findViewById<Button>(R.id.button3)

        var stars = 0

        ratingBar.setOnRatingBarChangeListener { ratingBar, fl, b ->
            stars = fl.roundToInt()
        }

        button.setOnClickListener{
            val newRating = Rating("", rated.text.toString(), "", stars, comment.text.toString(), Date().toString())
            vm.postRating(newRating)
        }
    }



}