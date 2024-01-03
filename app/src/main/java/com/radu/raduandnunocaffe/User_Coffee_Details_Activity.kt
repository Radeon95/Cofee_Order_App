package com.radu.raduandnunocaffe

import android.app.AlertDialog
import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.radu.raduandnunocaffe.databinding.ActivityUserCoffeeDetailsBinding
import com.squareup.picasso.Picasso

class User_Coffee_Details_Activity() : AppCompatActivity() {
    var binding: ActivityUserCoffeeDetailsBinding? = null
    private var coffeeName: String? = null
    private var coffeePrice: String? = null
    private var coffeeImage: String? = null
    private var isCustomizeAvailable: String? = null
    private var coffeeId: String? = null
    private var Quantity = 1
    private var finalCost = 0
    private var cost = 0
    var smallSizeChecked = false
    var mediumSizeChecked = false
    var largeSizeChecked = false
    var mAuth: FirebaseAuth? = null
    var database: FirebaseDatabase? = null
    var noSugarChecked = false
    var smallSugarChecked = false
    var mediumSugarChecked = false
    var highSugarChecked = false
    private var smallSizeCost = 0.0
    private var mediumSizeCost = 0.0
    private var largeSizeCost = 0.0
    private var noSugarCost = 0.0
    private var smallSugarCost = 0.0
    private var mediumSugarCost = 0.0
    private var highSugarCost = 0.0
    private var additionalCreamCost = 0.0
    private var additionalStickerCost = 0.0
    private var finalSizeCost = 0.0
    private var finalSugarCost = 0.0
    private var finalAdditionalCost = 0.0
    private var selectedSize: String? = null
    private var selectedSugar: String? = null
    private var selectedAdditions = "empty"
    var progressDialog: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserCoffeeDetailsBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        setSupportActionBar(binding!!.toolbar)
        progressDialog = ProgressDialog(this)
        progressDialog!!.setTitle("Please Wait")
        progressDialog!!.setCanceledOnTouchOutside(false)
        coffeeName = intent.getStringExtra("coffeeName")
        coffeePrice = intent.getStringExtra("coffeePrice")
        coffeeImage = intent.getStringExtra("coffeeImage")
        coffeeId = intent.getStringExtra("coffeeId")
        isCustomizeAvailable = intent.getStringExtra("isCustomizeAvailable")
        try {
            Picasso.get().load(coffeeImage).placeholder(R.drawable.spinner)
                .into(binding!!.CoffeeImage)
        } catch (e: Exception) {
            binding!!.CoffeeImage.setImageResource(R.drawable.spinner)
        }
        binding!!.coffeeNameEt.text = coffeeName
        binding!!.coffeeNameTwo.text = coffeeName
        binding!!.priceTv.text = "Rs.$coffeePrice"
        binding!!.totalPriceTv.text = "Rs.$coffeePrice"
        if ((isCustomizeAvailable == "true")) {
            binding!!.mainRl.visibility = View.VISIBLE
            binding!!.sugarMainRl.visibility = View.VISIBLE
            binding!!.additionMainRl.visibility = View.VISIBLE
        } else {
            binding!!.mainRl.visibility = View.GONE
            binding!!.view2.visibility = View.GONE
            binding!!.sugarMainRl.visibility = View.GONE
            binding!!.view3.visibility = View.GONE
            binding!!.additionMainRl.visibility = View.GONE
            binding!!.view4.visibility = View.GONE
        }
        cost = Integer.valueOf(coffeePrice)
        finalCost = Integer.valueOf(coffeePrice)
        binding!!.btnIncrement.setOnClickListener {
            binding!!.radioGroup2.clearCheck()
            binding!!.radioGroup3.clearCheck()
            binding!!.radioGroup4.clearCheck()
            finalCost = finalCost + cost
            Quantity++
            binding!!.totalPriceTv.text = "Rs.$finalCost"
            binding!!.quantityTv.text = "" + Quantity
        }
        binding!!.btnDecrement.setOnClickListener {
            binding!!.radioGroup2.clearCheck()
            binding!!.radioGroup3.clearCheck()
            binding!!.radioGroup4.clearCheck()
            if (Quantity > 1) {
                finalCost = finalCost - cost
                Quantity--
                binding!!.totalPriceTv.text = "Rs.$finalCost"
                binding!!.quantityTv.text = Quantity.toString()
            }
        }
        binding!!.smallSizeRB.setOnClickListener {
            selectedSize = "small"
            binding!!.radioGroup3.clearCheck()
            binding!!.radioGroup4.clearCheck()
            smallSizeChecked = true
            mediumSizeChecked = false
            largeSizeChecked = false
            smallSizeCost = finalCost * 0.1
            finalSizeCost = finalCost + smallSizeCost
            binding!!.totalPriceTv.text = "Rs.$finalSizeCost"
        }
        binding!!.mediumSizeRB.setOnClickListener {
            selectedSize = "medium"
            smallSizeChecked = false
            mediumSizeChecked = true
            largeSizeChecked = false
            binding!!.radioGroup3.clearCheck()
            binding!!.radioGroup4.clearCheck()
            mediumSizeCost = finalCost * 0.2
            finalSizeCost = finalCost + mediumSizeCost
            binding!!.totalPriceTv.text = "Rs.$finalSizeCost"
        }
        binding!!.largeSizeRB.setOnClickListener {
            selectedSize = "large"
            smallSizeChecked = false
            mediumSizeChecked = false
            largeSizeChecked = true
            binding!!.radioGroup3.clearCheck()
            binding!!.radioGroup4.clearCheck()
            largeSizeCost = finalCost * 0.3
            finalSizeCost = finalCost + largeSizeCost
            binding!!.totalPriceTv.text = "Rs.$finalSizeCost"
        }
        binding!!.noSugarRB.setOnClickListener {
            selectedSugar = "noSugar"
            highSugarChecked = false
            noSugarChecked = true
            smallSugarChecked = false
            mediumSugarChecked = false
            if ((isCustomizeAvailable == "true")) {
                if (smallSizeChecked || mediumSizeChecked || largeSizeChecked) {
                    noSugarCost = finalCost * (-0.1) //-10%
                    finalSugarCost = finalSizeCost + noSugarCost
                    binding!!.totalPriceTv.text = "Rs.$finalSugarCost"
                    binding!!.radioGroup3.clearCheck()
                    binding!!.radioGroup4.clearCheck()
                    binding!!.noSugarRB.isChecked = true
                } else {
                    Toast.makeText(
                        this@User_Coffee_Details_Activity,
                        "Please Select Size",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding!!.noSugarRB.isChecked = false
                }
            } else {
                noSugarCost = finalCost * (-0.1) //-10%
                finalSugarCost = finalCost + noSugarCost
                binding!!.totalPriceTv.text = "Rs.$finalSugarCost"
            }
        }
        binding!!.smallSugarRB.setOnClickListener {
            selectedSugar = "smallSugar"
            highSugarChecked = false
            noSugarChecked = false
            smallSugarChecked = true
            mediumSugarChecked = false
            if ((isCustomizeAvailable == "true")) {
                if (smallSizeChecked || mediumSizeChecked || largeSizeChecked) {
                    smallSugarCost = finalCost * 0.01 //-10%
                    finalSugarCost = finalSizeCost + smallSugarCost
                    binding!!.totalPriceTv.text = "Rs.$finalSugarCost"
                    binding!!.radioGroup3.clearCheck()
                    binding!!.radioGroup4.clearCheck()
                    binding!!.smallSugarRB.isChecked = true
                } else {
                    Toast.makeText(
                        this@User_Coffee_Details_Activity,
                        "Please Select Size",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding!!.smallSugarRB.isChecked = false
                }
            } else {
                smallSugarCost = finalCost * 0.01 //-10%
                finalSugarCost = finalCost + smallSugarCost
                binding!!.totalPriceTv.text = "Rs.$finalSugarCost"
            }
        }
        binding!!.mediumSugarRB.setOnClickListener {
            selectedSugar = "mediumSugar"
            highSugarChecked = false
            noSugarChecked = false
            smallSugarChecked = false
            mediumSugarChecked = true
            if ((isCustomizeAvailable == "true")) {
                if (smallSizeChecked || mediumSizeChecked || largeSizeChecked) {
                    mediumSugarCost = finalCost * 0.02 //-10%
                    finalSugarCost = finalSizeCost + mediumSugarCost
                    binding!!.totalPriceTv.text = "Rs.$finalSugarCost"
                    binding!!.radioGroup3.clearCheck()
                    binding!!.radioGroup4.clearCheck()
                    binding!!.mediumSugarRB.isChecked = true
                } else {
                    Toast.makeText(
                        this@User_Coffee_Details_Activity,
                        "Please Select Size",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding!!.mediumSugarRB.isChecked = false
                }
            } else {
                mediumSugarCost = finalCost * 0.02 //-10%
                finalSugarCost = finalCost + mediumSugarCost
                binding!!.totalPriceTv.text = "Rs.$finalSugarCost"
            }
        }
        binding!!.highSugarRB.setOnClickListener {
            selectedSugar = "highSugar"
            highSugarChecked = true
            noSugarChecked = false
            smallSugarChecked = false
            mediumSugarChecked = false
            if ((isCustomizeAvailable == "true")) {
                if (smallSizeChecked || mediumSizeChecked || largeSizeChecked) {
                    highSugarCost = finalCost * 0.03 //-10%
                    finalSugarCost = finalSizeCost + highSugarCost
                    binding!!.totalPriceTv.text = "Rs.$finalSugarCost"
                    binding!!.radioGroup3.clearCheck()
                    binding!!.radioGroup4.clearCheck()
                    binding!!.highSugarRB.isChecked = true
                } else {
                    Toast.makeText(
                        this@User_Coffee_Details_Activity,
                        "Please Select Size",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding!!.highSugarRB.isChecked = false
                }
            } else {
                highSugarCost = finalCost * 0.03 //-10%
                finalSugarCost = finalCost + highSugarCost
                binding!!.totalPriceTv.text = "Rs.$finalSugarCost"
            }
        }
        binding!!.additionCreamRB.setOnClickListener {
            selectedAdditions = "cream"
            if ((isCustomizeAvailable == "true")) {
                if (noSugarChecked || smallSugarChecked || mediumSugarChecked || highSugarChecked) {
                    additionalCreamCost = finalCost * 0.05
                    finalAdditionalCost = finalSugarCost + additionalCreamCost
                    binding!!.totalPriceTv.text = "Rs.$finalAdditionalCost"
                    binding!!.radioGroup4.clearCheck()
                    binding!!.additionCreamRB.isChecked = true
                } else {
                    Toast.makeText(
                        this@User_Coffee_Details_Activity,
                        "Please Select Sugar",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding!!.additionCreamRB.isChecked = false
                }
            } else {
                additionalCreamCost = finalCost * 0.05 //-10%
                finalAdditionalCost = finalSugarCost + additionalCreamCost
                binding!!.totalPriceTv.text = "Rs.$finalAdditionalCost"
            }
        }
        binding!!.additionalStickerRB.setOnClickListener {
            selectedAdditions = "sticker"
            if ((isCustomizeAvailable == "true")) {
                if (noSugarChecked || smallSugarChecked || mediumSugarChecked || highSugarChecked) {
                    additionalStickerCost = finalCost * 0.01
                    finalAdditionalCost = finalSugarCost + additionalStickerCost
                    binding!!.totalPriceTv.text = "Rs.$finalAdditionalCost"
                    binding!!.radioGroup4.clearCheck()
                    binding!!.additionalStickerRB.isChecked = true
                } else {
                    Toast.makeText(
                        this@User_Coffee_Details_Activity,
                        "Please Select Sugar",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding!!.additionalStickerRB.isChecked = false
                }
            } else {
                additionalStickerCost = finalCost * 0.01
                finalAdditionalCost = finalSugarCost + additionalStickerCost
                binding!!.totalPriceTv.text = "Rs.$finalAdditionalCost"
            }
        }
        binding!!.purchaseBtn.setOnClickListener {
            if ((address == "" + null) && (mobile == "" + null)) {
                Toast.makeText(
                    this@User_Coffee_Details_Activity,
                    "Please update address & mobile",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val finalQuantity = binding!!.quantityTv.text.toString()
                if ((isCustomizeAvailable == "true")) {
                    if ((smallSizeChecked || mediumSizeChecked || largeSizeChecked) && (noSugarChecked || smallSugarChecked || mediumSugarChecked || highSugarChecked)) {
                        val finalCostOfCoffee =
                            binding!!.totalPriceTv.text.toString().replace("Rs.", "")
                        dialogBox(finalCostOfCoffee, finalQuantity)
                    } else {
                        Toast.makeText(
                            this@User_Coffee_Details_Activity,
                            "Please select at least Size & Sugar",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    val finalCostOfCoffee =
                        binding!!.totalPriceTv.text.toString().replace("Rs.", "")
                    dialogBox(finalCostOfCoffee, finalQuantity)
                }
            }
        }
    }

    private fun dialogBox(finalCostOfCoffee: String, finalQuantity: String) {
        val alertDialog = AlertDialog.Builder(this@User_Coffee_Details_Activity).create()
        alertDialog.setTitle("Confirm Order")
        alertDialog.setMessage("You Need To Pay Rs.$finalCostOfCoffee")
        alertDialog.setButton(
            AlertDialog.BUTTON_POSITIVE,
            "Confirm"
        ) { dialog, which -> AddCoffeeToCart(finalCostOfCoffee, finalQuantity, alertDialog) }
        alertDialog.setButton(
            AlertDialog.BUTTON_NEGATIVE,
            "Cancel"
        ) { dialogInterface, i -> alertDialog.dismiss() }
        alertDialog.show()
    }

    private fun AddCoffeeToCart(
        finalCostOfCoffee: String,
        finalQuantity: String,
        alertDialog: AlertDialog
    ) {
        progressDialog!!.setMessage("Coffee adding to cart")
        progressDialog!!.show()
        val databaseReference = database!!.getReference("Users")
        val timeStamp = System.currentTimeMillis().toString()
        if ((isCustomizeAvailable == "true")) {
            if ((selectedAdditions == "empty")) {
                val hashMap = HashMap<String, Any?>()
                hashMap["coffeeId"] = coffeeId
                hashMap["coffeeName"] = coffeeName
                hashMap["coffeeImage"] = "" + coffeeImage
                hashMap["quantity"] = finalQuantity
                hashMap["selectedSize"] = selectedSize
                hashMap["selectedSugar"] = selectedSugar
                hashMap["selectedAdditions"] = "empty"
                hashMap["timeStamp"] = "" + timeStamp
                hashMap["isCustomizeAvailable"] = "" + isCustomizeAvailable
                hashMap["finalPrice"] = "" + finalCostOfCoffee
                hashMap["uid"] = "" + mAuth!!.uid
                databaseReference.child((mAuth!!.uid)!!).child("CartItems").child(timeStamp)
                    .setValue(hashMap).addOnSuccessListener(
                    OnSuccessListener {
                        alertDialog.dismiss()
                        progressDialog!!.dismiss()
                        Toast.makeText(
                            this@User_Coffee_Details_Activity,
                            "Coffee added to cart..",
                            Toast.LENGTH_SHORT
                        ).show()
                        ClearData()
                    })
            } else {
                val hashMap1 = HashMap<String, Any?>()
                hashMap1["coffeeId"] = coffeeId
                hashMap1["coffeeName"] = coffeeName
                hashMap1["coffeeImage"] = "" + coffeeImage
                hashMap1["quantity"] = finalQuantity
                hashMap1["selectedSize"] = selectedSize
                hashMap1["timeStamp"] = "" + timeStamp
                hashMap1["selectedSugar"] = selectedSugar
                hashMap1["selectedAdditions"] = selectedAdditions
                hashMap1["isCustomizeAvailable"] = "" + isCustomizeAvailable
                hashMap1["finalPrice"] = "" + finalCostOfCoffee
                hashMap1["uid"] = "" + mAuth!!.uid
                databaseReference.child((mAuth!!.uid)!!).child("CartItems").child(timeStamp)
                    .setValue(hashMap1).addOnSuccessListener(object : OnSuccessListener<Void?> {
                    override fun onSuccess(unused: Void?) {
                        alertDialog.dismiss()
                        progressDialog!!.dismiss()
                        Toast.makeText(
                            this@User_Coffee_Details_Activity,
                            "Coffee added to cart..",
                            Toast.LENGTH_SHORT
                        ).show()
                        ClearData()
                    }
                })
                alertDialog.dismiss()
            }
        } else {
            val hashMap2 = HashMap<String, Any?>()
            hashMap2["coffeeId"] = coffeeId
            hashMap2["coffeeName"] = coffeeName
            hashMap2["coffeeImage"] = "" + coffeeImage
            hashMap2["quantity"] = finalQuantity
            hashMap2["isCustomizeAvailable"] = "" + isCustomizeAvailable
            hashMap2["selectedSize"] = "empty"
            hashMap2["timeStamp"] = "" + timeStamp
            hashMap2["selectedSugar"] = "empty"
            hashMap2["selectedAdditions"] = "empty"
            hashMap2["finalPrice"] = "" + finalCostOfCoffee
            hashMap2["uid"] = "" + mAuth!!.uid
            databaseReference.child((mAuth!!.uid)!!).child("CartItems").child(timeStamp)
                .setValue(hashMap2).addOnSuccessListener(object : OnSuccessListener<Void?> {
                override fun onSuccess(unused: Void?) {
                    alertDialog.dismiss()
                    progressDialog!!.dismiss()
                    Toast.makeText(
                        this@User_Coffee_Details_Activity,
                        "Coffee added to cart..",
                        Toast.LENGTH_SHORT
                    ).show()
                    ClearData()
                }
            })
        }
    }

    private fun ClearData() {
        binding!!.radioGroup2.clearCheck()
        binding!!.radioGroup3.clearCheck()
        binding!!.radioGroup4.clearCheck()
        binding!!.quantityTv.text = "1"
        binding!!.totalPriceTv.text = "Rs.$coffeePrice"
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onStart() {
        super.onStart()
        LoadUserData()
    }

    private var address = ""
    private var mobile = ""
    private fun LoadUserData() {
        val databaseReference = database!!.getReference("Users")
        databaseReference.child((mAuth!!.uid)!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    address = "" + snapshot.child("address").value
                    mobile = "" + snapshot.child("mobile").value
                } else {
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}