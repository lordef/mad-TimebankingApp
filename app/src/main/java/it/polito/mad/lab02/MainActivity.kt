package it.polito.mad.lab02

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import it.polito.mad.lab02.databinding.ActivityMainBinding
import it.polito.mad.lab02.viewmodels.ShowProfileViewModel


//SOURCE GUIDE VIDEO https://www.youtube.com/watch?v=Mr__NdcIxqs

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private val vm by viewModels<ShowProfileViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_profile, R.id.nav_advertisement
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        vm.getProfileInfo().observe(this) { profile ->
            // set side bar profile image and user info
            val drawerProfileImage = binding.navView.getHeaderView(0).findViewById<ImageView>(R.id.drawer_profile_image)
            val drawerNickname =   binding.navView.getHeaderView(0).findViewById<TextView>(R.id.drawer_nickname)
            val drawerFullName = binding.navView.getHeaderView(0).findViewById<TextView>(R.id.drawer_fullname)

            drawerProfileImage.setImageURI(Uri.parse(profile.imageUri))
            drawerNickname.text = profile.nickname
            drawerFullName.text = profile.fullName

        }


    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


}