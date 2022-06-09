package it.polito.mad.lab02.fragments.listofskills

import android.net.Uri
import it.polito.mad.lab02.models.Rating
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import coil.load
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

    private var isLoggedUserPublisherRateFrag : Boolean? = false

    private var fragmentLabel = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        val isLoggedUserPublisher = arguments?.getBoolean("isLoggedUserPublisher")
        isLoggedUserPublisherRateFrag = isLoggedUserPublisher
        val profileRatedIncoming = arguments?.getString("profileRated")
        val timeslotRatedJson = arguments?.getString("timeslotRated")
        val timeslotRated = Gson().fromJson(timeslotRatedJson, TimeSlot::class.java)

        val rated = view.findViewById<TextView>(R.id.userRated)
        val ratedImageView = view.findViewById<ImageView>(R.id.imageView3)

        val yourReview = view.findViewById<ConstraintLayout>(R.id.constraintLayoutFirstReview)
        val theirReview = view.findViewById<ConstraintLayout>(R.id.constraintLayoutSecondReview)

        val yourReviewEmpty = view.findViewById<ConstraintLayout>(R.id.giveARatingConstraintLayout)
        val theirReviewEmpty = view.findViewById<TextView>(R.id.noRatingReceivedTextView)

        lateinit var otherProfile: Profile //their profile
        lateinit var ownerProfile: Profile //your profile

        fragmentLabel = "Ratings about " + timeslotRated.title
        (activity as AppCompatActivity?)?.supportActionBar?.title = fragmentLabel

        vm.profile.observe(viewLifecycleOwner){profile ->
            ownerProfile = profile
        }

        if(isLoggedUserPublisher == false){
            otherProfile = Gson().fromJson(profileRatedIncoming, Profile::class.java)
            rated.text = otherProfile.nickname
            ratedImageView.load(Uri.parse(otherProfile.imageUri))
        }else{
            vm.setUserListenerByUserUid(profileRatedIncoming!!)
            vm.userProfile.observe(viewLifecycleOwner){ profile ->
                otherProfile = profile
                rated.text = otherProfile.nickname
                ratedImageView.load(Uri.parse(otherProfile.imageUri))
            }
        }

        vm.setRatingsListenerByTimeslot(timeslotRated)
        vm.timeslotRatings.observe(viewLifecycleOwner){ ratings ->

            if(ratings.size == 2){
                yourReviewEmpty.visibility = View.INVISIBLE
                theirReviewEmpty.visibility = View.INVISIBLE
                yourReview.visibility = View.VISIBLE
                theirReview.visibility = View.VISIBLE

                twoRatingsPresent(ratings, ownerProfile)

            }else if(ratings.isEmpty()){
                yourReviewEmpty.visibility = View.VISIBLE
                theirReviewEmpty.visibility = View.VISIBLE
                yourReview.visibility = View.INVISIBLE
                theirReview.visibility = View.INVISIBLE

                giveARating(otherProfile, ownerProfile, timeslotRated)

            }else{
                if(ratings[0].rater == ownerProfile){
                    yourReviewEmpty.visibility = View.INVISIBLE
                    theirReviewEmpty.visibility = View.VISIBLE
                    yourReview.visibility = View.VISIBLE
                    theirReview.visibility = View.INVISIBLE

                    oneRatingPresent(ratings[0], true)

                }else{
                    yourReviewEmpty.visibility = View.VISIBLE
                    theirReviewEmpty.visibility = View.INVISIBLE
                    yourReview.visibility = View.INVISIBLE
                    theirReview.visibility = View.VISIBLE

                    oneRatingPresent(ratings[0], false)
                    giveARating(otherProfile, ownerProfile, timeslotRated)

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
            else -> super.onOptionsItemSelected(item)
        }

    }

    private fun onBackPressed(){
        val runnable = Runnable {
            // useful to call interaction with viewModel
            if(isLoggedUserPublisherRateFrag == true){
                vm.removeUserProfileListener()
            }
            vm.removeRatingsListenerByTimeslot()

        }
        // Perform persistence changes after 250 millis
        Handler().postDelayed(runnable, 250)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        (activity as AppCompatActivity?)?.supportActionBar?.title = fragmentLabel
    }

    private fun giveARating(otherProfile: Profile, ownerProfile: Profile, timeslotRated: TimeSlot){

        val ratingBar = view?.findViewById<RatingBar>(R.id.ratingbar)!!
        val comment = view?.findViewById<EditText>(R.id.commentEditText)!!
        val button = view?.findViewById<Button>(R.id.button3)!!

        var stars = 0

        ratingBar.setOnRatingBarChangeListener { _, fl, _ ->
            stars = fl.roundToInt()
        }

        button.setOnClickListener{
            var commentString = comment.text.toString()
            val date = SimpleDateFormat("yyyy/MM/dd").format(Calendar.getInstance().time)

            val newRating = Rating(otherProfile, ownerProfile, stars, commentString, date, timeslotRated)
            vm.postRating(newRating)

//            view?.findNavController()?.navigateUp()
//            onBackPressed()
        }
    }

    private fun twoRatingsPresent(ratings: List<Rating>, yourProfile: Profile){

        // first review
        val imageFR = view?.findViewById<ImageView>(R.id.imageViewFirstReview)!!
        val nameFR = view?.findViewById<TextView>(R.id.textViewFirstReview)!!
        val ratingBarFR = view?.findViewById<RatingBar>(R.id.ratingBarFirstReview)!!
        val dateFR = view?.findViewById<TextView>(R.id.textViewDateFirstReview)!!
        val commentFR = view?.findViewById<TextView>(R.id.textViewCommentFirstReview)!!

        // second review
        val nameTitleSR = view?.findViewById<TextView>(R.id.textViewUsername)!!
        val imageSR = view?.findViewById<ImageView>(R.id.imageViewSecondReview)!!
        val nameSR = view?.findViewById<TextView>(R.id.textViewSecondReview)!!
        val ratingBarSR = view?.findViewById<RatingBar>(R.id.ratingBarSecondReview)!!
        val dateSR = view?.findViewById<TextView>(R.id.textViewDateSecondReview)!!
        val commentSR = view?.findViewById<TextView>(R.id.textViewCommentSecondReview)!!

        if(ratings[0].rater == yourProfile){

            imageFR.load(Uri.parse(ratings[0].rater.imageUri))
            nameFR.text = ratings[0].rater.nickname
            ratingBarFR.rating = ratings[0].starsNum.toFloat()
            dateFR.text = ratings[0].timestamp
            commentFR.text = ratings[0].comment

            nameTitleSR.text = ratings[1].rater.nickname + "'s review"
            imageSR.load(Uri.parse(ratings[1].rater.imageUri))
            nameSR.text = ratings[1].rater.nickname
            ratingBarSR.rating = ratings[1].starsNum.toFloat()
            dateSR.text = ratings[1].timestamp
            commentSR.text = ratings[1].comment

        }else if(ratings[1].rater == yourProfile){

            imageFR.load(Uri.parse(ratings[1].rater.imageUri))
            nameFR.text = ratings[1].rater.nickname
            ratingBarFR.rating = ratings[1].starsNum.toFloat()
            dateFR.text = ratings[1].timestamp
            commentFR.text = ratings[1].comment

            nameTitleSR.text = ratings[0].rater.nickname+ "'s review"
            imageSR.load(Uri.parse(ratings[0].rater.imageUri))
            nameSR.text = ratings[0].rater.nickname
            ratingBarSR.rating = ratings[0].starsNum.toFloat()
            dateSR.text = ratings[0].timestamp
            commentSR.text = ratings[0].comment

        }
    }

    private fun oneRatingPresent(rating: Rating, yourProfile: Boolean){

        if(yourProfile){  // first review

            val imageFR = view?.findViewById<ImageView>(R.id.imageViewFirstReview)!!
            val nameFR = view?.findViewById<TextView>(R.id.textViewFirstReview)!!
            val ratingBarFR = view?.findViewById<RatingBar>(R.id.ratingBarFirstReview)!!
            val dateFR = view?.findViewById<TextView>(R.id.textViewDateFirstReview)!!
            val commentFR = view?.findViewById<TextView>(R.id.textViewCommentFirstReview)!!

            imageFR.load(Uri.parse(rating.rater.imageUri))
            nameFR.text = rating.rater.nickname
            ratingBarFR.rating = rating.starsNum.toFloat()
            dateFR.text = rating.timestamp
            commentFR.text = rating.comment

        }else{  // second review

            val nameTitleSR = view?.findViewById<TextView>(R.id.textViewUsername)!!
            val imageSR = view?.findViewById<ImageView>(R.id.imageViewSecondReview)!!
            val nameSR = view?.findViewById<TextView>(R.id.textViewSecondReview)!!
            val ratingBarSR = view?.findViewById<RatingBar>(R.id.ratingBarSecondReview)!!
            val dateSR = view?.findViewById<TextView>(R.id.textViewDateSecondReview)!!
            val commentSR = view?.findViewById<TextView>(R.id.textViewCommentSecondReview)!!

            nameTitleSR.text = rating.rater.nickname + "'s review"
            imageSR.load(Uri.parse(rating.rater.imageUri))
            nameSR.text = rating.rater.nickname
            ratingBarSR.rating = rating.starsNum.toFloat()
            dateSR.text = rating.timestamp
            commentSR.text = rating.comment

        }
    }




}