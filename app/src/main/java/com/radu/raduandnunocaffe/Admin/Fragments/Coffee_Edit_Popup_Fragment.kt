package com.radu.raduandnunocaffe.Admin.Fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.DialogInterface
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
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.radu.raduandnunocaffe.R
import com.radu.raduandnunocaffe.databinding.FragmentCoffeeEditPopupBinding
import com.squareup.picasso.Picasso
import fr.tvbarthel.lib.blurdialogfragment.SupportBlurDialogFragment

class Coffee_Edit_Popup_Fragment() : SupportBlurDialogFragment() {
    var binding: FragmentCoffeeEditPopupBinding? = null
    private var coffeeImage: String? = null
    private var coffeeName: String? = null
    private var coffeePrice: String? = null
    private var isCustomizeAvailable: String? = null
    private var Quantity: String? = null
    private var coffeeId: String? = null
    private var quantityEt: String? = null
    private var coffeeNameEt: String? = null
    private var coffeePriceEt: String? = null
    var database: FirebaseDatabase? = null
    var storageReference: FirebaseStorage? = null
    private var image_uri: Uri? = null
    private var checkCustomizeAvailable = false
    private lateinit var cameraPermissions: Array<String>
    private lateinit var storagePermissions: Array<String>
    var progressDialog: ProgressDialog? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCoffeeEditPopupBinding.inflate(inflater, container, false)
        cameraPermissions =
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        storagePermissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        progressDialog = ProgressDialog(context)
        progressDialog!!.setTitle("Please Wait...")
        progressDialog!!.setCanceledOnTouchOutside(false)
        database = FirebaseDatabase.getInstance()
        storageReference = FirebaseStorage.getInstance()
        val bundle = this.arguments
        if (bundle != null) {
            coffeeImage = bundle.getString("coffeeImage")
            coffeeName = bundle.getString("coffeeName")
            coffeePrice = bundle.getString("coffeePrice")
            isCustomizeAvailable = bundle.getString("isCustomizeAvailable")
            Quantity = bundle.getString("Quantity")
            coffeeId = bundle.getString("coffeeId")
            try {
                Picasso.get().load(coffeeImage).placeholder(R.drawable.spinner)
                    .into(binding!!.imgProduct)
            } catch (e: Exception) {
                binding!!.imgProduct.setImageResource(R.drawable.spinner)
            }
            binding!!.coffeeName.setText(coffeeName)
            binding!!.coffeeQuantity.setText("$Quantity ml")
            binding!!.coffeePrice.setText(coffeePrice)
            if ((isCustomizeAvailable == "true")) {
                binding!!.customizeSwitch.isChecked = true
            } else {
                binding!!.customizeSwitch.isChecked = false
            }
        }
        binding!!.closeBtn.setOnClickListener { dismiss() }
        binding!!.imgProduct.setOnClickListener { showImagePickerDialog() }
        binding!!.customizeSwitch.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                checkCustomizeAvailable = true
            } else {
                checkCustomizeAvailable = false
            }
        }
        binding!!.updateCoffeeBtn.setOnClickListener { ValidateData() }
        return binding!!.root
    }

    private fun ValidateData() {
        coffeeNameEt = binding!!.coffeeName.text.toString()
        quantityEt = binding!!.coffeeQuantity.text.toString().replace("ml", "")
        coffeePriceEt = binding!!.coffeePrice.text.toString()
        if (TextUtils.isEmpty(coffeeName)) {
            binding!!.coffeeName.error = "Coffee Name Required"
        } else if (TextUtils.isEmpty(quantityEt)) {
            binding!!.coffeeQuantity.error = "Coffee Quantity Required"
        } else if (TextUtils.isEmpty(coffeePriceEt)) {
            binding!!.coffeePrice.error = "Coffee Price Required"
        } else {
            UpdateCoffee()
        }
    }

    private fun UpdateCoffee() {
        progressDialog!!.setMessage("Coffee Updating")
        progressDialog!!.show()
        val databaseReference = FirebaseDatabase.getInstance().getReference("CoffeeMenu")
        if (image_uri == null) {
            val hashMap = HashMap<String, Any>()
            hashMap["coffee_name"] = "" + coffeeNameEt
            hashMap["quantity"] = "" + quantityEt
            hashMap["price"] = "" + coffeePriceEt
            hashMap["isCustomizeCusAvailable"] = "" + checkCustomizeAvailable
            databaseReference.child((coffeeId)!!).updateChildren(hashMap).addOnSuccessListener {
                progressDialog!!.dismiss()
                Toast.makeText(context, "Updated", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener { e ->
                progressDialog!!.dismiss()
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }
        } else {
            progressDialog!!.setMessage("Updating Coffee Details with Image")
            progressDialog!!.show()
            val timestamp = "" + System.currentTimeMillis()

            //upload with image
            val filepathname = "Product_images/$timestamp"
            storageReference!!.getReference(filepathname).putFile(image_uri!!)
                .addOnSuccessListener(object : OnSuccessListener<UploadTask.TaskSnapshot> {
                    override fun onSuccess(taskSnapshot: UploadTask.TaskSnapshot) {
                        val uriTask = taskSnapshot.storage.downloadUrl
                        while (!uriTask.isSuccessful);
                        val downloadImageUri = uriTask.result
                        if (uriTask.isSuccessful) {
                            val hashMap = HashMap<String, Any>()
                            hashMap["coffee_name"] = "" + coffeeNameEt
                            hashMap["quantity"] = "" + quantityEt
                            hashMap["price"] = "" + coffeePriceEt
                            hashMap["coffee_image"] = downloadImageUri.toString()
                            hashMap["isCustomizeCusAvailable"] = "" + checkCustomizeAvailable
                            databaseReference.child((coffeeId)!!).updateChildren(hashMap)
                                .addOnSuccessListener(
                                    OnSuccessListener {
                                        progressDialog!!.dismiss()
                                        val toast = Toast.makeText(
                                            context,
                                            "Coffee Updated Successfully",
                                            Toast.LENGTH_SHORT
                                        )
                                        toast.view!!.backgroundTintList =
                                            resources.getColorStateList(R.color.brown)
                                        toast.show()
                                    }).addOnFailureListener(object : OnFailureListener {
                                override fun onFailure(e: Exception) {
                                    progressDialog!!.dismiss()
                                    Toast.makeText(context, "" + e.message, Toast.LENGTH_SHORT)
                                        .show()
                                }
                            })
                        }
                    }
                })
        }
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
            (context)!!,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == (PackageManager.PERMISSION_GRANTED)
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
            (context)!!,
            Manifest.permission.CAMERA
        ) == (PackageManager.PERMISSION_GRANTED)
        val result1 = ContextCompat.checkSelfPermission(
            (context)!!,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == (PackageManager.PERMISSION_GRANTED)
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
        private val CAMERA_REQUEST_CODE = 200
        private val STORAGE_REQUEST_CODE = 300
        private val IMAGE_PICK_GALLERY_CODE = 400
        private val IMAGE_PICK_CAMERA_CODE = 500
    }
}