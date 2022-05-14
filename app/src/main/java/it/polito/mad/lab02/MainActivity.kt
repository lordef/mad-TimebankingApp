package it.polito.mad.lab02

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import coil.load
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import it.polito.mad.lab02.databinding.ActivityMainBinding
import it.polito.mad.lab02.viewmodels.PublicTimeSlotListViewModel
import it.polito.mad.lab02.viewmodels.ShowProfileViewModel
import it.polito.mad.lab02.viewmodels.SkillListViewModel
import it.polito.mad.lab02.viewmodels.TimeSlotListViewModel


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private val vm by viewModels<ShowProfileViewModel>()
    private val vm1 by viewModels<PublicTimeSlotListViewModel>()
    private val vm2 by viewModels<TimeSlotListViewModel>()
    private val vm3 by viewModels<SkillListViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView

        /*
        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_logout -> {
                    FirebaseAuth.getInstance().signOut()
                    val loginActivity = Intent(applicationContext, LoginActivity::class.java)
                    startActivity(loginActivity)
                    Log.d("MYTAG", "here")
                    finish()
                }

            }
            true
        }
         */

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_profile, R.id.nav_advertisement, R.id.nav_all_advertisements
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        vm.profile.observe(this) { profile ->
            // set side bar profile image and user info
            val drawerProfileImage =
                binding.navView.getHeaderView(0).findViewById<ImageView>(R.id.drawer_profile_image)
            val drawerNickname =
                binding.navView.getHeaderView(0).findViewById<TextView>(R.id.drawer_nickname)
            val drawerFullName =
                binding.navView.getHeaderView(0).findViewById<TextView>(R.id.drawer_fullname)

            drawerProfileImage.load(Uri.parse(profile.imageUri))
            //drawerProfileImage.setImageURI(Uri.parse(profile.imageUri))
            drawerNickname.text = profile.nickname
            drawerFullName.text = profile.fullName

            val logout = binding.navView.getHeaderView(0).findViewById<Button>(R.id.nav_logout)
            logout.setOnClickListener{
                FirebaseAuth.getInstance().signOut()
                val loginActivity = Intent(applicationContext, LoginActivity::class.java)
                loginActivity.putExtra("logout", true)
                startActivity(loginActivity)
                finish()
            }
        }

        return super.onCreateView(name, context, attrs)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


}