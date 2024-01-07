package com.radu.raduandnunocaffe.Fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.radu.raduandnunocaffe.Adapters.UserAdapters.CartItemsAdapter
import com.radu.raduandnunocaffe.Model.CartItemsModel
import com.radu.raduandnunocaffe.databinding.FragmentCartBinding

class CartFragment() : Fragment() {
    var binding: FragmentCartBinding? = null
    lateinit var cartItemsList: ArrayList<CartItemsModel>
    var cartItemsAdapter: CartItemsAdapter? = null
    var cartItemsModel: CartItemsModel? = null
    var database: FirebaseDatabase? = null
    var mAuth: FirebaseAuth? = null
    var databaseReference: DatabaseReference? = null
    var progressDialog: ProgressDialog? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(inflater, container, false)
        database = FirebaseDatabase.getInstance()
        mAuth = FirebaseAuth.getInstance()
        databaseReference = database!!.getReference("Users")
        progressDialog = ProgressDialog(activity)
        progressDialog!!.setTitle("Please wait...")
        progressDialog!!.setCanceledOnTouchOutside(false)
        binding!!.checkOutBtn.setOnClickListener {
            val finalPrice = binding!!.totalPriceCount.text.toString()
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Confirm")
            builder.setMessage("You need to pay $finalPrice")
            builder.setPositiveButton("Confirm") { dialogInterface, i -> SaveOrder() }
                .setNegativeButton("Cancel") { dialogInterface, i -> dialogInterface.dismiss() }
            builder.show()
        }
        return binding!!.root
    }

    private fun LoadCartData() {
        cartItemsList = ArrayList()
        val databaseReference = database!!.getReference("Users")
        databaseReference.child((mAuth!!.uid)!!).child("CartItems")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    cartItemsList!!.clear()
                    if (snapshot.exists()) {
                        binding!!.lottie.visibility = View.GONE
                        binding!!.recycleview.visibility = View.VISIBLE
                        for (ds: DataSnapshot in snapshot.children) {
                            cartItemsModel = ds.getValue(CartItemsModel::class.java)
                            cartItemsModel?.let { cartItemsList!!.add(it) }
                        }
                    } else {
                        binding!!.lottie.visibility = View.VISIBLE
                        binding!!.recycleview.visibility = View.GONE
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun SaveOrder() {
        progressDialog!!.setMessage("Saving order...")
        progressDialog!!.show()
        val timestamp = "" + System.currentTimeMillis()
        val TotalCost = binding!!.totalPriceCount.text.toString().replace("£.", "")
        val userdata = HashMap<String, Any?>()
        userdata["username"] = username
        userdata["email"] = email
        userdata["mobile"] = mobile
        userdata["address"] = address
        userdata["finalPrice"] = TotalCost
        userdata["orderDate"] = timestamp
        progressDialog!!.setMessage("Saving items")
        databaseReference!!.child((mAuth!!.uid)!!).child("Orders").child(timestamp)
            .setValue(userdata).addOnSuccessListener {
            for (i in cartItemsList!!.indices) {
                val hashMap = HashMap<String, Any>()
                hashMap["coffeeId"] = cartItemsList!!.get(i)!!.coffeeId.toString()
                hashMap["coffeeImage"] = cartItemsList!!.get(i)!!.coffeeImage.toString()
                hashMap["coffeeName"] = cartItemsList!!.get(i)!!.coffeeName.toString()
                hashMap["finalPrice"] = cartItemsList!!.get(i)!!.finalPrice.toString()
                hashMap["isCustomizeAvailable"] = cartItemsList!!.get(i)!!.isCustomizeAvailable.toString()
                hashMap["quantity"] = cartItemsList!!.get(i)!!.quantity.toString()
                hashMap["selectedAdditions"] = cartItemsList!!.get(i)!!.selectedAdditions.toString()
                hashMap["selectedSize"] = cartItemsList!!.get(i)!!.selectedSize.toString()
                hashMap["selectedSugar"] = cartItemsList!!.get(i)!!.selectedSugar.toString()
                hashMap["timeStamp"] = cartItemsList!!.get(i)!!.timeStamp.toString()
                hashMap["uid"] = cartItemsList!!.get(i)!!.uid.toString()
                cartItemsList!![i]!!.timeStamp?.let { it1 ->
                    databaseReference!!.child((mAuth!!.uid)!!).child("Orders").child(timestamp)
                        .child("Items").child(
                            it1
                        ).setValue(hashMap).addOnSuccessListener { removeCartData() }
                }
            }
        }
    }

    private fun removeCartData() {
        progressDialog!!.setMessage("Removing items")
        val databaseReference = database!!.getReference("Users")
        databaseReference.child((mAuth!!.uid)!!).child("CartItems").removeValue()
            .addOnSuccessListener(
                OnSuccessListener {
                    progressDialog!!.dismiss()
                    cartItemsAdapter!!.notifyDataSetChanged()
                    Toast.makeText(context, "Order Successfully", Toast.LENGTH_SHORT).show()
                })
    }

    override fun onStart() {
        super.onStart()
        LoadUserData()
        LoadCartItems()
        LoadTotalPrice()
        LoadCartData()
    }

    private var username: String? = null
    private var email: String? = null
    private var mobile: String? = null
    private var address: String? = null
    private fun LoadUserData() {
        val databaseReference = database!!.getReference("Users")
        databaseReference.child((mAuth!!.uid)!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                username = "" + snapshot.child("username").value
                email = "" + snapshot.child("email").value
                mobile = "" + snapshot.child("mobile").value
                address = "" + snapshot.child("address").value
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun LoadTotalPrice() {
        val databaseReference = database!!.getReference("Users")
        databaseReference.child((mAuth!!.uid)!!).child("CartItems")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var TotalPrice = 0.0
                    if (snapshot.exists()) {
                        binding!!.totalPriceCount.visibility = View.VISIBLE
                        binding!!.checkOutBtn.visibility = View.VISIBLE
                        for (ds: DataSnapshot in snapshot.children) {
                            val finalPrice = "" + ds.child("finalPrice").value
                            val value = java.lang.Double.valueOf(finalPrice)
                            TotalPrice += value
                            binding!!.totalPriceCount.text = ("£. $TotalPrice").toString()
                        }
                    } else {
                        binding!!.totalPriceCount.visibility = View.GONE
                        binding!!.checkOutBtn.visibility = View.GONE
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun LoadCartItems() {
        cartItemsList = ArrayList()
        val databaseReference = database!!.getReference("Users")
        databaseReference.child((mAuth!!.uid)!!).child("CartItems")
            .addValueEventListener(object : ValueEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    cartItemsList!!.clear()
                    if (snapshot.exists()) {
                        for (ds: DataSnapshot in snapshot.children) {
                            cartItemsModel = ds.getValue(CartItemsModel::class.java)
                            cartItemsModel?.let { cartItemsList!!.add(it) }
                        }
                        cartItemsAdapter = CartItemsAdapter((activity)!!, cartItemsList)
                        binding!!.recycleview.adapter = cartItemsAdapter
                        cartItemsAdapter!!.notifyDataSetChanged()
                    } else {
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }
}