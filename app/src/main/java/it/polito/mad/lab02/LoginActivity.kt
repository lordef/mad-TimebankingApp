package it.polito.mad.lab02

import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.GoogleAuthProvider
import java.lang.Exception


class LoginActivity : AppCompatActivity() {

    private lateinit var signInButton: SignInButton
    private lateinit var signOutButton: Button
    private lateinit var googleSignInClient: GoogleSignInClient
    private var mAuth: FirebaseAuth? = null
    private var mAuthListener: AuthStateListener? = null

    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()
        mAuthListener = AuthStateListener { firebaseAuth ->
            if (firebaseAuth.currentUser != null) {
                Toast.makeText(applicationContext, "Success", Toast.LENGTH_LONG).show()
                finish()
                startActivity(Intent(this, MainActivity::class.java))
            }
        }

        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        //version 2
        oneTapClient = Identity.getSignInClient(this)
        signInRequest = BeginSignInRequest.builder()
            .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                .setSupported(true)
                .build())
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId(getString(R.string.default_web_client_id))
                    .build())
            // Automatically sign in when exactly one credential is retrieved.
            .setAutoSelectEnabled(true)
            .build()



        val signInButton = findViewById<SignInButton>(R.id.sign_in_button)
        signInButton.setOnClickListener {
            signIn()
        }

        val signOutButton = findViewById<Button>(R.id.sign_out_button)
        signOutButton.setOnClickListener {
            signOut()
        }


    }

    override fun onStart() {
        super.onStart()
        mAuth!!.addAuthStateListener(mAuthListener!!)
    }

    // Receiver For SignIn
    private val signInReceiver =
        registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) {
            var task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            Log.d("MYTAG", "${task.isSuccessful}")
            if (task.isSuccessful) {
                // Sign in succeeded, proceed with account
                val acct: GoogleSignInAccount = task.result
                Log.d("MYTAG", acct.toString())
                firebaseAuthWithGoogle("acct");


            } else {
                // Sign in failed
            }

        }

    // Receiver For SignIn
    private val signInReceiverSecondTime =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            var task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            Log.d("MYTAG", "${task.isSuccessful}")
            if (task.isSuccessful) {
                // Sign in succeeded, proceed with account
                val acct: GoogleSignInAccount = task.result
                Log.d("MYTAG", acct.toString())
                firebaseAuthWithGoogle("acct");


            } else {
                // Sign in failed
            }

        }

    private fun signIn() {

        //val signInIntent = googleSignInClient.signInIntent
        //signInReceiver.launch(signInIntent)

        //version 2
        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener(this) { result ->
                try {
                    startIntentSenderForResult(
                        result.pendingIntent.intentSender, 9002,
                        null, 0, 0, 0, null)
                } catch (e: IntentSender.SendIntentException) {
                    Log.e("MYTAG", "Couldn't start One Tap UI: ${e.localizedMessage}")
                }
            }
            .addOnFailureListener(this) { e ->
                // No saved credentials found. Launch the One Tap sign-up flow, or
                // do nothing and continue presenting the signed-out UI.
                Log.d("MYTAG", e.localizedMessage)
                val signInIntent = googleSignInClient.signInIntent
                signInReceiverSecondTime.launch(signInIntent)
            }
    }

    private fun signOut() {
        googleSignInClient.signOut().addOnSuccessListener(this) {
            Toast.makeText(this, "Signed out", Toast.LENGTH_SHORT).show()
        }
    }

    private fun firebaseAuthWithGoogle(token: String) {
        val credential = GoogleAuthProvider.getCredential(token, null)
        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(
                this
            ) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(applicationContext, "Success", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(applicationContext, "Not Success", Toast.LENGTH_LONG).show()
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            9002 -> {
                try {
                    val credential = oneTapClient.getSignInCredentialFromIntent(data)
                    val idToken = credential.googleIdToken
                    when {
                        idToken != null -> {
                            // Got an ID token from Google. Use it to authenticate
                            // with your backend.
                            Log.d("MYTAG", "Got ID token.")
                            Log.d("MYTAG", idToken.toString())
                            firebaseAuthWithGoogle(idToken);

                        }
                        else -> {
                            // Shouldn't happen.
                            Log.d("MYTAG", "No ID token!")
                        }
                    }
                } catch (e: Exception) {
                    // ...
                }
            }
        }
    }
}