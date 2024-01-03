package com.radu.raduandnunocaffe

import android.app.ProgressDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.radu.raduandnunocaffe.databinding.ActivityOtpactivityBinding

class OTPActivity() : AppCompatActivity() {
    var binding: ActivityOtpactivityBinding? = null
    var progressDialog: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpactivityBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        progressDialog = ProgressDialog(this)
        progressDialog!!.setTitle("Please Wait")
        progressDialog!!.setCanceledOnTouchOutside(false)
        binding!!.resendTv.setOnClickListener(View.OnClickListener { finish() })
        binding!!.submitBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                if ((!binding!!.inputOtp1.text.toString().trim { it <= ' ' }
                        .isEmpty() && !binding!!.inputOtp2.text.toString().trim { it <= ' ' }
                        .isEmpty() && !binding!!.inputOtp3.text.toString().trim { it <= ' ' }
                        .isEmpty()
                            && !binding!!.inputOtp4.text.toString().trim { it <= ' ' }
                        .isEmpty() && !binding!!.inputOtp4.text.toString().trim { it <= ' ' }
                        .isEmpty() && !binding!!.inputOtp5.text.toString().trim { it <= ' ' }
                        .isEmpty() && !binding!!.inputOtp6.text.toString().trim { it <= ' ' }
                        .isEmpty())
                ) {
                    progressDialog!!.setMessage("Verifying Mobile.....")
                    progressDialog!!.show()
                    val enterdotp = (binding!!.inputOtp1.text.toString() +
                            binding!!.inputOtp2.text.toString() +
                            binding!!.inputOtp3.text.toString() +
                            binding!!.inputOtp4.text.toString() +
                            binding!!.inputOtp1.text.toString() +
                            binding!!.inputOtp1.text.toString())

                    //     binding.progressbarofotpauth.setVisibility(View.VISIBLE);
                    val codereciever = intent.getStringExtra("otp")
                    val countrycode1 = intent.getStringExtra("coutryCode")
                    val mobile = intent.getStringExtra("mobile")
                    val credential = PhoneAuthProvider.getCredential((codereciever)!!, enterdotp)
                    addphonenumbertodatabse(mobile)
                } else {
                    Toast.makeText(this@OTPActivity, "Enter All numbers", Toast.LENGTH_SHORT).show()
                }
            }
        })
        numberOtpMove()
    }

    private fun addphonenumbertodatabse(mobile: String?) {
        progressDialog!!.setMessage("updating status")
        progressDialog!!.show()
        val obj = HashMap<String, Any?>()
        obj["isMobileVerified"] = "true"
        obj["mobile"] = mobile
        val databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        databaseReference.child((FirebaseAuth.getInstance().uid)!!).updateChildren(obj)
            .addOnSuccessListener(object : OnSuccessListener<Void?> {
                override fun onSuccess(unused: Void?) {
                    progressDialog!!.dismiss()
                    Toast.makeText(
                        this@OTPActivity,
                        "Mobile Verify Successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            })
    }

    private fun numberOtpMove() {
        binding!!.inputOtp1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (!charSequence.toString().trim { it <= ' ' }.isEmpty()) {
                    binding!!.inputOtp2.requestFocus()
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        binding!!.inputOtp2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (!charSequence.toString().trim { it <= ' ' }.isEmpty()) {
                    binding!!.inputOtp3.requestFocus()
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        binding!!.inputOtp3.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (!charSequence.toString().trim { it <= ' ' }.isEmpty()) {
                    binding!!.inputOtp4.requestFocus()
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        binding!!.inputOtp4.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (!charSequence.toString().trim { it <= ' ' }.isEmpty()) {
                    binding!!.inputOtp5.requestFocus()
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        binding!!.inputOtp5.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (!charSequence.toString().trim { it <= ' ' }.isEmpty()) {
                    binding!!.inputOtp6.requestFocus()
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })
    }
}