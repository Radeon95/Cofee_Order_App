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
import com.google.firebase.database.FirebaseDatabase
import com.radu.raduandnunocaffe.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    var binding: ActivityRegisterBinding? = null
    var mAuth: FirebaseAuth? = null
    var database: FirebaseDatabase? = null
    private var progressDialog: ProgressDialog? = null
    var passwordVisible = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        progressDialog = ProgressDialog(this)
        progressDialog!!.setTitle("Please Wait........")
        progressDialog!!.setCanceledOnTouchOutside(false)
        binding!!.backBtn.setOnClickListener { onBackPressed() }
        binding!!.haveAccount1Tv.setOnClickListener {
            startActivity(
                Intent(
                    this@RegisterActivity,
                    LoginActivity::class.java
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
        binding!!.registerBtn.setOnClickListener { validateData() }
    }

    private var username = ""
    private var email = ""
    private var password = ""
    private fun validateData() {
        username = binding!!.nameEt.text.toString()
        email = binding!!.emailEt.text.toString()
        password = binding!!.passwordEt.text.toString()
        if (TextUtils.isEmpty(username)) {
            binding!!.nameEt.error = "Username Required"
        } else if (TextUtils.isEmpty(email)) {
            binding!!.emailEt.error = "Email Required"
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding!!.emailEt.error = "Invalid Email Pattern"
        } else if (TextUtils.isEmpty(password)) {
            binding!!.passwordEt.error = "Password Required"
        } else {
            createUserAccount()
        }
    }

    private fun createUserAccount() {
        progressDialog!!.setMessage("Creating Account....")
        progressDialog!!.show()
        mAuth!!.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { updateUserInfo() }
            .addOnFailureListener { e ->
                progressDialog!!.dismiss()
                Toast.makeText(this@RegisterActivity, "" + e.message, Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateUserInfo() {
        progressDialog!!.setMessage("Saving user info....")
        val timestamp = System.currentTimeMillis()
        val uid = mAuth!!.uid
        val hashMap = HashMap<String, Any>()
        hashMap["username"] = username
        hashMap["email"] = email
        hashMap["password"] = password
        hashMap["uid"] = "" + uid
        hashMap["usertype"] = "user"
        hashMap["register_date"] = "" + timestamp
        val databaseReference = database!!.getReference("Users")
        databaseReference.child(uid!!).setValue(hashMap).addOnSuccessListener {
            progressDialog!!.dismiss()
            Toast.makeText(this@RegisterActivity, "Account Created....", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
        }.addOnFailureListener { e ->
            progressDialog!!.dismiss()
            Toast.makeText(this@RegisterActivity, "" + e.message, Toast.LENGTH_SHORT).show()
        }
    }
}