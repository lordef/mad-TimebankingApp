package it.polito.mad.lab02.fragments.listofskills

import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import coil.load
import it.polito.mad.lab02.R
import com.google.gson.Gson
import it.polito.mad.lab02.Utils
import it.polito.mad.lab02.Utils.minutesInHoursAndMinutesString
import it.polito.mad.lab02.databinding.FragmentShowProfileBinding
import it.polito.mad.lab02.models.Profile
import it.polito.mad.lab02.models.TimeSlot
import it.polito.mad.lab02.viewmodels.MainActivityViewModel

class PublicShowProfileFragment : Fragment() {

    private var _binding: FragmentShowProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val vm by activityViewModels<MainActivityViewModel>()

    private var profileImageUri = "android.resource://it.polito.mad.lab02/drawable/profile_image"

    private lateinit var userUid : String
    private lateinit var profileImage : ImageView
    private lateinit var fullName : TextView
    private lateinit var nickname : TextView
    private lateinit var email : TextView
    private lateinit var location : TextView
    private lateinit var skills : TextView
    private lateinit var description : TextView
    private lateinit var ratingCard : CardView
    private lateinit var ratingValue : TextView
    private lateinit var balance : TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentShowProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        /* Divide screen in 1/3 and 2/3 */
        val firstLayout = binding.upperConstraintLayout
        val secondLayer =
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
                binding.mainLinearLayout
            else
                binding.mainScrollView

        if (secondLayer != null) {
            Utils.divideDisplayInPortion(
                firstLayout,
                secondLayer,
                resources.configuration.orientation
            )
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileImage = binding.profileImageView
        fullName = binding.fullNameTextView
        nickname = binding.nicknameTextView
        email = binding.emailTextView
        location = binding.locationTextView
        skills = binding.skillTextView
        description = binding.descriptionTextView
        ratingCard = binding.ratingCardView
        ratingValue = binding.ratingValueTextView
        balance = binding.balanceValueTextView


        val id = arguments?.getString("id")
        val origin = arguments?.getString("origin")

        if (id != null) {
            if (origin == "interests") {
                setHasOptionsMenu(false)
                vm.requesterChatList.observe(viewLifecycleOwner) {
                    val ts = it.filter { t -> t.timeSlot.id == id }[0].timeSlot
                    // update UI
                    setUIFieldsByProfile(ts.userProfile)

                    vm.setRatingNumberListenerByUserUid(userUid)
                    vm.ratingNumber.observe(viewLifecycleOwner) { avgRatingNum ->
                        ratingValue.text = avgRatingNum.toString().substring(0,3)
                    }
                }
            }
            if (origin == "assigned") {
                setHasOptionsMenu(false)
                vm.myAssignedTimeSlotList.observe(viewLifecycleOwner) {
                    val ts = it.filter { t -> t.id == id }[0]
                    // update UI
                    setUIFieldsByProfile(ts.userProfile)

                    vm.setRatingNumberListenerByUserUid(userUid)
                    vm.ratingNumber.observe(viewLifecycleOwner) { avgRatingNum ->
                        ratingValue.text = avgRatingNum.toString().substring(0,3)
                    }
                }
            }
            if (origin == "accepted") {
                setHasOptionsMenu(false)
                vm.loggedUserTimeSlotList.observe(viewLifecycleOwner) {
                    val ts = it.filter { t -> t.id == id }[0]
                    // update UI
                    setUIFieldsByProfile(ts.userProfile)

                    vm.setRatingNumberListenerByUserUid(userUid)
                    vm.ratingNumber.observe(viewLifecycleOwner) { avgRatingNum ->
                        ratingValue.text = avgRatingNum.toString().substring(0,3)
                    }
                }
            }

            if(origin == "ratings"){
                val profileRaterJson = arguments?.getString("profileRater")
                val profileRater = Gson().fromJson(profileRaterJson, Profile::class.java)
                setUIFieldsByProfile(profileRater)

                vm.setRatingNumberListenerByUserUid(userUid)
                vm.ratingNumber.observe(viewLifecycleOwner) { avgRatingNum ->
                    ratingValue.text = avgRatingNum.toString().substring(0,3)
                }
            }

            if (origin == null) {
                setHasOptionsMenu(false)
                vm.timeslotList.observe(viewLifecycleOwner) {
                    val ts = it.filter { t -> t.id == id }[0]
                    // update UI
                    setUIFieldsByProfile(ts.userProfile)

                    vm.setRatingNumberListenerByUserUid(userUid)
                    vm.ratingNumber.observe(viewLifecycleOwner) { avgRatingNum ->
                        ratingValue.text = avgRatingNum.toString().substring(0,3)
                    }
                }
            }
        }

        ratingCard.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("userUid", userUid)
            it.findNavController()
                .navigate(
                    R.id.action_publicShowProfileFragment_to_ratingsFragment,
                    bundle
                )
        }

        val user = arguments?.getString("user")
        if (user != null) {
            setHasOptionsMenu(false)
            val userObj = Gson().fromJson(user, Profile::class.java)
            // update UI
            setUIFieldsByProfile(userObj)
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                view.findNavController().navigateUp()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUIFieldsByProfile(profile: Profile){
        userUid = profile.uid

        profileImageUri = profile.imageUri
        profileImage.load(Uri.parse(profileImageUri))

        fullName.text = profile.fullName
        nickname.text = profile.nickname
        email.text = profile.email
        location.text = profile.location
        skills.text = profile.skills.joinToString(", ")
        description.text = profile.description
        balance.text = minutesInHoursAndMinutesString(profile.balance)
    }
}