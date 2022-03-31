package it.polito.mad.lab02

import android.content.Intent
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
        val firstLayout = findViewById<ConstraintLayout>(R.id.upperConstraintLayout)
        val sv = findViewById<ScrollView>(R.id.mainScrollView)

        sv.viewTreeObserver.addOnGlobalLayoutListener(object: ViewTreeObserver.OnGlobalLayoutListener{
             override fun onGlobalLayout() {
                val h = sv.height
                val w = sv.width
                Log.d("Layout", "firstLayout.requestLayout(): $w,$h")
                firstLayout.post{firstLayout.layoutParams = LinearLayout.LayoutParams(w, h/3)}
                sv.viewTreeObserver.removeOnGlobalLayoutListener(this )
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
}