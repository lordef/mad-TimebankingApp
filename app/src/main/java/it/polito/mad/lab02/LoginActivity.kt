package it.polito.mad.lab02

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import it.polito.mad.lab02.models.Profile
import okhttp3.internal.notify
import java.util.*


class LoginActivity : AppCompatActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private var mAuth: FirebaseAuth? = null
    private var mAuthListener: AuthStateListener? = null
    private var isFirstAuthentication = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)



        mAuth = FirebaseAuth.getInstance()
        mAuthListener = AuthStateListener { firebaseAuth ->
            if (firebaseAuth.currentUser != null && !isFirstAuthentication) {
                Toast.makeText(
                    applicationContext,
                    "Logged in as ${mAuth?.currentUser?.displayName}",
                    Toast.LENGTH_SHORT
                ).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        if (intent.extras?.getBoolean("logout") == true) {
            signOut()
        }

        val signInButton = findViewById<SignInButton>(R.id.sign_in_button)
        signInButton.setOnClickListener {
            signIn()
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
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            if (task.isSuccessful) {
                // Sign in succeeded, proceed with account
                val acct: GoogleSignInAccount = task.result
                firebaseAuthWithGoogle(acct)


            } else {
                // Sign in failed
            }

        }

    private fun signIn() {
        isFirstAuthentication = true
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
                    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
                    db
                        .collection("users")
                        .whereEqualTo("uid", mAuth?.currentUser?.uid)
                        .get().addOnSuccessListener {
                            if (it.isEmpty) {
                                val user = Profile(
                                    mAuth?.currentUser?.photoUrl.toString(),
                                    mAuth?.currentUser?.displayName!!,
//                                    "",
                                    //in the first sign in, set nickname as derivation from username/displayName
                                    mAuth?.currentUser?.displayName!!
                                        .replace(" ", ".")
                                        .lowercase(Locale.getDefault()),
                                    mAuth?.currentUser?.email!!,
                                    "",
                                    emptyList(),
                                    "",
                                    mAuth?.currentUser?.uid!!,
                                    5*60 //5 free hours gifted
                                )

                                db
                                    .collection("users")
                                    .document(mAuth?.currentUser?.uid!!)
                                    .set(user).addOnSuccessListener {
                                        isFirstAuthentication = false
                                        Toast.makeText(
                                            applicationContext,
                                            "Logged in as ${mAuth?.currentUser?.displayName}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        val intent = Intent(this, MainActivity::class.java)
                                        intent.putExtra("newAccount", true)
                                        startActivity(intent)
                                        finish()
                                    }
                            } else {
                                Toast.makeText(
                                    applicationContext,
                                    "Logged in as ${mAuth?.currentUser?.displayName}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                startActivity(Intent(this, MainActivity::class.java))
                                finish()
                            }
                        }
                        .addOnFailureListener {
                            isFirstAuthentication = false
                        }
                } else {
                    Toast.makeText(applicationContext, "Not logged in", Toast.LENGTH_LONG).show()
                }
            }
    }
}