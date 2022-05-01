package it.polito.mad.lab02

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.webkit.WebChromeClient.FileChooserParams.parseResult
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.google.gson.Gson
import it.polito.mad.lab02.databinding.FragmentEditProfileBinding
import it.polito.mad.lab02.databinding.FragmentShowProfileBinding
import it.polito.mad.lab02.models.Profile
import it.polito.mad.lab02.viewmodels.ShowProfileViewModel
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.Logger
import kotlin.concurrent.fixedRateTimer


class EditProfileFragment : Fragment() {
    private var _binding: FragmentEditProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val vm by viewModels<ShowProfileViewModel>()

    /* Variables for CAMERA */
    private val MY_CAMERA_PERMISSION_CODE = 100
    private var imgUri: Uri =
        Uri.parse("android.resource://it.polito.mad.lab02/drawable/profile_image")
    private var imgUriOld: Uri = Uri.parse("")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val profileViewModel =
            ViewModelProvider(this).get(ShowProfileViewModel::class.java)

        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
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

    override fun onViewCreated(view : View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Listener to load new photo on click
        //val profileImageButton = binding.editProfileImageButton
        //profileImageButton.setOnClickListener { onButtonClickEvent(profileImageButton) }

        //Retrieve profile info from ShowProfileFragment
        getProfileInfoFromShowProfileFragment()

        val callback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                val bundle = showProfile()
                view?.let {
                    setFragmentResult("12", bundle)
                    //Navigation.findNavController(it).previousBackStackEntry?.savedStateHandle?.set("JSON", bundle)
                    Navigation.findNavController(it).popBackStack()
                    /*
                        Navigation.findNavController(it).navigate(
                            R.id.action_nav_profile_to_editProfileFragment,
                            bundle
                        )
                        */
                }
                //Navigation.findNavController(view).navigate(R.id.action_timeSlotEditFragment_to_timeSlotDetailsFragment, bundle)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            /*
            R.id.editItem -> {
                Toast.makeText(this.context, "Edit Profile selected", Toast.LENGTH_SHORT)
                    .show()
                val bundle = showProfile()
                view?.let {
                    Navigation.findNavController(it).previousBackStackEntry?.savedStateHandle?.set("JSON", bundle)
                    Navigation.findNavController(it).popBackStack()
                /*
                    Navigation.findNavController(it).navigate(
                        R.id.action_nav_profile_to_editProfileFragment,
                        bundle
                    )
                    */
                }

                true
            }
            */
            android.R.id.home -> {
                val bundle = showProfile()
                view?.let {
                    setFragmentResult("12", bundle)
                    Navigation.findNavController(it).popBackStack()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

    private fun showProfile(): Bundle {
        val bundle = Bundle()
        val profileJson = Gson().toJson(editProfile())
        bundle.putString("JSON", profileJson)

        return bundle
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getProfileInfoFromShowProfileFragment(){

        val profileInfo = arguments?.getString("JSON") ?: return
        val profileInfoString = JSONObject(profileInfo).toString()
        val profileInfoDetails = Gson().fromJson(profileInfoString, Profile::class.java)

        val profileImage = view?.findViewById<ImageView>(R.id.editProfileImageView)
        val fullNameEditText = view?.findViewById<TextView>(R.id.fullNameEditText)
        val nickNameEditText = view?.findViewById<TextView>(R.id.nicknameEditText)
        val emailEditText = view?.findViewById<TextView>(R.id.emailEditText)
        val locationEditText = view?.findViewById<TextView>(R.id.locationEditText)
        val skillsEditText = view?.findViewById<TextView>(R.id.skillEditText)
        val descriptionEditText = view?.findViewById<TextView>(R.id.descriptionEditText)

        profileImage?.setImageURI(Uri.parse(profileInfoDetails.imageUri))
        fullNameEditText?.text = profileInfoDetails.fullName
        nickNameEditText?.text = profileInfoDetails.nickname
        emailEditText?.text = profileInfoDetails.email
        locationEditText?.text = profileInfoDetails.location
        skillsEditText?.text = profileInfoDetails.skills
        descriptionEditText?.text = profileInfoDetails.description

    }

    private fun editProfile() : Profile{
        val fullNameEditText = view?.findViewById<EditText>(R.id.fullNameEditText)
        val nicknameEditText = view?.findViewById<EditText>(R.id.nicknameEditText)
        val emailEditText = view?.findViewById<TextView>(R.id.emailEditText)
        val locationEditText = view?.findViewById<TextView>(R.id.locationEditText)
        val skillEditText = view?.findViewById<TextView>(R.id.skillEditText)
        val descriptionEditText = view?.findViewById<EditText>(R.id.descriptionEditText)
        val obj = Profile(
            "",
            fullNameEditText?.text.toString(),
            nicknameEditText?.text.toString(),
            emailEditText?.text.toString(),
            locationEditText?.text.toString(),
            skillEditText?.text.toString(),
            descriptionEditText?.text.toString()
        )
        vm.updateProfile(obj)
        return obj
    }

}

