package it.polito.mad.lab02.fragments

import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.google.gson.Gson
import it.polito.mad.lab02.R
import it.polito.mad.lab02.Utils
import it.polito.mad.lab02.viewmodels.ShowProfileViewModel
import it.polito.mad.lab02.databinding.FragmentShowProfileBinding

class ShowProfileFragment : Fragment() {

    private var _binding: FragmentShowProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val vm by viewModels<ShowProfileViewModel>()

    private var profileImageUri = "android.resource://it.polito.mad.lab02/drawable/profile_image"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val profileViewModel =
            ViewModelProvider(this).get(ShowProfileViewModel::class.java)

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
            Utils.divideDisplayInPortion(firstLayout, secondLayer, resources.configuration.orientation)
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        val profileImage = binding.profileImageView
        val fullName = binding.fullNameTextView
        val nickname = binding.nicknameTextView
        val email = binding.emailTextView
        val location = binding.locationTextView
        val skills = binding.skillTextView
        val description = binding.descriptionTextView


        /*
        val callback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                Navigation.findNavController(view).popBackStack(R.id.nav_advertisement,false)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)
        */

        //TODO: trying to retrieve content from ViewModel
        vm.getProfileInfo().observe(viewLifecycleOwner) { profile ->
            // update UI
            profileImageUri = profile.imageUri
            //TODO
            profileImage.setImageURI(Uri.parse(profileImageUri))
            fullName.text = profile.fullName
            nickname.text = profile.nickname
            email.text = profile.email
            location.text = profile.location
            skills.text = profile.skills
            description.text = profile.description

        }


        //TODO: old code to manage
        // Retrieve json object of class TimeSlotClass
        /*
        val pref = this.context?.let { SharedPreference(it) }
        val gson = Gson()
        val json = pref?.getTimeSlotDetails(title)
        if (!json.equals("")) {
            val obj = gson.fromJson(json, TimeSlotDetailsModel::class.java)

            // Put it into the TextViews
            val title = view.findViewById<TextView>(R.id.titleTextView)
            val description = view.findViewById<TextView>(R.id.descriptionTextView)
            val dateTime = view.findViewById<TextView>(R.id.dateTimeTextView)
            val duration = view.findViewById<TextView>(R.id.durationTextView)
            val location = view.findViewById<TextView>(R.id.locationTextView)

            if (obj !== null) {
                title.text = obj.title
                description.text = obj.description
                dateTime.text = obj.dateTime
                duration.text = obj.duration
                location.text = obj.location
            }
        }
         */
    }

    /*
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.pencil_menu, menu)
    }
    */

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.editItem -> {
                Toast.makeText(this.context, "Edit Profile selected", Toast.LENGTH_SHORT)
                    .show()
                val bundle = editProfile()
                view?.let {
                    Navigation.findNavController(it).navigate(
                        R.id.action_nav_profile_to_editProfileFragment,
                        bundle
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

    private fun editProfile(): Bundle {
        val bundle = Bundle()
        val profileJson = Gson().toJson(vm.getProfileInfo().value)
        bundle.putString("JSON", profileJson)

        return bundle
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}