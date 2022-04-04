package it.polito.mad.lab02

import android.Manifest
import android.R.attr.bitmap
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.*
import android.webkit.WebChromeClient.FileChooserParams.parseResult
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import com.google.gson.Gson
import java.io.File
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*


class EditProfileActivity : AppCompatActivity() {

    /* Variables for CAMERA */
    private val MY_CAMERA_PERMISSION_CODE = 100
    private var imgUri: Uri =
        Uri.parse("android.resource://it.polito.mad.lab02/drawable/profile_image")
    private var imgUriOld: Uri = Uri.parse("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        /* Divide screen in 1/3 and 2/3 */
        val firstLayout = findViewById<ConstraintLayout>(R.id.upperConstraintLayout)
        val secondLayer =
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
                findViewById<LinearLayout>(R.id.mainLinearLayout)
            else
                findViewById<ScrollView>(R.id.mainScrollView)

        Utils.divideDisplayInPortion(firstLayout, secondLayer, resources.configuration.orientation)

        val profileImageButton = findViewById<ImageButton>(R.id.editProfileImageButton)
        profileImageButton.setOnClickListener { onButtonClickEvent(profileImageButton) }

        //Retrieve a Bundle object
        val extras: Bundle? = intent.extras
        val showActivityHashMap =
            extras!!.getSerializable("showActivityHashMap") as HashMap<String, String>

        val keyPrefix = "group07.lab2."

        val editProfileImageView = findViewById<ImageView>(R.id.editProfileImageView)
        val editProfileImageUri = showActivityHashMap[keyPrefix + "PROFILE_IMG_URI"]
        imgUri = Uri.parse(editProfileImageUri!!)
        imgUriOld = imgUri
        Utils.setUriInImageView(
            editProfileImageView,
            Uri.parse(editProfileImageUri),
            contentResolver
        )


        val fullNameEditText = findViewById<TextView>(R.id.fullNameEditText)
        fullNameEditText.text = showActivityHashMap.getValue(keyPrefix + "FULL_NAME")

        val nickNameEditText = findViewById<TextView>(R.id.nicknameEditText)
        nickNameEditText.text = showActivityHashMap.getValue(keyPrefix + "NICKNAME")

        val emailEditText = findViewById<TextView>(R.id.emailEditText)
        emailEditText.text = showActivityHashMap.getValue(keyPrefix + "EMAIL")

        val locationEditText = findViewById<TextView>(R.id.locationEditText)
        locationEditText.text = showActivityHashMap.getValue(keyPrefix + "LOCATION")

        val skillsEditText = findViewById<TextView>(R.id.skillEditText)
        skillsEditText.text = showActivityHashMap.getValue(keyPrefix + "SKILLS")

        val descriptionEditText = findViewById<TextView>(R.id.descriptionEditText)
        descriptionEditText.text = showActivityHashMap.getValue(keyPrefix + "DESCRIPTION")

    }

    /* Useful for tick -> once pressed it commit changes */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.commit_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.commitItem -> {
                onBackPressed()
                Toast.makeText(this, "Changes sent", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /* Useful for register a Context Menu - allows sigle click instead of long press */
    private fun onButtonClickEvent(sender: View?) {
        registerForContextMenu(sender)
        openContextMenu(sender)
        unregisterForContextMenu(sender)
    }

    /* Confirm edited fields on back press */
    override fun onBackPressed() {
        commitProfileEdited()

        super.onBackPressed()
    }

    /* Confirm edited fields */
    private fun commitProfileEdited() {
        onSave()

        val i = Intent(this, ShowProfileActivity::class.java)

        //Create a Bundle object
        val extras = Bundle()
        val showActivityHashMap = HashMap<String, String>()

        val keyPrefix = "group07.lab2."

        showActivityHashMap[keyPrefix + "PROFILE_IMG_URI"] = imgUri.toString()

        val fullNameText = findViewById<TextView>(R.id.fullNameEditText).text
        showActivityHashMap[keyPrefix + "FULL_NAME"] = fullNameText.toString()

        val nicknameText = findViewById<TextView>(R.id.nicknameEditText).text
        showActivityHashMap[keyPrefix + "NICKNAME"] = nicknameText.toString()

        val emailText = findViewById<TextView>(R.id.emailEditText).text
        showActivityHashMap[keyPrefix + "EMAIL"] = emailText.toString()

        val locationText = findViewById<TextView>(R.id.locationEditText).text
        showActivityHashMap[keyPrefix + "LOCATION"] = locationText.toString()

        val skillsText = findViewById<TextView>(R.id.skillEditText).text
        showActivityHashMap[keyPrefix + "SKILLS"] = skillsText.toString()

        val descriptionText = findViewById<TextView>(R.id.descriptionEditText).text
        showActivityHashMap[keyPrefix + "DESCRIPTION"] = descriptionText.toString()

        extras.putSerializable("showActivityHashMap", showActivityHashMap)
        extras.putString("RESULT", "OK")

        i.putExtras(extras)

        setResult(Activity.RESULT_OK, i)
    }


    /******* CAMERA ********/
    /* Context menu for Camera */
    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)

        val inflater: MenuInflater = menuInflater
        menu.setHeaderTitle("Choose your option")
        inflater.inflate(R.menu.camera_menu, menu)
    }

    /* Options for Camera */
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onContextItemSelected(item: MenuItem): Boolean {
        //val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
        return when (item.itemId) {
            R.id.selectImageOption -> {
                Toast.makeText(this, "Select an image from the phone gallery", Toast.LENGTH_SHORT)
                    .show()
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                getImageFromGallery.launch(Intent.createChooser(intent, ""))
                true
            }
            R.id.useCameraOption -> {
                Toast.makeText(this, "Use the camera to take a picture", Toast.LENGTH_SHORT).show()
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(
                        arrayOf(Manifest.permission.CAMERA),
                        MY_CAMERA_PERMISSION_CODE
                    )
                } else {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            MY_CAMERA_PERMISSION_CODE + 1
                        )
                    } else {
                        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
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
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_CAMERA_PERMISSION_CODE || requestCode == MY_CAMERA_PERMISSION_CODE + 1 || requestCode == MY_CAMERA_PERMISSION_CODE + 2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_LONG).show()
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
                Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun clonePic(uri: Uri): Uri {
        val resultUri = setImageUri()
        BitmapFactory.decodeStream(contentResolver.openInputStream(uri)).also { bitmap ->
            val fos: OutputStream? = contentResolver.openOutputStream(resultUri)

            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        }
        return resultUri
    }


    @Throws(IOException::class)
    private fun setImageUri(): Uri {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        val uri = File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )
        val photoURI: Uri = FileProvider.getUriForFile(
            this,
            "it.polito.mad.lab02.provider",
            uri
        )
        imgUri = photoURI
        return photoURI
    }

    private fun deleteOldImage(): Boolean {
        if (imgUriOld != Uri.parse("android.resource://it.polito.mad.lab02/drawable/profile_image") && imgUriOld != imgUri) {

            val file = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), imgUriOld.lastPathSegment!!)
            if(file.exists()) {
                return file.delete()
            }
        }
        return false
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
    @RequiresApi(Build.VERSION_CODES.R)
    private val getImageFromCamera =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                val imageFile = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), imgUri.lastPathSegment!!)
                val ei = ExifInterface(imageFile)
                val orientation: Int = ei.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED
                )

                var rotatedBitmap: Bitmap? = null
                val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(imgUri))
                rotatedBitmap = when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90f)
                    ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180f)
                    ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270f)
                    ExifInterface.ORIENTATION_NORMAL -> bitmap
                    else -> bitmap
                }
                val fos: OutputStream? = contentResolver.openOutputStream(imgUri)
                rotatedBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                val editProfileImageView = findViewById<ImageView>(R.id.editProfileImageView)
                Utils.setUriInImageView(editProfileImageView, imgUri, contentResolver)
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
                    val editProfileImageView = findViewById<ImageView>(R.id.editProfileImageView)
                    Utils.setUriInImageView(editProfileImageView, imgUri, contentResolver)
                }
            }
        }

    private fun onSave() {

        deleteOldImage()

        val pref = SharedPreference(this)

        val fullName = findViewById<EditText>(R.id.fullNameEditText)
        val nickname = findViewById<EditText>(R.id.nicknameEditText)
        val email = findViewById<EditText>(R.id.emailEditText)
        val location = findViewById<EditText>(R.id.locationEditText)
        val skills = findViewById<EditText>(R.id.skillEditText)
        val description = findViewById<EditText>(R.id.descriptionEditText)
        val obj = ProfileClass(
            imageUri = imgUri.toString(),
            fullName = fullName.text.toString(),
            nickname = nickname.text.toString(),
            email = email.text.toString(),
            location = location.text.toString(),
            skills = skills.text.toString(),
            description = description.text.toString()
        )

        val gson = Gson()
        val json = gson.toJson(obj)
        pref.setProfile(json)
    }

}

