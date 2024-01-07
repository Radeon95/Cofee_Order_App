package com.radu.raduandnunocaffe

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.radu.raduandnunocaffe.Admin.Admin_Home_Activity
import com.radu.raduandnunocaffe.databinding.ActivitySplashBinding

class Splash_Activity : AppCompatActivity() {
    var binding: ActivitySplashBinding? = null
    var mAuth: FirebaseAuth? = null
    var database: FirebaseDatabase? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        val text = "COFFEE PLACE"
        val ss = SpannableString(text)
        val fcsBlue = ForegroundColorSpan(resources.getColor(R.color.black))
        val fcsYellow = ForegroundColorSpan(resources.getColor(R.color.brown))
        ss.setSpan(fcsBlue, 1, 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        ss.setSpan(fcsYellow, 7, 10, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding!!.text.text = ss
        val anim = AnimationUtils.loadAnimation(this, R.anim.splah_anim)
        binding!!.logo.animation = anim

        //   final Intent intent = new Intent(this,Welcome_Screen_Activity.class);
        val timer: Thread = object : Thread() {
            override fun run() {
                try {
                    sleep(2000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                } finally {
                    checkUser()
                    //                    startActivity(intent);
//                    finish();
                }
            }
        }
        timer.start()
    }

    private fun checkUser() {
        val firebaseUser = mAuth!!.currentUser
        if (firebaseUser == null) {
            startActivity(Intent(this@Splash_Activity, Welcome_Screen_Activity::class.java))
            finish()
        } else {
            val reference = database!!.getReference("Users")
            reference.child(firebaseUser.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val userType = "" + snapshot.child("usertype").value
                        if (userType == "user") {
                            startActivity(Intent(this@Splash_Activity, MainActivity::class.java))
                            finish()
                        } else {
                            startActivity(
                                Intent(
                                    this@Splash_Activity,
                                    Admin_Home_Activity::class.java
                                )
                            )
                            finish()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
        }
    }
}