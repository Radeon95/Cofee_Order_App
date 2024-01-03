package com.radu.raduandnunocaffe

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.radu.raduandnunocaffe.databinding.ActivityForgotPasswordBinding

class Forgot_Password_Activity : AppCompatActivity() {
    var binding: ActivityForgotPasswordBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        binding!!.backBtn.setOnClickListener { onBackPressed() }
    }
}