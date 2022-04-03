package it.polito.mad.lab02

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Environment.DIRECTORY_DCIM
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
import android.provider.MediaStore.MediaColumns.*
import android.util.Log
import android.view.*
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.annotation.RequiresApi
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap
import com.google.gson.Gson
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class EditProfileActivity : AppCompatActivity() {

    /* Variables for CAMERA */
    private val CAMERA_REQUEST = 1888
    private val MY_CAMERA_PERMISSION_CODE = 100
    private val RESULT_LOAD_IMAGE = 1
    private val CAPTURE_IMAGE = 3
    private val PICK_IMAGE = 2
    private var imgPath: Uri =
        Uri.parse("android.resource://it.polito.mad.lab02/drawable/profile_image")
    private var imgName = ""

    @SuppressLint("WrongThread")
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

        secondLayer.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val h = secondLayer.height
                val w = secondLayer.width
                Log.d("Layout", "firstLayout.requestLayout(): $w,$h")
                firstLayout.post {
                    firstLayout.layoutParams =
                        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
                            LinearLayout.LayoutParams(w / 3, h)
                        else
                            LinearLayout.LayoutParams(w, h / 3)
                }
                secondLayer.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

        val profileImageButton = findViewById<ImageButton>(R.id.editProfileImageButton)
        profileImageButton.setOnClickListener { onButtonClickEvent(profileImageButton) }

        //Retrieve a Bundle object
        val extras: Bundle? = intent.extras
        val showActivityHashMap =
            extras!!.getSerializable("showActivityHashMap") as HashMap<String, String>

        val keyPrefix = "group07.lab2."

        val editProfileImageView = findViewById<ImageView>(R.id.editProfileImageView)
        val editProfileImageUri = showActivityHashMap[keyPrefix + "PROFILE_IMG_URI"]
        imgPath = Uri.parse(editProfileImageUri!!)
        setPicInImageView(editProfileImageView, Uri.parse(editProfileImageUri))


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

    private fun setPicInImageView(imageView: ImageView, imgUri: Uri) {
        BitmapFactory.decodeStream(contentResolver.openInputStream(imgUri)).also { bitmap ->
            imageView.setImageBitmap(bitmap)
        }
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

        showActivityHashMap[keyPrefix + "PROFILE_IMG_URI"] = imgPath.toString()

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
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onContextItemSelected(item: MenuItem): Boolean {
        //val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
        return when (item.itemId) {
            R.id.selectImageOption -> {
                Toast.makeText(this, "Select an image from the phone gallery", Toast.LENGTH_SHORT)
                    .show()
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(Intent.createChooser(intent, ""), PICK_IMAGE)
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
                            //val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            //startActivityForResult(cameraIntent, CAMERA_REQUEST)
                            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, setImageUri())
                            startActivityForResult(intent, CAPTURE_IMAGE)

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
                startActivityForResult(intent, CAPTURE_IMAGE)
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setPic(imageView: ImageView) {
        BitmapFactory.decodeStream(contentResolver.openInputStream(imgPath)).also { bitmap ->
            imageView.setImageBitmap(bitmap)
        }
    }

    private fun clonePic(uri: Uri) : Uri {
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
        imgName = "JPEG_${timeStamp}_" + ".jpg"
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        val uri = File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )
        val photoURI: Uri = FileProvider.getUriForFile(
            this,
            "it.polito.mad.lab02.provider",
            uri!!
        )
        imgPath = photoURI
        return photoURI
    }

    /* Use the returned picture from the camera */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val editProfileImageView = findViewById<ImageView>(R.id.editProfileImageView)

        //get result from the camera
        if (requestCode == PICK_IMAGE) {
            if (data != null) {
                imgPath = clonePic(data!!.data!!)
                editProfileImageView.setImageBitmap(
                    MediaStore.Images.Media.getBitmap(
                        this.contentResolver,
                        data.data
                    )
                )
            }
        } else if (requestCode == CAPTURE_IMAGE) {
            setPic(editProfileImageView)
        }
    }

    private fun onSave() {
        val pref = SharedPreference(this)

        val fullName = findViewById<EditText>(R.id.fullNameEditText)
        val nickname = findViewById<EditText>(R.id.nicknameEditText)
        val email = findViewById<EditText>(R.id.emailEditText)
        val location = findViewById<EditText>(R.id.locationEditText)
        val skills = findViewById<EditText>(R.id.skillEditText)
        val description = findViewById<EditText>(R.id.descriptionEditText)
        val obj = ProfileClass(
            imageUri = imgPath.toString(),
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

