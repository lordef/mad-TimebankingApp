package it.polito.mad.lab02

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDeepLinkBuilder
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import coil.decode.BitmapFactoryDecoder
import coil.imageLoader
import coil.load
import coil.request.ImageRequest
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import it.polito.mad.lab02.databinding.ActivityMainBinding
import it.polito.mad.lab02.viewmodels.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.internal.notify
import java.net.URL


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private val vm by viewModels<MainActivityViewModel>()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val newAccount = intent.getBooleanExtra("newAccount", false)
        if(newAccount){
            val builder: androidx.appcompat.app.AlertDialog.Builder =
                androidx.appcompat.app.AlertDialog.Builder(this, R.style.AlertDialogTheme)

            builder.setCancelable(true)
            builder.setTitle("Welcome!")
            builder.setMessage("You earned 5 free hours")

            builder.setPositiveButton("OK",
                DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.cancel()
                })
            builder.show()
        }

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
                R.id.nav_profile, R.id.nav_advertisement, R.id.nav_all_advertisements, R.id.nav_chats,
                R.id.nav_timeSlotAssignedAndAcceptedFragment, R.id.nav_timeSlotsOfInterestFragment
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navView.menu.findItem(R.id.nav_logout).setOnMenuItemClickListener {
            FirebaseAuth.getInstance().signOut()
            val loginActivity = Intent(applicationContext, LoginActivity::class.java)
            loginActivity.putExtra("logout", true)
            startActivity(loginActivity)
            finish()
            true
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            val name = "myNotificationChannel"
            val descriptionText = "New messages"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel("9999", name, importance)
            mChannel.description = descriptionText
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }

        vm.newMessage.observe(this) {

            if (it != null) {

                val bundle = Bundle()
                bundle.putString("id", it.id)
                bundle.putString("timeslot", Gson().toJson(it.timeSlot))

                val pendingIntent = NavDeepLinkBuilder(this)
                    .setComponentName(MainActivity::class.java)
                    .setGraph(R.navigation.mobile_navigation)
                    .setDestination(R.id.nav_single_message)
                    .setArguments(bundle)
                    .createPendingIntent()

                var image: Drawable?

                lifecycleScope.launch(Dispatchers.IO) {
                    val request = ImageRequest.Builder(applicationContext)
                        .data(Uri.parse(it.user.imageUri))
                        .build()
                    image = imageLoader
                        .execute(request)
                        .drawable
                    if (image != null) {
                        var notification = NotificationCompat.Builder(applicationContext, "9999")
                            .setSmallIcon(IconCompat.createWithBitmap(image?.toBitmap()))
                            .setContentTitle("Message from: ${it.user.nickname}")
                            .setContentText(it.text)
                            .setLargeIcon(image?.toBitmap())
                            .setStyle(
                                NotificationCompat.BigTextStyle()
                                    .bigText(it.text)
                            )
                            .setContentIntent(pendingIntent)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setAutoCancel(true)
                            .build()

                        val notificationManagerCompat = NotificationManagerCompat.from(applicationContext)
                        notificationManagerCompat.notify(100, notification)
                    }
                }

                //Toast.makeText(this, "New message from: ${it.user.nickname}", Toast.LENGTH_SHORT).show()

            }
        }

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

        }

        return super.onCreateView(name, context, attrs)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


}