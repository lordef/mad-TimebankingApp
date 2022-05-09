package it.polito.mad.lab02

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.GoogleAuthProvider


class LoginActivity : AppCompatActivity() {

    private lateinit var signInButton: SignInButton
    private lateinit var signOutButton: Button
    private lateinit var googleSignInClient: GoogleSignInClient
    private var mAuth: FirebaseAuth? = null
    private var mAuthListener: AuthStateListener? = null


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
            ActivityResultContracts.StartActivityForResult()
        ) {
            var task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            Log.d("MYTAG", "${task.isSuccessful}")
            if (task.isSuccessful) {
                // Sign in succeeded, proceed with account
                val acct: GoogleSignInAccount = task.result
                Log.d("MYTAG", acct.toString())
                firebaseAuthWithGoogle(acct);


            } else {
                // Sign in failed
            }

        }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        signInReceiver.launch(signInIntent)
    }

    private fun signOut() {
        googleSignInClient.signOut().addOnSuccessListener(this) {
            Toast.makeText(this, "Signed out", Toast.LENGTH_SHORT).show()
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
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
}