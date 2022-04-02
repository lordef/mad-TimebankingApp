package it.polito.mad.lab02

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.ViewTreeObserver
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.gson.Gson

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

        // Retrieve json object of class ProfileClass
        val pref = SharedPreference(this)
        val gson = Gson()
        val json = pref.getProfile()
        if(!json.equals("")) {
            val obj = gson.fromJson(json, ProfileClass::class.java)
            // Put it into the TextViews
            val profileImage = findViewById<ImageView>(R.id.profileImageView)
            val fullName = findViewById<TextView>(R.id.fullNameTextView)
            val nickname = findViewById<TextView>(R.id.nicknameTextView)
            val email = findViewById<TextView>(R.id.emailTextView)
            val location = findViewById<TextView>(R.id.locationTextView)
            val skills = findViewById<TextView>(R.id.skill1TextView)
            val description = findViewById<TextView>(R.id.descriptionTextView)
            if(obj!==null) {
                /*profileImage.setImageBitmap(
                    MediaStore.Images.Media.getBitmap(
                        this.contentResolver,
                        Uri.parse(obj.imageUri)
                    )
                )*/
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

        extras.putString("DEFAULTTEXT","John Doe")
        i.putExtras(extras)

        startActivityForResult(i, 1)
    }

    /* TODO */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1){
            if (resultCode == Activity.RESULT_OK){
                val result: String? = data!!.extras!!.getString("RESULT")
                val imgUri = Uri.parse(data!!.extras!!.getString("ImgUri"))
                val profileImage = findViewById<ImageView>(R.id.profileImageView)
                println("ImageUri: ${imgUri}")
                profileImage.setImageBitmap(
                    MediaStore.Images.Media.getBitmap(
                        this.contentResolver,
                        imgUri
                    )
                )
                println("result: ${result}")
            }
        }
    }
}