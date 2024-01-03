package com.radu.raduandnunocaffe.Fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.radu.raduandnunocaffe.OTPActivity
import com.radu.raduandnunocaffe.R
import com.radu.raduandnunocaffe.Splash_Activity
import com.radu.raduandnunocaffe.databinding.FragmentProfileBinding
import com.squareup.picasso.Picasso
import java.util.concurrent.TimeUnit

class ProfileFragment() : Fragment() {
    var binding: FragmentProfileBinding? = null
    var mAuth: FirebaseAuth? = null
    var database: FirebaseDatabase? = null
    var storageReference: FirebaseStorage? = null
    var progressDialog: ProgressDialog? = null
    var dialog: Dialog? = null
    private lateinit var cameraPermissions: Array<String>
    private lateinit var storagePermissions: Array<String>
    private var image_uri: Uri? = null
    private var countryCode: String? = null
    private var phoneNumber: String? = null
    private var codeSent: String? = null
    var mCallbacks: OnVerificationStateChangedCallbacks? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        storageReference = FirebaseStorage.getInstance()
        progressDialog = ProgressDialog(context)
        progressDialog!!.setTitle("Please Wait")
        progressDialog!!.setCanceledOnTouchOutside(false)
        countryCode = binding!!.countrycodepicker.selectedCountryCodeWithPlus
        cameraPermissions =
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        storagePermissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        LoadUserData()
        binding!!.logoutBtn.setOnClickListener { LoadLogoutDialogBox() }
        binding!!.selectPic.setOnClickListener { showImagePickerDialog() }
        binding!!.updateBtn.setOnClickListener { ValidateData() }
        binding!!.verifyMobile.setOnClickListener {
            val number: String
            number = binding!!.mobileEt.text.toString()
            if (number.isEmpty()) {
                Toast.makeText(context, "Please Enter Your Number", Toast.LENGTH_SHORT).show()
            } else if (number.length < 10) {
                Toast.makeText(context, "Please Enter Correct Number", Toast.LENGTH_SHORT).show()
            } else {
                progressDialog!!.setMessage("Sending OTP.....")
                progressDialog!!.show()
                phoneNumber = countryCode + number
                val options = PhoneAuthOptions.newBuilder(mAuth!!).setPhoneNumber(
                    phoneNumber!!
                )
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity((activity)!!)
                    .setCallbacks((mCallbacks)!!)
                    .build()
                PhoneAuthProvider.verifyPhoneNumber(options)
            }
        }
        mCallbacks = object : OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                progressDialog!!.dismiss()
                Toast.makeText(context, "OTP is Sent Completed", Toast.LENGTH_SHORT).show()
            }

            override fun onVerificationFailed(e: FirebaseException) {
                progressDialog!!.dismiss()
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                Log.d("TAG", (e.message)!!)
            }

            override fun onCodeSent(s: String, forceResendingToken: ForceResendingToken) {
                super.onCodeSent(s, forceResendingToken)
                Toast.makeText(context, "OTP is Sent Completed", Toast.LENGTH_SHORT).show()
                progressDialog!!.dismiss()
                codeSent = s
                val intent = Intent(context, OTPActivity::class.java)
                intent.putExtra("otp", codeSent)
                intent.putExtra("coutryCode", countryCode)
                intent.putExtra("mobile", binding!!.mobileEt.text.toString())
                startActivity(intent)
            }
        }
        binding!!.countrycodepicker.setOnCountryChangeListener {
            countryCode = binding!!.countrycodepicker.selectedCountryCodeWithPlus
        }
        return binding!!.root
    }

    private var username = ""
    private var address = ""
    private var mobile = ""
    private fun ValidateData() {
        username = binding!!.usernameEt.text.toString().trim { it <= ' ' }
        address = binding!!.addressEt.text.toString().trim { it <= ' ' }
        mobile = binding!!.mobileEt.text.toString().trim { it <= ' ' }
        if (TextUtils.isEmpty(username)) {
            binding!!.usernameEt.error = "Username Required"
        } else if (TextUtils.isEmpty(address)) {
            binding!!.addressEt.error = "Address Required"
        } else if (TextUtils.isEmpty(mobile)) {
            binding!!.mobileEt.error = "Required Contact No"
        } else if (mobile.length < 10) {
            binding!!.mobileEt.error = "Invalid Number"
        } else {
            updateData()
        }
    }

    private fun updateData() {
        progressDialog!!.setMessage("Updating user data....")
        progressDialog!!.show()
        val databaseReference = database!!.getReference("Users")
        val timestamp = System.currentTimeMillis()
        if (image_uri == null) {
            if ((verifyedMobile == binding!!.mobileEt.text.toString())) {
                val hashMap = HashMap<String, Any?>()
                hashMap["username"] = username
                hashMap["address"] = address
                hashMap["mobile"] = mobile
                hashMap["countryCode"] = countryCode
                hashMap["isMobileVerified"] = "true"
                databaseReference.child((mAuth!!.uid)!!).updateChildren(hashMap)
                    .addOnSuccessListener(
                        OnSuccessListener {
                            progressDialog!!.dismiss()
                            Toast.makeText(
                                context,
                                "Profile Updated Successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                        })
            } else {
                val hashMap = HashMap<String, Any?>()
                hashMap["username"] = username
                hashMap["address"] = address
                hashMap["mobile"] = mobile
                hashMap["countryCode"] = countryCode
                hashMap["isMobileVerified"] = "false"
                databaseReference.child((mAuth!!.uid)!!).updateChildren(hashMap)
                    .addOnSuccessListener(object : OnSuccessListener<Void?> {
                        override fun onSuccess(unused: Void?) {
                            progressDialog!!.dismiss()
                            Toast.makeText(
                                context,
                                "Profile Updated Successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
            }
        } else {
            progressDialog!!.setMessage("Updating user data with profile pic....")
            progressDialog!!.show()
            val filepathname = "Profile_image/$timestamp"
            storageReference!!.getReference(filepathname).putFile(image_uri!!)
                .addOnSuccessListener(object : OnSuccessListener<UploadTask.TaskSnapshot> {
                    override fun onSuccess(taskSnapshot: UploadTask.TaskSnapshot) {
                        val uriTask = taskSnapshot.storage.downloadUrl
                        while (!uriTask.isSuccessful);
                        val downloadImageUri = uriTask.result
                        if (uriTask.isSuccessful) {
                            if ((verifyedMobile == binding!!.mobileEt.text.toString())) {
                                val hashMap = HashMap<String, Any?>()
                                hashMap["username"] = username
                                hashMap["address"] = address
                                hashMap["mobile"] = mobile
                                hashMap["countryCode"] = countryCode
                                hashMap["isMobileVerified"] = "true"
                                hashMap["profile_pic"] = downloadImageUri.toString()
                                databaseReference.child((mAuth!!.uid)!!).updateChildren(hashMap)
                                    .addOnSuccessListener(object : OnSuccessListener<Void?> {
                                        override fun onSuccess(unused: Void?) {
                                            progressDialog!!.dismiss()
                                            Toast.makeText(
                                                context,
                                                "Profile Updated Successfully",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }).addOnFailureListener(object : OnFailureListener {
                                    override fun onFailure(e: Exception) {
                                        progressDialog!!.dismiss()
                                        Toast.makeText(context, "" + e.message, Toast.LENGTH_SHORT)
                                            .show()
                                    }
                                })
                            } else {
                                val hashMap = HashMap<String, Any?>()
                                hashMap["username"] = username
                                hashMap["address"] = address
                                hashMap["mobile"] = mobile
                                hashMap["countryCode"] = countryCode
                                hashMap["isMobileVerified"] = "false"
                                hashMap["profile_pic"] = downloadImageUri.toString()
                                databaseReference.child((mAuth!!.uid)!!).updateChildren(hashMap)
                                    .addOnSuccessListener(object : OnSuccessListener<Void?> {
                                        override fun onSuccess(unused: Void?) {
                                            progressDialog!!.dismiss()
                                            Toast.makeText(
                                                context,
                                                "Profile Updated Successfully",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }).addOnFailureListener(object : OnFailureListener {
                                    override fun onFailure(e: Exception) {
                                        progressDialog!!.dismiss()
                                        Toast.makeText(context, "" + e.message, Toast.LENGTH_SHORT)
                                            .show()
                                    }
                                })
                            }
                        } else {
                            val hashMap = HashMap<String, Any?>()
                            hashMap["username"] = username
                            hashMap["address"] = address
                            hashMap["mobile"] = mobile
                            hashMap["countryCode"] = countryCode
                            hashMap["isMobileVerified"] = "false"
                            hashMap["profile_pic"] = downloadImageUri.toString()
                            databaseReference.child((mAuth!!.uid)!!).updateChildren(hashMap)
                                .addOnSuccessListener(object : OnSuccessListener<Void?> {
                                    override fun onSuccess(unused: Void?) {
                                        progressDialog!!.dismiss()
                                        Toast.makeText(
                                            context,
                                            "Profile Updated Successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }).addOnFailureListener(object : OnFailureListener {
                                override fun onFailure(e: Exception) {
                                    progressDialog!!.dismiss()
                                    Toast.makeText(context, "" + e.message, Toast.LENGTH_SHORT)
                                        .show()
                                }
                            })
                        }
                    }
                }).addOnFailureListener(object : OnFailureListener {
                override fun onFailure(e: Exception) {
                    progressDialog!!.dismiss()
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun LoadUserData() {
        val databaseReference = database!!.getReference("Users")
        databaseReference.child((mAuth!!.uid)!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val username = "" + snapshot.child("username").value
                val email = "" + snapshot.child("email").value
                val profile_pic = "" + snapshot.child("profile_pic").value
                val mobile = "" + snapshot.child("mobile").value
                val address = "" + snapshot.child("address").value
                val countryCode = "" + snapshot.child("countryCode").value
                val isMobileVerified = "" + snapshot.child("isMobileVerified").value
                if ((isMobileVerified == "true")) {
                    verifyedMobile = "" + snapshot.child("mobile").value
                } else {
                }
                if ((countryCode == "" + null)) {
                } else {
                    binding!!.countrycodepicker.setCountryForPhoneCode(countryCode.toInt())
                }
                binding!!.emailEt.setText(email)
                binding!!.usernameEt.setText(username)
                binding!!.usernameTv.text = username
                if ((mobile == "" + null)) {
                    binding!!.mobileEt.setText("")
                } else {
                    binding!!.mobileEt.setText(mobile)
                }
                if ((address == "" + null)) {
                    binding!!.addressEt.setText("")
                } else {
                    binding!!.addressEt.setText(address)
                }
                try {
                    Picasso.get().load(profile_pic).placeholder(R.drawable.man)
                        .into(binding!!.profileIv)
                } catch (e: Exception) {
                    binding!!.profileIv.setImageResource(R.drawable.man)
                }
                if (snapshot.exists()) {
                    try {
                        if ((isMobileVerified == "true")) {
                            binding!!.verifyMobile.visibility = View.GONE
                        } else if ((isMobileVerified == "false")) {
                            binding!!.verifyMobile.visibility = View.VISIBLE
                        } else {
                            binding!!.verifyMobile.visibility = View.GONE
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun showImagePickerDialog() {
        val options = arrayOf("camera", "Gallery")
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Pick Image").setItems(options, object : DialogInterface.OnClickListener {
            override fun onClick(dialogInterface: DialogInterface, i: Int) {
                if (i == 0) {
                    if (checkCameraPermissions()) {
                        pickFromCamera()
                    } else {
                        requestCameraPermission()
                    }
                } else {
                    if (checkStoragePermission()) {
                        pickFromGallery()
                    } else {
                        requestStoragePermission()
                    }
                }
            }
        })
        builder.show()
    }

    private fun pickFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.setType("image/*")
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE)
    }

    private fun pickFromCamera() {
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_Image_Title")
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp_Image_Description")
        image_uri = context!!.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE)
    }

    private fun checkStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            (context)!!,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == (PackageManager.PERMISSION_GRANTED)
    }

    private fun requestStoragePermission() {
        ActivityCompat.requestPermissions((activity)!!, storagePermissions, STORAGE_REQUEST_CODE)
    }

    private fun checkCameraPermissions(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            (context)!!,
            Manifest.permission.CAMERA
        ) == (PackageManager.PERMISSION_GRANTED)
        val result1 = ContextCompat.checkSelfPermission(
            (context)!!,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == (PackageManager.PERMISSION_GRANTED)
        return result && result1
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions((activity)!!, cameraPermissions, CAMERA_REQUEST_CODE)
    }

    private fun LoadLogoutDialogBox() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Logout")
        builder.setMessage("Do you want to logout?")
        builder.setPositiveButton("Yes", object : DialogInterface.OnClickListener {
            override fun onClick(dialogInterface: DialogInterface, i: Int) {
                mAuth!!.signOut()
                startActivity(Intent(context, Splash_Activity::class.java))
                activity!!.finish()
            }
        }).setNegativeButton("No", object : DialogInterface.OnClickListener {
            override fun onClick(dialogInterface: DialogInterface, i: Int) {
                dialogInterface.dismiss()
            }
        })
        builder.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                image_uri = data!!.data
                binding!!.profileIv.setImageURI(image_uri)
            } else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                binding!!.profileIv.setImageURI(image_uri)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onStart() {
        super.onStart()
        LoadVerifyedMobileNumber()
    }

    private var verifyedMobile: String? = null
    private fun LoadVerifyedMobileNumber() {
        val databaseReference = database!!.getReference("Users")
        databaseReference.child((mAuth!!.uid)!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    verifyedMobile = "" + snapshot.child("isMobileVerified").value
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    companion object {
        private val CAMERA_REQUEST_CODE = 100
        private val STORAGE_REQUEST_CODE = 200
        private val IMAGE_PICK_GALLERY_CODE = 300
        private val IMAGE_PICK_CAMERA_CODE = 400
    }
}