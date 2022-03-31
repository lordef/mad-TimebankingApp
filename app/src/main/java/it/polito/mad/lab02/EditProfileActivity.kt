package it.polito.mad.lab02

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton

class EditProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val profileImageButton = findViewById<ImageButton>(R.id.profileImageButton)
        profileImageButton.setOnClickListener{onButtonClickEvent(profileImageButton)}
    }
}

/* Useful for register a Context Menu - allows sigle click instead of long press */
private fun onButtonClickEvent(sender: View?) {
    registerForContextMenu(sender)
    openContextMenu(sender)
    unregisterForContextMenu(sender)
}