package com.radu.raduandnunocaffe

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.radu.raduandnunocaffe.databinding.ActivityCoffeeDetailsBinding
import com.squareup.picasso.Picasso

class Coffee_Details_Activity : AppCompatActivity() {
    var binding: ActivityCoffeeDetailsBinding? = null
    private var coffeeName: String? = null
    private var coffeePrice: String? = null
    private var coffeeImage: String? = null
    private var isCustomizeAvailable: String? = null
    private var Quantity = 1
    private var finalCost = 0
    private var cost = 0
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
    var smallSizeChecked = false
    var mediumSizeChecked = false
    var largeSizeChecked = false
    var noSugarChecked = false
    var smallSugarChecked = false
    var mediumSugarChecked = false
    var highSugarChecked = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoffeeDetailsBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        setSupportActionBar(binding!!.toolbar)
        coffeeName = intent.getStringExtra("coffeeName")
        coffeePrice = intent.getStringExtra("coffeePrice")
        coffeeImage = intent.getStringExtra("coffeeImage")
        isCustomizeAvailable = intent.getStringExtra("isCustomizeAvailable")
        try {
            Picasso.get().load(coffeeImage).placeholder(R.drawable.spinner)
                .into(binding!!.CoffeeImage)
        } catch (e: Exception) {
            binding!!.CoffeeImage.setImageResource(R.drawable.spinner)
        }
        binding!!.coffeeName.text = coffeeName
        binding!!.coffeeNameTwo.text = coffeeName
        binding!!.priceTv.text = "£ $coffeePrice"
        binding!!.totalPriceTv.text = "£ $coffeePrice"
        if (isCustomizeAvailable == "true") {
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
            binding!!.totalPriceTv.text = "£ $finalCost"
            binding!!.quantityTv.text = "" + Quantity
        }
        binding!!.btnDecrement.setOnClickListener {
            binding!!.radioGroup2.clearCheck()
            binding!!.radioGroup3.clearCheck()
            binding!!.radioGroup4.clearCheck()
            if (Quantity > 1) {
                finalCost = finalCost - cost
                Quantity--
                binding!!.totalPriceTv.text = "£ $finalCost"
                binding!!.quantityTv.text = Quantity.toString()
            }
        }
        binding!!.smallSizeRB.setOnClickListener {
            binding!!.radioGroup3.clearCheck()
            binding!!.radioGroup4.clearCheck()
            smallSizeChecked = true
            mediumSizeChecked = false
            largeSizeChecked = false
            smallSizeCost = finalCost * 0.1
            finalSizeCost = finalCost + smallSizeCost
            binding!!.totalPriceTv.text = "£ $finalSizeCost"
        }
        binding!!.mediumSizeRB.setOnClickListener {
            smallSizeChecked = false
            mediumSizeChecked = true
            largeSizeChecked = false
            binding!!.radioGroup3.clearCheck()
            binding!!.radioGroup4.clearCheck()
            mediumSizeCost = finalCost * 0.2
            finalSizeCost = finalCost + mediumSizeCost
            binding!!.totalPriceTv.text = "£ $finalSizeCost"
        }
        binding!!.largeSizeRB.setOnClickListener {
            smallSizeChecked = false
            mediumSizeChecked = false
            largeSizeChecked = true
            binding!!.radioGroup3.clearCheck()
            binding!!.radioGroup4.clearCheck()
            largeSizeCost = finalCost * 0.3
            finalSizeCost = finalCost + largeSizeCost
            binding!!.totalPriceTv.text = "£ $finalSizeCost"
        }
        binding!!.noSugarRB.setOnClickListener {
            highSugarChecked = false
            noSugarChecked = true
            smallSugarChecked = false
            mediumSugarChecked = false
            if (isCustomizeAvailable == "true") {
                if (smallSizeChecked || mediumSizeChecked || largeSizeChecked) {
                    noSugarCost = finalCost * -0.1 //-10%
                    finalSugarCost = finalSizeCost + noSugarCost
                    binding!!.totalPriceTv.text = "£ $finalSugarCost"
                    binding!!.radioGroup3.clearCheck()
                    binding!!.radioGroup4.clearCheck()
                    binding!!.noSugarRB.isChecked = true
                } else {
                    Toast.makeText(
                        this@Coffee_Details_Activity,
                        "Please Select Size",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding!!.noSugarRB.isChecked = false
                }
            } else {
                noSugarCost = finalCost * -0.1 //-10%
                finalSugarCost = finalCost + noSugarCost
                binding!!.totalPriceTv.text = "£ $finalSugarCost"
            }
        }
        binding!!.smallSugarRB.setOnClickListener {
            highSugarChecked = false
            noSugarChecked = false
            smallSugarChecked = true
            mediumSugarChecked = false
            if (isCustomizeAvailable == "true") {
                if (smallSizeChecked || mediumSizeChecked || largeSizeChecked) {
                    smallSugarCost = finalCost * 0.01 //-10%
                    finalSugarCost = finalSizeCost + smallSugarCost
                    binding!!.totalPriceTv.text = "£ $finalSugarCost"
                    binding!!.radioGroup3.clearCheck()
                    binding!!.radioGroup4.clearCheck()
                    binding!!.smallSugarRB.isChecked = true
                } else {
                    Toast.makeText(
                        this@Coffee_Details_Activity,
                        "Please Select Size",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding!!.smallSugarRB.isChecked = false
                }
            } else {
                smallSugarCost = finalCost * 0.01 //-10%
                finalSugarCost = finalCost + smallSugarCost
                binding!!.totalPriceTv.text = "£ $finalSugarCost"
            }
        }
        binding!!.mediumSugarRB.setOnClickListener {
            highSugarChecked = false
            noSugarChecked = false
            smallSugarChecked = false
            mediumSugarChecked = true
            if (isCustomizeAvailable == "true") {
                if (smallSizeChecked || mediumSizeChecked || largeSizeChecked) {
                    mediumSugarCost = finalCost * 0.02 //-10%
                    finalSugarCost = finalSizeCost + mediumSugarCost
                    binding!!.totalPriceTv.text = "£ $finalSugarCost"
                    binding!!.radioGroup3.clearCheck()
                    binding!!.radioGroup4.clearCheck()
                    binding!!.mediumSugarRB.isChecked = true
                } else {
                    Toast.makeText(
                        this@Coffee_Details_Activity,
                        "Please Select Size",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding!!.mediumSugarRB.isChecked = false
                }
            } else {
                mediumSugarCost = finalCost * 0.02 //-10%
                finalSugarCost = finalCost + mediumSugarCost
                binding!!.totalPriceTv.text = "£ $finalSugarCost"
            }
        }
        binding!!.highSugarRB.setOnClickListener {
            highSugarChecked = true
            noSugarChecked = false
            smallSugarChecked = false
            mediumSugarChecked = false
            if (isCustomizeAvailable == "true") {
                if (smallSizeChecked || mediumSizeChecked || largeSizeChecked) {
                    highSugarCost = finalCost * 0.03 //-10%
                    finalSugarCost = finalSizeCost + highSugarCost
                    binding!!.totalPriceTv.text = "£ $finalSugarCost"
                    binding!!.radioGroup3.clearCheck()
                    binding!!.radioGroup4.clearCheck()
                    binding!!.highSugarRB.isChecked = true
                } else {
                    Toast.makeText(
                        this@Coffee_Details_Activity,
                        "Please Select Size",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding!!.highSugarRB.isChecked = false
                }
            } else {
                highSugarCost = finalCost * 0.03 //-10%
                finalSugarCost = finalCost + highSugarCost
                binding!!.totalPriceTv.text = "£ $finalSugarCost"
            }
        }
        binding!!.additionCreamRB.setOnClickListener {
            if (isCustomizeAvailable == "true") {
                if (noSugarChecked || smallSugarChecked || mediumSugarChecked || highSugarChecked) {
                    additionalCreamCost = finalCost * 0.05
                    finalAdditionalCost = finalSugarCost + additionalCreamCost
                    binding!!.totalPriceTv.text = "£ $finalAdditionalCost"
                    binding!!.radioGroup4.clearCheck()
                    binding!!.additionCreamRB.isChecked = true
                } else {
                    Toast.makeText(
                        this@Coffee_Details_Activity,
                        "Please Select Sugar",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding!!.additionCreamRB.isChecked = false
                }
            } else {
                additionalCreamCost = finalCost * 0.05 //-10%
                finalAdditionalCost = finalSugarCost + additionalCreamCost
                binding!!.totalPriceTv.text = "£ $finalAdditionalCost"
            }
        }
        binding!!.additionalStickerRB.setOnClickListener {
            if (isCustomizeAvailable == "true") {
                if (noSugarChecked || smallSugarChecked || mediumSugarChecked || highSugarChecked) {
                    additionalStickerCost = finalCost * 0.01
                    finalAdditionalCost = finalSugarCost + additionalStickerCost
                    binding!!.totalPriceTv.text = "£ $finalAdditionalCost"
                    binding!!.radioGroup4.clearCheck()
                    binding!!.additionalStickerRB.isChecked = true
                } else {
                    Toast.makeText(
                        this@Coffee_Details_Activity,
                        "Please Select Sugar",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding!!.additionalStickerRB.isChecked = false
                }
            } else {
                additionalStickerCost = finalCost * 0.01
                finalAdditionalCost = finalSugarCost + additionalStickerCost
                binding!!.totalPriceTv.text = "£ $finalAdditionalCost"
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}