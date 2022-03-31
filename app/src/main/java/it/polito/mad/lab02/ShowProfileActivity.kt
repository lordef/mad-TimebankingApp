package it.polito.mad.lab02

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.ViewTreeObserver
import android.widget.*

class ShowProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_profile)

        val firstLayout = findViewById<LinearLayout>(R.id.upperLinearLayout)
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
                Toast.makeText(this, "Menu selected", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}