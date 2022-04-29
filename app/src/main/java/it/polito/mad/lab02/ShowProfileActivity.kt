package it.polito.mad.lab02

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.gson.Gson
import it.polito.mad.lab02.models.Profile


class ShowProfileActivity : AppCompatActivity() {

    /* Variable for communicating with EditProfileActivity  */
    private val EDIT_REQUEST_CODE = 1

    private var profileImageUri = "android.resource://it.polito.mad.lab02/drawable/profile_image"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_profile)

        /* Divide screen in 1/3 and 2/3 */
        val firstLayout = findViewById<ConstraintLayout>(R.id.upperConstraintLayout)
        val secondLayer =
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
                findViewById<LinearLayout>(R.id.mainLinearLayout)
            else
                findViewById<ScrollView>(R.id.mainScrollView)

        Utils.divideDisplayInPortion(firstLayout, secondLayer, resources.configuration.orientation)

        // Retrieve json object of class ProfileClass
        val pref = SharedPreference(this)
        val gson = Gson()
        val obj = pref.getProfile()
        if (!obj.equals("")) {
            //val obj = gson.fromJson(json, Profile::class.java)
            // Put it into the TextViews
            val profileImage = findViewById<ImageView>(R.id.profileImageView)
            val fullName = findViewById<TextView>(R.id.fullNameTextView)
            val nickname = findViewById<TextView>(R.id.nicknameTextView)
            val email = findViewById<TextView>(R.id.emailTextView)
            val location = findViewById<TextView>(R.id.locationTextView)
            val skills = findViewById<TextView>(R.id.skillTextView)
            val description = findViewById<TextView>(R.id.descriptionTextView)
            if (obj !== null) {
                profileImageUri = obj.imageUri
                Utils.setUriInImageView(profileImage, Uri.parse(profileImageUri), contentResolver)

                fullName.text = obj.fullName
                nickname.text = obj.nickname
                email.text = obj.email
                location.text = obj.location
                skills.text = obj.skills
                description.text = obj.description
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.pencil_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.editItem -> {
                Toast.makeText(this, "Edit profile selected", Toast.LENGTH_SHORT).show()
                editProfile()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun editProfile() {
        val i = Intent(this, EditProfileActivity::class.java)

        //Create a Bundle object
        val extras = Bundle()
        val showActivityHashMap = HashMap<String, String>()

        //Fill out hashmap
        val keyPrefix = "group07.lab2."

        showActivityHashMap[keyPrefix + "PROFILE_IMG_URI"] = profileImageUri

        val fullNameText = findViewById<TextView>(R.id.fullNameTextView).text
        showActivityHashMap[keyPrefix + "FULL_NAME"] = fullNameText.toString()

        val nicknameText = findViewById<TextView>(R.id.nicknameTextView).text
        showActivityHashMap[keyPrefix + "NICKNAME"] = nicknameText.toString()

        val emailText = findViewById<TextView>(R.id.emailTextView).text
        showActivityHashMap[keyPrefix + "EMAIL"] = emailText.toString()

        val locationText = findViewById<TextView>(R.id.locationTextView).text
        showActivityHashMap[keyPrefix + "LOCATION"] = locationText.toString()

        val skillsText = findViewById<TextView>(R.id.skillTextView).text
        showActivityHashMap[keyPrefix + "SKILLS"] = skillsText.toString()

        val descriptionText = findViewById<TextView>(R.id.descriptionTextView).text
        showActivityHashMap[keyPrefix + "DESCRIPTION"] = descriptionText.toString()


        extras.putSerializable("showActivityHashMap", showActivityHashMap)

        i.putExtras(extras)

        getEditProfileData.launch(i)
    }

    // Receiver For EditProfile
    private val getEditProfileData =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                //Fill fields
                //Retrieve a Bundle object from EditProfileActivity
                val extras: Bundle? = it!!.data!!.extras
                val showActivityHashMap =
                    extras!!.getSerializable("showActivityHashMap") as HashMap<String, String>

                val keyPrefix = "group07.lab2."

                val profileImageView = findViewById<ImageView>(R.id.profileImageView)
                val profileImageUri = showActivityHashMap[keyPrefix + "PROFILE_IMG_URI"]
                profileImageView.setImageBitmap(
                    MediaStore.Images.Media.getBitmap(
                        this.contentResolver,
                        Uri.parse(profileImageUri)
                    )
                )
                this.profileImageUri = profileImageUri!!

                val fullNameTextView = findViewById<TextView>(R.id.fullNameTextView)
                fullNameTextView.text = showActivityHashMap.getValue(keyPrefix + "FULL_NAME")

                val nicknameTextView = findViewById<TextView>(R.id.nicknameTextView)
                nicknameTextView.text = showActivityHashMap.getValue(keyPrefix + "NICKNAME")

                val emailTextView = findViewById<TextView>(R.id.emailTextView)
                emailTextView.text = showActivityHashMap.getValue(keyPrefix + "EMAIL")

                val locationTextView = findViewById<TextView>(R.id.locationTextView)
                locationTextView.text = showActivityHashMap.getValue(keyPrefix + "LOCATION")

                val skillsTextView = findViewById<TextView>(R.id.skillTextView)
                skillsTextView.text = showActivityHashMap.getValue(keyPrefix + "SKILLS")

                val descriptionTextView = findViewById<TextView>(R.id.descriptionTextView)
                descriptionTextView.text = showActivityHashMap.getValue(keyPrefix + "DESCRIPTION")
            }
        }
}