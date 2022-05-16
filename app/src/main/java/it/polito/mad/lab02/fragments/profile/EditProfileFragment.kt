package it.polito.mad.lab02.fragments.profile

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
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
import androidx.core.content.FileProvider
import androidx.core.content.PermissionChecker
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.core.view.get
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import it.polito.mad.lab02.R
import it.polito.mad.lab02.Utils
import it.polito.mad.lab02.databinding.FragmentEditProfileBinding
import it.polito.mad.lab02.models.Profile
import it.polito.mad.lab02.viewmodels.ShowProfileViewModel
import it.polito.mad.lab02.viewmodels.SkillListViewModel
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


class EditProfileFragment : Fragment() {
    private var _binding: FragmentEditProfileBinding? = null
    private var profileImageUri = "android.resource://it.polito.mad.lab02/drawable/profile_image"

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val vm by activityViewModels<ShowProfileViewModel>()
    private val vm1 by activityViewModels<SkillListViewModel>()

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
            Utils.divideDisplayInPortion(
                firstLayout,
                secondLayer,
                resources.configuration.orientation
            )
        }

        return root
    }

    private var columnCount = 1
    var listOfSkills = String()

    private var orsk = String()
    var firstTime = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        orsk = arguments?.getString("skills").toString().replace(",", "")

        val count = vm.profile.value?.skills?.split(" ")?.size

        var i = 0
        while(i < count!!){
            if(i == 0) listOfSkills += vm.profile.value?.skills?.split(" ")?.get(i)
            else listOfSkills = listOfSkills + " " + vm.profile.value?.skills?.split(" ")?.get(i)
            ++i
            }

        val profileImageButton = view.findViewById<ImageButton>(R.id.editProfileImageButton)
        profileImageButton.setOnClickListener { onButtonClickEvent(profileImageButton) }

        //Retrieve profile info from ShowProfileFragment
        getProfileInfoFromShowProfileFragment()

        var skillList = listOfSkills

        var skills = resources.getStringArray(R.array.skillsList)

        val skillsText = view.findViewById<TextView>(R.id.skillEditText)
        val addSkillsButton = view.findViewById<Button>(R.id.addSkill)
        val predefinedSkillsButton = view.findViewById<TextView>(R.id.predefinedSkills)
        predefinedSkillsButton.setOnClickListener {
            val dialog = this.layoutInflater.inflate(R.layout.dialog_skills, null)
            val builder = AlertDialog.Builder(this.context).setView(dialog)


            val skillsPicker = dialog.findViewById<NumberPicker>(R.id.skillsPicker)
            skillsPicker.minValue = 0
            skillsPicker.maxValue = skills.size - 1
            skillsPicker.displayedValues = skills
            skillsPicker.wrapSelectorWheel = false


            var temp = skills[0]
            skillsPicker.setOnValueChangedListener(NumberPicker.OnValueChangeListener { _, _, newVal ->

                temp = skills[newVal]
            })


            val alertDialog = builder.show()

            val button = dialog.findViewById<Button>(R.id.button)
            button.setOnClickListener {
                skillsText.text = temp
                alertDialog.dismiss()
            }

        }

        addSkillsButton.setOnClickListener {
            if (skillsText.text.toString() == null || skillsText.text.toString() == "")
                Toast.makeText(this.context, "Insert a skill", Toast.LENGTH_SHORT).show()
            else if (skillsText.text.toString().split(" ").size != 1)
                Toast.makeText(this.context, "Skill cannot contain spaces", Toast.LENGTH_SHORT).show()
            else if (skillList.split(" ").contains(skillsText.text.toString()))
                Toast.makeText(this.context, "Skill already existent", Toast.LENGTH_SHORT).show()
            else{

                val count = vm.profile.value?.skills?.split(" ")?.size

                var i = 0
                skillList = String()
                while(i < count!!){
                    if(i == 0) skillList += vm.profile.value?.skills?.split(" ")?.get(i)
                    else skillList = skillList + " " + vm.profile.value?.skills?.split(" ")?.get(i)
                    ++i
                }
                if (skillList != "") skillList = skillList + " " + skillsText.text.toString().toLowerCase()
                else skillList += skillsText.text.toString().toLowerCase()
                vm.addSkill(skillList)
                listOfSkills = skillList
                skillsText.text = ""

            }

        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.skillList)

        vm.profile.observe(viewLifecycleOwner) { profile ->
            val listOfSkills = profile?.skills?.split(" ")?.toMutableList()
            if (recyclerView is RecyclerView) {
                with(recyclerView) {
                    layoutManager = when {
                        columnCount <= 1 -> LinearLayoutManager(context)
                        else -> GridLayoutManager(context, columnCount)
                    }
                    if(listOfSkills?.contains("") != true)
                        adapter = SkillRecyclerViewAdapter(listOfSkills!!)
                }
            }
        }


        //Listener to load new photo on click
        //val profileImageButton = binding.editProfileImageButton
        //profileImageButton.setOnClickListener { onButtonClickEvent(profileImageButton) }


        val profileImage = view.findViewById<ImageView>(R.id.editProfileImageView)
        savedInstanceState?.let {
            imgUri = Uri.parse(savedInstanceState.getString("imgUri"))
            profileImage?.load(imgUri)
            //profileImage?.setImageURI(imgUri)
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                editProfile()
                view.let {
                    Navigation.findNavController(it).popBackStack()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.commitItem -> {
                editProfile()
                view?.let {
                    Navigation.findNavController(it).popBackStack()
                }
                true
            }
            android.R.id.home -> {
                editProfile()
                view?.let {
                    Navigation.findNavController(it).popBackStack()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getProfileInfoFromShowProfileFragment() {
        val profileImage = view?.findViewById<ImageView>(R.id.editProfileImageView)
        val fullNameEditText = view?.findViewById<TextView>(R.id.fullNameEditText)
        val nickNameEditText = view?.findViewById<TextView>(R.id.nicknameEditText)
        val emailEditText = view?.findViewById<TextView>(R.id.emailEditText)
        val locationEditText = view?.findViewById<TextView>(R.id.locationEditText)
        val skillsEditText = view?.findViewById<TextView>(R.id.skillEditText)
        val descriptionEditText = view?.findViewById<TextView>(R.id.descriptionEditText)

        vm.profile.observe(viewLifecycleOwner) { profile ->
            imgUri = Uri.parse(profile?.imageUri)
            imgUriOld = imgUri
            profileImage?.load(imgUri)
            fullNameEditText?.text = profile?.fullName
            nickNameEditText?.text = profile?.nickname
            emailEditText?.text = profile?.email
            locationEditText?.text = profile?.location
            listOfSkills = profile?.skills.toString()
            if(listOfSkills == "") listOfSkills = String()
            descriptionEditText?.text = profile?.description
        }
    }

    private fun editProfile() {

        val fullNameEditText = view?.findViewById<EditText>(R.id.fullNameEditText)
        val nicknameEditText = view?.findViewById<EditText>(R.id.nicknameEditText)
        val emailEditText = view?.findViewById<TextView>(R.id.emailEditText)
        val locationEditText = view?.findViewById<TextView>(R.id.locationEditText)
//        val skillEditText = view?.findViewById<TextView>(R.id.skillEditText)
        val descriptionEditText = view?.findViewById<EditText>(R.id.descriptionEditText)


        var skillList = listOfSkills



        val skillsToDelete = getSkillsToDelete(orsk, skillList)
        val skillsToAdd = getSkillsToAdd(orsk, skillList)




        vm1.addSkill(skillsToAdd)
        vm1.deleteSkill(skillsToDelete)


        if (imgUri == imgUriOld) {
            val obj = Profile(
                imgUri.toString(),
                fullNameEditText?.text.toString(),
                nicknameEditText?.text.toString(),
                emailEditText?.text.toString(),
                locationEditText?.text.toString(),
                skillList,
                descriptionEditText?.text.toString(),
                FirebaseAuth.getInstance().currentUser?.uid!!
            )
            vm.updateProfile(obj)
        } else {
            val imageEditText = view?.findViewById<ImageView>(R.id.editProfileImageView)
            imageEditText?.isDrawingCacheEnabled = true
            imageEditText?.buildDrawingCache()
            val bitmap = (imageEditText?.drawable as BitmapDrawable).bitmap
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            deleteLocalImage()
            deleteOldImage()
            // Create a storage reference from our app
            val storageRef = Firebase.storage.reference
            // Create a reference to "images/imgUri"
            val imagesRef = storageRef.child("images/${imgUri.lastPathSegment!!}")
            imagesRef.putBytes(data).addOnSuccessListener {
                imagesRef.downloadUrl.addOnSuccessListener {
                    val obj = Profile(
                        it.toString(),
                        fullNameEditText?.text.toString(),
                        nicknameEditText?.text.toString(),
                        emailEditText?.text.toString(),
                        locationEditText?.text.toString(),
                        listOfSkills,
                        descriptionEditText?.text.toString(),
                        FirebaseAuth.getInstance().currentUser?.uid!!
                    )
                    vm.updateProfile(obj)
                }
            }
        }
        firstTime = true
    }


    private fun getSkillsToDelete(originalSkills: String, finalSkills: String): List<String> {
        var os = originalSkills.split(" ").toMutableList()
        var fs = finalSkills.split(" ").toMutableList()
        return os.filter { !fs.contains(it) }
    }
    private fun getSkillsToAdd(originalSkills: String, finalSkills: String): List<String> {
        var os = originalSkills.split(" ").toMutableList()
        var fs = finalSkills.split(" ").toMutableList()
        return fs.filter { !os.contains(it) }
    }



    private fun onButtonClickEvent(sender: View?) {
        registerForContextMenu(sender!!)
        requireActivity().openContextMenu(sender)
        unregisterForContextMenu(sender)
    }

    /* Context menu for Camera */
    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)

        val inflater: MenuInflater = requireActivity().menuInflater
        menu.setHeaderTitle("Choose your option")
        inflater.inflate(R.menu.camera_menu, menu)
    }

    /* Options for Camera */
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onContextItemSelected(item: MenuItem): Boolean {
        //val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
        return when (item.itemId) {
            R.id.selectImageOption -> {
                Toast.makeText(
                    requireActivity(),
                    "Select an image from the phone gallery",
                    Toast.LENGTH_SHORT
                )
                    .show()
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                getImageFromGallery.launch(Intent.createChooser(intent, ""))
                true
            }
            R.id.useCameraOption -> {
                Toast.makeText(
                    requireActivity(),
                    "Use the camera to take a picture",
                    Toast.LENGTH_SHORT
                ).show()
                if (checkSelfPermission(
                        requireActivity(),
                        Manifest.permission.CAMERA
                    ) != PermissionChecker.PERMISSION_GRANTED
                ) {
                    requestPermissions(
                        arrayOf(Manifest.permission.CAMERA),
                        MY_CAMERA_PERMISSION_CODE
                    )
                } else {
                    if (checkSelfPermission(
                            requireActivity(),
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ) != PermissionChecker.PERMISSION_GRANTED
                    ) {
                        requestPermissions(
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            MY_CAMERA_PERMISSION_CODE + 1
                        )
                    } else {
                        if (checkSelfPermission(
                                requireActivity(),
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            ) != PermissionChecker.PERMISSION_GRANTED
                        ) {
                            requestPermissions(
                                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                MY_CAMERA_PERMISSION_CODE + 2
                            )
                        } else {
                            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, setImageUri())
                            getImageFromCamera.launch(intent)
                        }
                    }
                }
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }

    /* Get permissions to use the camera */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_CAMERA_PERMISSION_CODE || requestCode == MY_CAMERA_PERMISSION_CODE + 1 || requestCode == MY_CAMERA_PERMISSION_CODE + 2) {
            if (grantResults[0] == PermissionChecker.PERMISSION_GRANTED) {
                Toast.makeText(requireActivity(), "Permission granted", Toast.LENGTH_LONG).show()
                super.onRequestPermissionsResult(
                    requestCode + 1,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    grantResults
                )
                super.onRequestPermissionsResult(
                    requestCode + 2,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    grantResults
                )
                //dispatchTakePictureIntent()
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, setImageUri())
                getImageFromCamera.launch(intent)
            } else {
                Toast.makeText(requireActivity(), "Permission denied", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun clonePic(uri: Uri): Uri {
        val resultUri = setImageUri()
        BitmapFactory.decodeStream(requireActivity().contentResolver.openInputStream(uri))
            .also { bitmap ->
                val fos: OutputStream? =
                    requireActivity().contentResolver.openOutputStream(resultUri)

                // Use the compress method on the BitMap object to write image to the OutputStream
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            }
        return resultUri
    }


    @Throws(IOException::class)
    private fun setImageUri(): Uri {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File =
            requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        val uri = File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )
        val photoURI: Uri = FileProvider.getUriForFile(
            requireActivity(),
            "it.polito.mad.lab02.provider",
            uri
        )
        imgUri = photoURI
        return photoURI
    }

    private fun deleteLocalImage(): Boolean {
        if (imgUri != Uri.parse("android.resource://it.polito.mad.lab02/drawable/profile_image") && imgUriOld != imgUri) {

            val file = File(
                requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                imgUri.lastPathSegment!!
            )
            if (file.exists()) {
                return file.delete()
            }
        }
        return false
    }

    private fun deleteOldImage() {

        val storageRef = Firebase.storage.reference
        // Create a reference to "images/imgUri"
        val imagesRef = storageRef.child("${imgUriOld.lastPathSegment!!}")
        imagesRef.delete().addOnSuccessListener {
            Log.d("MYTAG", "SUCCESS: images/${imgUriOld.lastPathSegment!!}")
        }
            .addOnFailureListener {
                Log.d("MYTAG", "FAILURE: images/${imgUriOld.lastPathSegment!!}")
            }
    }

    private fun rotateImage(source: Bitmap, angle: Float): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height,
            matrix, true
        )
    }

    // Receiver For Camera (updated version of startActivityForResult)
    private val getImageFromCamera =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val imageFile = File(
                        requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                        imgUri.lastPathSegment!!
                    )
                    val ei = ExifInterface(imageFile)
                    val orientation: Int = ei.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED
                    )

                    var rotatedBitmap: Bitmap? = null
                    val bitmap = BitmapFactory.decodeStream(
                        requireActivity().contentResolver.openInputStream(imgUri)
                    )
                    rotatedBitmap = when (orientation) {
                        ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90f)
                        ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180f)
                        ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270f)
                        ExifInterface.ORIENTATION_NORMAL -> bitmap
                        else -> bitmap
                    }
                    val fos: OutputStream? =
                        requireActivity().contentResolver.openOutputStream(imgUri)
                    rotatedBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                }
                val editProfileImageView =
                    requireView().findViewById<ImageView>(R.id.editProfileImageView)
                Utils.setUriInImageView(
                    editProfileImageView,
                    imgUri,
                    requireActivity().contentResolver
                )
            }
        }

    // Receiver For Gallery
    private val getImageFromGallery =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                if (it != null) {
                    println("Test ${parseResult(Activity.RESULT_OK, it.data)!![0]!!}")
                    imgUri = clonePic(parseResult(Activity.RESULT_OK, it.data)?.get(0)!!)
                    val editProfileImageView =
                        requireView().findViewById<ImageView>(R.id.editProfileImageView)
                    Utils.setUriInImageView(
                        editProfileImageView,
                        imgUri,
                        requireActivity().contentResolver
                    )
                }
            }
        }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("imgUri", imgUri.toString())
    }
}

