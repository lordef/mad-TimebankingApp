package it.polito.mad.lab02.fragments.listofskills

import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import coil.load
import com.google.gson.Gson
import it.polito.mad.lab02.R
import it.polito.mad.lab02.Utils
import it.polito.mad.lab02.viewmodels.ShowProfileViewModel
import it.polito.mad.lab02.databinding.FragmentShowProfileBinding
import it.polito.mad.lab02.viewmodels.PublicTimeSlotListViewModel

class PublicShowProfileFragment : Fragment() {

    private var _binding: FragmentShowProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val vm1 by activityViewModels<PublicTimeSlotListViewModel>()

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

        val profileImage = binding.profileImageView
        val fullName = binding.fullNameTextView
        val nickname = binding.nicknameTextView
        val email = binding.emailTextView
        val location = binding.locationTextView
        val skills = binding.skillTextView
        val description = binding.descriptionTextView

        val id = arguments?.getString("id")
        if (id != null) {
            setHasOptionsMenu(false)
            vm1.timeslotList.observe(viewLifecycleOwner) {
                val ts = it.filter { t -> t.id == id }[0]
                // update UI
                profileImageUri = ts.userProfile.imageUri
                profileImage.load(Uri.parse(profileImageUri))
                //profileImage.setImageURI(Uri.parse(profileImageUri))
                fullName.text = ts.userProfile.fullName
                nickname.text = ts.userProfile.nickname
                email.text = ts.userProfile.email
                location.text = ts.userProfile.location
                skills.text = ts.userProfile.skills
                description.text = ts.userProfile.description
            }
        }


        val callback = object : OnBackPressedCallback(true){
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