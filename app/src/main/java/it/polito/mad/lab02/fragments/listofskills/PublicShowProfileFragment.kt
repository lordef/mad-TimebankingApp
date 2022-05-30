package it.polito.mad.lab02.fragments.listofskills

import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import coil.load
import it.polito.mad.lab02.R
import com.google.gson.Gson
import it.polito.mad.lab02.Utils
import it.polito.mad.lab02.databinding.FragmentShowProfileBinding
import it.polito.mad.lab02.models.Profile
import it.polito.mad.lab02.viewmodels.MainActivityViewModel

class PublicShowProfileFragment : Fragment() {

    private var _binding: FragmentShowProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val vm by activityViewModels<MainActivityViewModel>()

    private var profileImageUri = "android.resource://it.polito.mad.lab02/drawable/profile_image"

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

        var userUid = ""
        val profileImage = binding.profileImageView
        val fullName = binding.fullNameTextView
        val nickname = binding.nicknameTextView
        val email = binding.emailTextView
        val location = binding.locationTextView
        val skills = binding.skillTextView
        val description = binding.descriptionTextView
        val ratingCard = binding.ratingCardView
        val ratingValue = binding.ratingValueTextView


        val id = arguments?.getString("id")
        val origin = arguments?.getString("origin")
        if (id != null) {
            if (origin == "interests") {
                setHasOptionsMenu(false)
                vm.requesterChatList.observe(viewLifecycleOwner) {
                    val ts = it.filter { t -> t.timeSlot.id == id }[0].timeSlot
                    // update UI
                    profileImageUri = ts.userProfile.imageUri
                    profileImage.load(Uri.parse(profileImageUri))
                    //profileImage.setImageURI(Uri.parse(profileImageUri))
                    fullName.text = ts.userProfile.fullName
                    nickname.text = ts.userProfile.nickname
                    email.text = ts.userProfile.email
                    location.text = ts.userProfile.location
                    skills.text = ts.userProfile.skills.joinToString(", ")
                    description.text = ts.userProfile.description

                    userUid = ts.userProfile.uid
                    vm.setRatingNumberListenerByUserUid(userUid)
                    vm.ratingNumber.observe(viewLifecycleOwner) { avgRatingNum ->
                        ratingValue.text = avgRatingNum.toString()
                    }
                }


            }

            if (origin == null) {
                setHasOptionsMenu(false)
                vm.timeslotList.observe(viewLifecycleOwner) {
                    val ts = it.filter { t -> t.id == id }[0]

                    // update UI
                    profileImageUri = ts.userProfile.imageUri
                    profileImage.load(Uri.parse(profileImageUri))
                    //profileImage.setImageURI(Uri.parse(profileImageUri))
                    fullName.text = ts.userProfile.fullName
                    nickname.text = ts.userProfile.nickname
                    email.text = ts.userProfile.email
                    location.text = ts.userProfile.location
                    skills.text = ts.userProfile.skills.joinToString(", ")
                    description.text = ts.userProfile.description

                    userUid = ts.userProfile.uid
                    vm.setRatingNumberListenerByUserUid(userUid)
                    vm.ratingNumber.observe(viewLifecycleOwner) { avgRatingNum ->
                        ratingValue.text = avgRatingNum.toString()
                    }
                }
            }
        }

        //TODO: vedere come funziona la navigation, non funiona il pulsante indietro in basso
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
            profileImageUri = userObj.imageUri
            profileImage.load(Uri.parse(profileImageUri))
            //profileImage.setImageURI(Uri.parse(profileImageUri))
            fullName.text = userObj.fullName
            nickname.text = userObj.nickname
            email.text = userObj.email
            location.text = userObj.location
            skills.text = userObj.skills.joinToString(", ")
            description.text = userObj.description
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
}