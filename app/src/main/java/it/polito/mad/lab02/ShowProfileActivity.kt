package it.polito.mad.lab02

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.ViewTreeObserver
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout

class ShowProfileActivity : AppCompatActivity() {

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
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater : MenuInflater = menuInflater
        inflater.inflate(R.menu.pencil_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.editItem -> {
                Toast.makeText(this, "Edit profile selected", Toast.LENGTH_SHORT).show()
                editProfile()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /* TODO:
        In order to reduce the risk of name clashes with existing keys, name your item out
        of your project package name, e.g. “group07.lab2.FULL_NAME”
    */
    private fun editProfile(){
        val i = Intent(this, EditProfileActivity::class.java)

        //Create a Bundle object
        val extras = Bundle()
        val showActivityHashMap = HashMap<String, String>()

        //Fill out hashmap
        val keyPrefix = "group07.lab2."

        /* TODO: insert image */
        /*
        val profileImageUrl = findViewById<ImageView>(R.id.profileImageView)
        showActivityHashMap[keyPrefix + "PROFILE_IMG_URL"] = profileImageUrl.toString()
        */

        val fullNameText = findViewById<TextView>(R.id.fullNameTextView).text
        showActivityHashMap[keyPrefix + "FULL_NAME"] = fullNameText.toString()

        val nicknameText = findViewById<TextView>(R.id.nicknameTextView).text
        showActivityHashMap[keyPrefix + "NICKNAME"] = nicknameText.toString()

        val emailText = findViewById<TextView>(R.id.emailTextView).text
        showActivityHashMap[keyPrefix + "EMAIL"] = emailText.toString()

        val locationText = findViewById<TextView>(R.id.locationTextView).text
        showActivityHashMap[keyPrefix + "LOCATION"] = locationText.toString()

        val skillsText = findViewById<TextView>(R.id.skill1TextView).text
        showActivityHashMap[keyPrefix + "SKILLS"] = skillsText.toString()

        val descriptionText = findViewById<TextView>(R.id.descriptionTextView).text
        showActivityHashMap[keyPrefix + "DESCRIPTION"] = descriptionText.toString()


        extras.putSerializable("showActivityHashMap", showActivityHashMap)

        i.putExtras(extras)

        startActivityForResult(i, 1)
    }

    /* Set up fields from edit Activity result  */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1){
            if (resultCode == Activity.RESULT_OK){
                //Print OK result
                val result: String? = data!!.extras!!.getString("RESULT")
                println("result: ${result}")

                //Fill fields
                //Retrieve a Bundle object
                val extras:Bundle? = data.extras
                val showActivityHashMap = extras!!.getSerializable("showActivityHashMap") as HashMap<String, String>

                val keyPrefix = "group07.lab2."

                val fullNameTextView = findViewById<TextView>(R.id.fullNameTextView)
                fullNameTextView.text = showActivityHashMap.getValue(keyPrefix+"FULL_NAME")

                val nicknameTextView = findViewById<TextView>(R.id.nicknameTextView)
                nicknameTextView.text = showActivityHashMap.getValue(keyPrefix+"NICKNAME")

                val emailTextView = findViewById<TextView>(R.id.emailTextView)
                emailTextView.text = showActivityHashMap.getValue(keyPrefix+"EMAIL")

                val locationTextView = findViewById<TextView>(R.id.locationTextView)
                locationTextView.text = showActivityHashMap.getValue(keyPrefix+"LOCATION")

                val skillsTextView = findViewById<TextView>(R.id.skill1TextView)
                skillsTextView.text = showActivityHashMap.getValue(keyPrefix+"SKILLS")

                val descriptionTextView = findViewById<TextView>(R.id.descriptionTextView)
                descriptionTextView.text = showActivityHashMap.getValue(keyPrefix+"DESCRIPTION")
            }
        }
    }
}