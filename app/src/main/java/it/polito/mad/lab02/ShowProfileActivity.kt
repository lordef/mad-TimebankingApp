package it.polito.mad.lab02

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.ViewTreeObserver
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout

class ShowProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_profile)


        /* Divide screen in 1/3 and 2/3 */
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            val firstLayout = findViewById<ConstraintLayout>(R.id.upperConstraintLayout)
            val father = findViewById<LinearLayout>(R.id.mainLinearLayout)

            father.viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    val h = father.height
                    val w = father.width
                    Log.d("Layout", "firstLayout.requestLayout(): $w,$h")
                    firstLayout.post {
                        firstLayout.layoutParams = LinearLayout.LayoutParams(w/3, h)
                    }
                    father.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            })
        } else {
            val firstLayout = findViewById<ConstraintLayout>(R.id.upperConstraintLayout)
            val sv = findViewById<ScrollView>(R.id.mainScrollView)

            sv.viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    val h = sv.height
                    val w = sv.width
                    Log.d("Layout", "firstLayout.requestLayout(): $w,$h")
                    firstLayout.post {
                        firstLayout.layoutParams = LinearLayout.LayoutParams(w, h / 3)
                    }
                    sv.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            })
        }
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater : MenuInflater = menuInflater
        inflater.inflate(R.menu.pencil_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){ //TODO: change id
            R.id.item1 -> {
                Toast.makeText(this, "Edit profile selected", Toast.LENGTH_SHORT).show()
                editProfile()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /* TODO */
    private fun editProfile(){
        val i = Intent(this, EditProfileActivity::class.java)

        //Create a Bundle object
        val extras = Bundle()
        extras.putString("DEFAULTTEXT","Tizio Doe")
        i.putExtras(extras)

        startActivityForResult(i, 1)
    }

    /* TODO */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1){
            if (resultCode == Activity.RESULT_OK){
                val result: String? = data!!.extras!!.getString("RESULT")
                println("result: ${result}")
            }
        }
    }
}