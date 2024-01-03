package com.radu.raduandnunocaffe.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.radu.raduandnunocaffe.Adapters.UserAdapters.ShowOrdersAdapter
import com.radu.raduandnunocaffe.Model.OrdersModel
import com.radu.raduandnunocaffe.databinding.FragmentOrdersBinding

class OrdersFragment : Fragment() {
    var binding: FragmentOrdersBinding? = null
    var mAuth: FirebaseAuth? = null
    var database: FirebaseDatabase? = null
    var showOrdersAdapter: ShowOrdersAdapter? = null
    lateinit var ordersList: ArrayList<OrdersModel>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrdersBinding.inflate(inflater, container, false)
        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        return binding!!.root
    }

    override fun onStart() {
        super.onStart()
        LoadOrders()
    }

    private fun LoadOrders() {
        ordersList = ArrayList()
        val databaseReference = database!!.getReference("Users")
        databaseReference.child(mAuth!!.uid!!).child("Orders")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    ordersList!!.clear()
                    if (snapshot.exists()) {
                        binding!!.lottie.visibility = View.GONE
                        binding!!.recycleview.visibility = View.VISIBLE
                        for (ds in snapshot.children) {
                            val ordersModel = ds.getValue(OrdersModel::class.java)
                            ordersModel?.let { ordersList!!.add(it) }
                        }
                    } else {
                        binding!!.lottie.visibility = View.VISIBLE
                        binding!!.recycleview.visibility = View.VISIBLE
                    }
                    showOrdersAdapter = ShowOrdersAdapter(activity!!, ordersList)
                    binding!!.recycleview.adapter = showOrdersAdapter
                    showOrdersAdapter!!.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }
}