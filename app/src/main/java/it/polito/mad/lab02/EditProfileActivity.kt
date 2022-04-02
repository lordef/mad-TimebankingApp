package it.polito.mad.lab02

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
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
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

/* Constants for CAMERA */
private val CAMERA_REQUEST = 1888
private val MY_CAMERA_PERMISSION_CODE = 100
private val RESULT_LOAD_IMAGE = 1
private val CAPTURE_IMAGE = 3
private val PICK_IMAGE = 2


class EditProfileActivity : AppCompatActivity() {
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

        //Retrieve a Bundle object : TODO
        // val extras:Bundle? = intent.extras

        // val testTextView = findViewById<TextView>(R.id.edit4TextView)
        // testTextView.text = extras!!.getString("DEFAULTTEXT")
        /* end - TODO */
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
        editedProfile()

        super.onBackPressed()
    }

    /* Confirm edited fields */
    private fun editedProfile() {
        val i = Intent(this, ShowProfileActivity::class.java)

        //Create a Bundle object
        val extras = Bundle()
        extras.putString("RESULT", "OK")
        i.putExtras(extras)

        setResult(Activity.RESULT_OK, i)
        //finish() //TODO: useful?
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
    /* Options for Camera */
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onContextItemSelected(item: MenuItem): Boolean {
        //val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
        return when (item.itemId) {
            R.id.selectImageOption -> {
                Toast.makeText(this, "Select an image from the phone gallery", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this, "permission granted", Toast.LENGTH_LONG).show()
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
                Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setPic(imageView: ImageView) {
        // Get the dimensions of the View
        val targetW: Int = imageView.width
        val targetH: Int = imageView.height

        val bmOptions = BitmapFactory.Options().apply {
            // Get the dimensions of the bitmap
            inJustDecodeBounds = true

            //BitmapFactory.decodeFile(imgPath)
            BitmapFactory.decodeStream(getContentResolver().openInputStream(imgPath))

            val photoW: Int = outWidth
            val photoH: Int = outHeight

            // Determine how much to scale down the image
            val scaleFactor: Int = Math.max(1, Math.min(photoW / targetW, photoH / targetH))

            // Decode the image file into a Bitmap sized to fill the View
            inJustDecodeBounds = false
            inSampleSize = scaleFactor
            inPurgeable = true
        }
        BitmapFactory.decodeStream(getContentResolver().openInputStream(imgPath), null, bmOptions)
            ?.also { bitmap ->
                imageView.setImageBitmap(bitmap)
            }
    }

    private var imgPath: Uri = Uri.EMPTY
    private var imgName = ""

    private fun setImageUri(): Uri {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        imgName = "JPEG_${timeStamp}_" + ".jpg"
        val fos: OutputStream?
        val contentValues = ContentValues().apply {
            put(DISPLAY_NAME, imgName)
            put(MIME_TYPE, "image/png")
            put(RELATIVE_PATH, DIRECTORY_DCIM)
            put(IS_PENDING, 1)
        }

        //use application context to get contentResolver
        val contentResolver = applicationContext.contentResolver
        val uri = contentResolver.insert(EXTERNAL_CONTENT_URI, contentValues)
        uri?.let { contentResolver.openOutputStream(it) }.also { fos = it }
        fos?.flush()
        fos?.close()

        contentValues.clear()
        contentValues.put(IS_PENDING, 0)
        uri?.let {
            contentResolver.update(it, contentValues, null, null)
        }
        this.imgPath = uri!!
        return uri!!
    }

    /* Use the returned picture from the camera */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val editProfileImageView = findViewById<ImageView>(R.id.editProfileImageView)

        //get result from the camera
        if (requestCode == PICK_IMAGE) {
            if (data != null) {
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

}

