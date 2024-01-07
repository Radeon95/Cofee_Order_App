package com.radu.raduandnunocaffe

import android.os.Bundle
import android.text.format.DateFormat
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.radu.raduandnunocaffe.Adapters.UserAdapters.ShowOrderDetailsAdapter
import com.radu.raduandnunocaffe.Model.CartItemsModel
import com.radu.raduandnunocaffe.databinding.ActivityShowOrderDetailsBinding
import java.util.Calendar

class Show_Order_Details_Activity : AppCompatActivity() {
    var binding: ActivityShowOrderDetailsBinding? = null
    lateinit var cartItemsModelList: ArrayList<CartItemsModel>
    var showOrderDetailsAdapter: ShowOrderDetailsAdapter? = null
    var database: FirebaseDatabase? = null
    var mAuth: FirebaseAuth? = null
    private var username: String? = null
    private var email: String? = null
    private var orderDate: String? = null
    private var amount: String? = null
    private var mobile: String? = null
    private var address: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowOrderDetailsBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        setSupportActionBar(binding!!.toolbar)
        val intent = intent
        username = intent.getStringExtra("address")
        email = intent.getStringExtra("email")
        orderDate = intent.getStringExtra("orderDate")
        amount = intent.getStringExtra("amount")
        mobile = intent.getStringExtra("mobile")
        address = intent.getStringExtra("address")
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = orderDate!!.toLong()
        val formatDate = DateFormat.format("dd/MM/yyyy hh:mm a", calendar).toString()
        binding!!.usernameTv.text = username
        binding!!.emailTv.text = email
        binding!!.orderDateTv.text = formatDate
        binding!!.mobileTv.text = mobile
        binding!!.amountTv.text = "£ .$amount"
        binding!!.addressTv.text = address
    }

    override fun onStart() {
        super.onStart()
        LoadOrderDetails()
    }

    private fun LoadOrderDetails() {
        cartItemsModelList = ArrayList()
        val databaseReference = database!!.getReference("Users")
        databaseReference.child(mAuth!!.uid!!).child("Orders").child(orderDate!!).child("Items")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    cartItemsModelList!!.clear()
                    for (ds in snapshot.children) {
                        val cartItemsModel = ds.getValue(
                            CartItemsModel::class.java
                        )
                        if (cartItemsModel != null) {
                            cartItemsModelList!!.add(cartItemsModel)
                        }
                    }
                    showOrderDetailsAdapter = ShowOrderDetailsAdapter(
                        this@Show_Order_Details_Activity,
                        cartItemsModelList
                    )
                    binding!!.nestedRecycleview.adapter = showOrderDetailsAdapter
                    showOrderDetailsAdapter!!.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}