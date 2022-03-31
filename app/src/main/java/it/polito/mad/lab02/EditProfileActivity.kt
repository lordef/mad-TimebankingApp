package it.polito.mad.lab02

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewTreeObserver
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.constraintlayout.widget.ConstraintLayout
import android.view.View
import android.widget.ImageButton


class EditProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

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
        
        val profileImageButton = findViewById<ImageButton>(R.id.profileImageButton)
        profileImageButton.setOnClickListener{onButtonClickEvent(profileImageButton)}
    }
}

/* Useful for register a Context Menu - allows sigle click instead of long press */
private fun onButtonClickEvent(sender: View?) {
    /*
    registerForContextMenu(sender)
    openContextMenu(sender)
    unregisterForContextMenu(sender)
    */
}