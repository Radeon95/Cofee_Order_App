package com.radu.raduandnunocaffe

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.view.MotionEvent
import android.view.View.OnTouchListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.radu.raduandnunocaffe.Admin.Admin_Home_Activity
import com.radu.raduandnunocaffe.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    var binding: ActivityLoginBinding? = null
    var passwordVisible = false
    private var progressDialog: ProgressDialog? = null
    var mAuth: FirebaseAuth? = null
    var database: FirebaseDatabase? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        progressDialog = ProgressDialog(this)
        progressDialog!!.setTitle("Please Wait...")
        progressDialog!!.setCanceledOnTouchOutside(false)
        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        binding!!.backBtn.setOnClickListener { onBackPressed() }
        binding!!.forgotPassTv.setOnClickListener {
            startActivity(
                Intent(
                    this@LoginActivity,
                    Forgot_Password_Activity::class.java
                )
            )
        }
        binding!!.passwordEt.setOnTouchListener(OnTouchListener { view, motionEvent ->
            val Right = 2
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                if (motionEvent.rawX >= binding!!.passwordEt.right - binding!!.passwordEt.compoundDrawables[Right].bounds.width()) {
                    val selection = binding!!.passwordEt.selectionEnd
                    if (passwordVisible) {
                        //set drawable image here
                        binding!!.passwordEt.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_baseline_visibility_off_24,
                            0
                        )

                        //for hide password
                        binding!!.passwordEt.transformationMethod =
                            PasswordTransformationMethod.getInstance()
                        passwordVisible = false
                    } else {
                        //set drawable image here
                        binding!!.passwordEt.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_baseline_visibility_24,
                            0
                        )

                        //for show password
                        binding!!.passwordEt.transformationMethod =
                            HideReturnsTransformationMethod.getInstance()
                        passwordVisible = true
                    }
                    binding!!.passwordEt.setSelection(selection)
                    return@OnTouchListener true
                }
            }
            false
        })
        binding!!.loginBtn.setOnClickListener { validateData() }
    }

    private var email = ""
    private var password = ""
    private fun validateData() {
        email = binding!!.emailEt.text.toString()
        password = binding!!.passwordEt.text.toString()
        if (TextUtils.isEmpty(email)) {
            binding!!.emailEt.error = "Email Required"
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding!!.emailEt.error = "Invalid Email Pattern"
        } else if (TextUtils.isEmpty(password)) {
            binding!!.passwordEt.error = "Password Required"
        } else {
            userSignIn()
        }
    }

    private fun userSignIn() {
        progressDialog!!.setMessage("Logging In....")
        progressDialog!!.show()
        mAuth!!.signInWithEmailAndPassword(email, password).addOnSuccessListener { checkUser() }
            .addOnFailureListener { e ->
                progressDialog!!.dismiss()
                Toast.makeText(this@LoginActivity, "" + e.message, Toast.LENGTH_SHORT).show()
            }
    }

    private fun checkUser() {
        progressDialog!!.setMessage("Checking User.....")
        val firebaseUser = mAuth!!.currentUser
        val reference = database!!.getReference("Users")
        reference.child(firebaseUser!!.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    progressDialog!!.dismiss()
                    val userType = "" + snapshot.child("usertype").value
                    if (userType == "user") {
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    } else if (userType == "admin") {
                        startActivity(Intent(this@LoginActivity, Admin_Home_Activity::class.java))
                        finish()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@LoginActivity, "" + error.message, Toast.LENGTH_SHORT)
                        .show()
                }
            })
    }
}