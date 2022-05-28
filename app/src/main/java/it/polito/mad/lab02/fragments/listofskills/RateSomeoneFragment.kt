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
import com.google.gson.Gson
import it.polito.mad.lab02.R
import it.polito.mad.lab02.models.Profile
import it.polito.mad.lab02.viewmodels.MainActivityViewModel
import java.util.*
import kotlin.math.roundToInt


class RateSomeoneFragment : Fragment(R.layout.fragment_rate_someone) {

    private val vm by activityViewModels<MainActivityViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val isLoggedUserPublisher = arguments?.getBoolean("isLoggedUserPublisher")
        val profileRatedIncoming = arguments?.getString("profileRated")

        lateinit var profileRated: Profile
        lateinit var profileRater: Profile

        vm.profile.observe(viewLifecycleOwner){profile ->
            profileRater = profile
        }

        if(isLoggedUserPublisher == false){
            profileRated = Gson().fromJson(profileRatedIncoming, Profile::class.java)
        }else{
            vm.setUserListenerByUserUid(profileRatedIncoming!!)
            vm.userProfile.observe(viewLifecycleOwner){ profile ->
                profileRated = profile
            }
        }





        val rated = view.findViewById<TextView>(R.id.userRated)
        val ratingBar = view.findViewById<RatingBar>(R.id.ratingbar)
        val comment = view.findViewById<EditText>(R.id.comment)
        val button = view.findViewById<Button>(R.id.button3)

        rated.text = profileRated.nickname

        var stars = 0

        ratingBar.setOnRatingBarChangeListener { ratingBar, fl, b ->
            stars = fl.roundToInt()
        }

        button.setOnClickListener{
            val newRating = Rating(profileRated, profileRater, stars, comment.text.toString(), Date().toString())
            vm.postRating(newRating)
        }
    }



}