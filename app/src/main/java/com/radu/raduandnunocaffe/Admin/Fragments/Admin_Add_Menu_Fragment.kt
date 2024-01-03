package com.radu.raduandnunocaffe.Admin.Fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.radu.raduandnunocaffe.R
import com.radu.raduandnunocaffe.Splash_Activity
import com.radu.raduandnunocaffe.databinding.FragmentAdminAddMenuBinding
import java.text.SimpleDateFormat
import java.util.Calendar

class Admin_Add_Menu_Fragment : Fragment() {
    var binding: FragmentAdminAddMenuBinding? = null
    private var progressDialog: ProgressDialog? = null
    var mAuth: FirebaseAuth? = null
    var database: FirebaseDatabase? = null
    var storageReference: FirebaseStorage? = null
    private var coffeeName = ""
    private var coffeeQuantity = ""
    private var coffeePrice = ""
    var isCustomizeAvailable = false
    private var image_uri: Uri? = null
    private lateinit var cameraPermissions: Array<String>
    private lateinit var storagePermissions: Array<String>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAdminAddMenuBinding.inflate(inflater, container, false)
        cameraPermissions =
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        storagePermissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        storageReference = FirebaseStorage.getInstance()
        progressDialog = ProgressDialog(context)
        progressDialog!!.setTitle("Please Wait...")
        progressDialog!!.setCanceledOnTouchOutside(false)
        binding!!.logOutBtn.setOnClickListener { LoadLogoutDialog() }
        binding!!.imgProduct.setOnClickListener { showImagePickerDialog() }
        binding!!.customizeSwitch.setOnCheckedChangeListener { compoundButton, b ->
            isCustomizeAvailable = if (b) {
                true
            } else {
                false
            }
        }
        binding!!.addCoffeeBtn.setOnClickListener { ValidateData() }
        return binding!!.root
    }

    private fun LoadLogoutDialog() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("LogOut")
        builder.setMessage("Do you want to logout?")
        builder.setPositiveButton("Yes") { dialogInterface, i ->
            mAuth!!.signOut()
            startActivity(Intent(context, Splash_Activity::class.java))
            activity!!.finish()
        }
            .setNegativeButton("No") { dialogInterface, i -> dialogInterface.dismiss() }
        builder.show()
    }

    private fun ValidateData() {
        coffeeName = binding!!.coffeeName.text.toString()
        coffeeQuantity = binding!!.coffeeQuantity.text.toString()
        coffeePrice = binding!!.coffeePrice.text.toString()
        if (TextUtils.isEmpty(coffeeName)) {
            binding!!.coffeeName.error = "Coffee Name Required"
        } else if (TextUtils.isEmpty(coffeeQuantity)) {
            binding!!.coffeeQuantity.error = "Coffee Quantity Required"
        } else if (TextUtils.isEmpty(coffeePrice)) {
            binding!!.coffeePrice.error = "Coffee Price Required"
        } else if (image_uri == null) {
            Toast.makeText(context, "Please select coffee image", Toast.LENGTH_SHORT).show()
        } else {
            addCoffee()
        }
    }

    private fun addCoffee() {
        progressDialog!!.setMessage("Saving Product Details with Image")
        progressDialog!!.show()
        val timestamp = "" + System.currentTimeMillis()
        val calendar2 = Calendar.getInstance()
        val currentTime = SimpleDateFormat("HH:mm:ss")
        val time = currentTime.format(calendar2.time)

        //upload with image
        val filepathname = "Product_images/$timestamp"
        val databaseReference = database!!.getReference("CoffeeMenu")
        storageReference!!.getReference(filepathname).putFile(image_uri!!)
            .addOnSuccessListener { taskSnapshot ->
                val uriTask = taskSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val downloadImageUri = uriTask.result
                if (uriTask.isSuccessful) {
                    val hashMap = HashMap<String, Any>()
                    hashMap["coffeeId"] = "" + timestamp
                    hashMap["coffee_name"] = "" + coffeeName
                    hashMap["quantity"] = "" + coffeeQuantity
                    hashMap["price"] = "" + coffeePrice
                    hashMap["coffee_image"] = downloadImageUri.toString()
                    hashMap["isCustomizeCusAvailable"] = "" + isCustomizeAvailable
                    hashMap["timestamp"] = "" + time
                    databaseReference.child(timestamp).setValue(hashMap).addOnSuccessListener {
                        progressDialog!!.dismiss()
                        val toast =
                            Toast.makeText(context, "Coffee Added Successfully", Toast.LENGTH_SHORT)
                        toast.view!!.backgroundTintList = resources.getColorStateList(R.color.brown)
                        toast.show()
                        ClearData()
                    }.addOnFailureListener { e ->
                        progressDialog!!.dismiss()
                        Toast.makeText(context, "" + e.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    private fun ClearData() {
        binding!!.imgProduct.setImageResource(R.drawable.more)
        image_uri = null
        binding!!.coffeeName.setText("")
        binding!!.coffeeQuantity.setText("")
        binding!!.coffeePrice.setText("")
        binding!!.customizeSwitch.isChecked = false
    }

    private fun showImagePickerDialog() {
        val options = arrayOf("camera", "Gallery")
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Pick Image").setItems(options) { dialogInterface, i ->
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
        builder.show()
    }

    private fun requestStoragePermission() {
        ActivityCompat.requestPermissions(
            (context as Activity?)!!,
            storagePermissions,
            STORAGE_REQUEST_CODE
        )
    }

    private fun pickFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.setType("image/*")
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE)
    }

    private fun checkStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context!!,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            (context as Activity?)!!,
            cameraPermissions,
            CAMERA_REQUEST_CODE
        )
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

    private fun checkCameraPermissions(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            context!!,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        val result1 = ContextCompat.checkSelfPermission(
            context!!,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        return result && result1
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                image_uri = data!!.data
                binding!!.imgProduct.setImageURI(image_uri)
            } else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                binding!!.imgProduct.setImageURI(image_uri)
            }
        }
    }

    companion object {
        private const val CAMERA_REQUEST_CODE = 200
        private const val STORAGE_REQUEST_CODE = 300
        private const val IMAGE_PICK_GALLERY_CODE = 400
        private const val IMAGE_PICK_CAMERA_CODE = 500
    }
}