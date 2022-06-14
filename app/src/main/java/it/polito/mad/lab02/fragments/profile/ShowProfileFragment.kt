package it.polito.mad.lab02.fragments.profile

import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import coil.load
import it.polito.mad.lab02.R
import it.polito.mad.lab02.Utils
import it.polito.mad.lab02.Utils.minutesInHoursAndMinutesString
import it.polito.mad.lab02.databinding.FragmentShowProfileBinding
import it.polito.mad.lab02.models.Profile
import it.polito.mad.lab02.viewmodels.MainActivityViewModel

class ShowProfileFragment : Fragment() {

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
        setHasOptionsMenu(true)

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


        vm.profile.observe(viewLifecycleOwner) { profile ->
            // update UI
            setUIFieldsByProfile(profile)

            vm.setRatingNumberListenerByUserUid(userUid)

            vm.ratingNumber.observe(viewLifecycleOwner){ avgRatingNum ->
                ratingValue.text = avgRatingNum.toString().substring(0,3)
            }
        }

        ratingCard.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("userUid", userUid)
            it.findNavController()
                .navigate(
                    R.id.action_nav_profile_to_ratingsFragment,
                    bundle
                )
        }

        val callback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                view.findNavController().navigateUp()
                onBackPressed()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.pencil_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.editItem -> {
                Toast.makeText(this.context, "Edit Profile selected", Toast.LENGTH_SHORT)
                    .show()
                view?.let {
                    val bundle = Bundle()
                    val skills = binding.skillTextView.text.toString()
                    bundle.putString("skills", skills)
                    Navigation.findNavController(it).navigate(
                        R.id.action_nav_profile_to_editProfileFragment, bundle
                    )
                }

                true
            }
            /*
            android.R.id.home -> {
                findNavController().popBackStack(R.id.nav_advertisement,false)
                true
            }
             */
            else -> super.onOptionsItemSelected(item)
        }

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


    private fun onBackPressed(){
        val runnable = Runnable {
            // useful to call interaction with viewModel
            vm.removeRatingNumberListener()
        }
        // Perform persistence changes after 250 millis
        Handler().postDelayed(runnable, 250)
    }
}