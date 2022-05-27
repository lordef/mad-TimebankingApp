package it.polito.mad.lab02.fragments.profile

import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import coil.load
import it.polito.mad.lab02.R
import it.polito.mad.lab02.Utils
import it.polito.mad.lab02.databinding.FragmentShowProfileBinding
import it.polito.mad.lab02.viewmodels.MainActivityViewModel

class ShowProfileFragment : Fragment() {

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
        setHasOptionsMenu(true)

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
        val balance = binding.balanceValueTextView


        vm.profile.observe(viewLifecycleOwner) { profile ->
            // update UI
            profileImageUri = profile.imageUri
            profileImage.load(Uri.parse(profileImageUri))
            //profileImage.setImageURI(Uri.parse(profileImageUri))
            fullName.text = profile.fullName
            nickname.text = profile.nickname
            email.text = profile.email
            location.text = profile.location
            skills.text = profile.skills.map { s -> s.split("/").last() }.joinToString(", ")
            description.text = profile.description
            balance.text = profile.balance.toString() + " m"

            userUid = profile.uid
            vm.setRatingNumberByUserUid(userUid)

            vm.ratingNumber.observe(viewLifecycleOwner){ avgRatingNum ->
                ratingValue.text = avgRatingNum.toString()
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
}