package it.polito.mad.lab02

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.annotation.RequiresApi

/* Constants for CAMERA */
private const val CAMERA_REQUEST = 1888
private const val MY_CAMERA_PERMISSION_CODE = 100
private const val RESULT_LOAD_IMAGE = 1


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
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onContextItemSelected(item: MenuItem): Boolean {
        //val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
        return when (item.itemId) {
            R.id.selectImageOption -> {
                Toast.makeText(
                    this,
                    "Option selectImageOption selected", Toast.LENGTH_SHORT
                ).show()
                val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(i, RESULT_LOAD_IMAGE)
                true
            }
            R.id.useCameraOption -> {
                Toast.makeText(this, "Option useCameraOption selected", Toast.LENGTH_SHORT).show()
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(
                        arrayOf(Manifest.permission.CAMERA),
                        MY_CAMERA_PERMISSION_CODE
                    )
                } else {
                    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(cameraIntent, CAMERA_REQUEST)
                }
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }

    /* Use the returned picture from the camera */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val editProfileImageView = findViewById<ImageView>(R.id.editProfileImageView)

        //get result from the camera
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            val photo = data?.extras!!["data"] as Bitmap?
            editProfileImageView.setImageBitmap(photo)
        }

        //get result from the gallery
        //TODO: https://www.youtube.com/watch?v=KaDwSvOpU5E
        if (requestCode === RESULT_LOAD_IMAGE && resultCode === RESULT_OK && null != data) {
            val selectedImage: Uri? = data.data
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = selectedImage?.let {
                contentResolver.query(
                    it,
                    filePathColumn, null, null, null
                )
            }
            cursor!!.moveToFirst()
            val columnIndex = cursor.getColumnIndex(filePathColumn[0])
            val picturePath = cursor.getString(columnIndex)
            cursor.close()
            println(picturePath)
            editProfileImageView.setImageBitmap(BitmapFactory.decodeFile(picturePath))
        }
    }

}

