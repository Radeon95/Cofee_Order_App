package com.radu.raduandnunocaffe

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.radu.raduandnunocaffe.databinding.ActivityWelcomeScreenBinding

class Welcome_Screen_Activity : AppCompatActivity() {
    var binding: ActivityWelcomeScreenBinding? = null
    var mGoogleSignInClient: GoogleSignInClient? = null
    var mAuth: FirebaseAuth? = null
    var database: FirebaseDatabase? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeScreenBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this@Welcome_Screen_Activity, gso)
        binding!!.loginBtn.setOnClickListener {
            startActivity(
                Intent(
                    this@Welcome_Screen_Activity,
                    LoginActivity::class.java
                )
            )
        }
        binding!!.registerBtn.setOnClickListener {
            startActivity(
                Intent(
                    this@Welcome_Screen_Activity,
                    RegisterActivity::class.java
                )
            )
        }
        binding!!.googleBtn.setOnClickListener {
            val signInIntent = mGoogleSignInClient?.getSignInIntent()
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken)
            } catch (e: ApiException) {
                Toast.makeText(this@Welcome_Screen_Activity, "" + e.message, Toast.LENGTH_SHORT)
                    .show()
                Log.d("GOOGLEFAILED", e.message!!)
                // Google Sign In failed, update UI appropriately
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String?) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(this) { task: Task<AuthResult> ->
                if (task.isSuccessful) {
                    val user = mAuth!!.currentUser

                    //if user is signing in first time then get and show user info from google account
                    if (task.result.additionalUserInfo!!.isNewUser) {
                        val email = user!!.email
                        val uid = user.uid
                        val username = user.displayName
                        val image = user.photoUrl.toString()
                        val timestamp = System.currentTimeMillis()
                        val hashMap = HashMap<String, Any?>()
                        hashMap["username"] = username
                        hashMap["email"] = email
                        hashMap["uid"] = "" + uid
                        hashMap["usertype"] = "user"
                        hashMap["register_date"] = "" + timestamp
                        hashMap["profile_pic"] = "" + image
                        val database = FirebaseDatabase.getInstance()
                        val reference = database.getReference("Users")
                        reference.child(uid).setValue(hashMap)
                            .addOnSuccessListener(OnSuccessListener {
                                Toast.makeText(
                                    this@Welcome_Screen_Activity,
                                    "Google sign in successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                            })
                    }
                    startActivity(Intent(this@Welcome_Screen_Activity, MainActivity::class.java))
                    finish()
                } else {
                }
            }.addOnFailureListener { e: Exception ->
                Toast.makeText(
                    this@Welcome_Screen_Activity,
                    "" + e.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    companion object {
        private const val RC_SIGN_IN = 65
    }
}