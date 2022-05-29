package it.polito.mad.lab02.fragments.listofskills

import it.polito.mad.lab02.models.Rating
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import it.polito.mad.lab02.R
import it.polito.mad.lab02.models.Profile
import it.polito.mad.lab02.models.TimeSlot
import it.polito.mad.lab02.viewmodels.MainActivityViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt


class RateSomeoneFragment : Fragment(R.layout.fragment_rate_someone) {

    private val vm by activityViewModels<MainActivityViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val isLoggedUserPublisher = arguments?.getBoolean("isLoggedUserPublisher")
        val profileRatedIncoming = arguments?.getString("profileRated")
        val timeslotRatedJson = arguments?.getString("timeslotRated")
        val timeslotRated = Gson().fromJson(timeslotRatedJson, TimeSlot::class.java)

        val rated = view.findViewById<TextView>(R.id.userRated)
        val ratingBar = view.findViewById<RatingBar>(R.id.ratingbar)
        val comment = view.findViewById<EditText>(R.id.commentEditText)
        val button = view.findViewById<Button>(R.id.button3)

        lateinit var profileRated: Profile
        lateinit var profileRater: Profile


        vm.setRatingsListenerByTimeslotId(timeslotRated)
        vm.timeslotRatings.observe(viewLifecycleOwner){ rating ->
            Log.d("mytaggg", "fragment: "+rating.toString())

        }


        vm.profile.observe(viewLifecycleOwner){profile ->
            profileRater = profile
        }

        if(isLoggedUserPublisher == false){
            profileRated = Gson().fromJson(profileRatedIncoming, Profile::class.java)
            rated.text = profileRated.nickname
        }else{
            vm.setUserListenerByUserUid(profileRatedIncoming!!)
            vm.userProfile.observe(viewLifecycleOwner){ profile ->
                profileRated = profile
                rated.text = profileRated.nickname
            }
        }

        var stars = 0

        ratingBar.setOnRatingBarChangeListener { ratingBar, fl, b ->
            stars = fl.roundToInt()
        }

        button.setOnClickListener{
            var commentString = comment.text.toString()
            val date = SimpleDateFormat("yyyy/MM/dd").format(Calendar.getInstance().time)
            val timeSlot = TimeSlot("","","","","","","","",profileRater!!, "", "", emptyList())

            val newRating = Rating(profileRated, profileRater, stars, commentString, date, timeSlot)
            vm.postRating(newRating)

            view.findNavController().navigateUp()
            onBackPressed()
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
            else -> super.onOptionsItemSelected(item)
        }

    }

    private fun onBackPressed(){
        val runnable = Runnable {
            // useful to call interaction with viewModel
            vm.removeUserProfileListener()
        }
        // Perform persistence changes after 250 millis
        Handler().postDelayed(runnable, 250)
    }




}