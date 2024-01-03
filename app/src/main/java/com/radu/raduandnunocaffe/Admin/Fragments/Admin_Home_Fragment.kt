package com.radu.raduandnunocaffe.Admin.Fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.radu.raduandnunocaffe.Adapters.CoffeeAdapter
import com.radu.raduandnunocaffe.Model.CoffeeModel
import com.radu.raduandnunocaffe.databinding.FragmentAdminHomeBinding

class Admin_Home_Fragment : Fragment() {
    var binding: FragmentAdminHomeBinding? = null
    var searchEtVisible = false
    var database: FirebaseDatabase? = null
    var coffeeAdapter: CoffeeAdapter? = null
    lateinit var coffeeList: ArrayList<CoffeeModel>
    var coffeeModel: CoffeeModel? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAdminHomeBinding.inflate(inflater, container, false)
        database = FirebaseDatabase.getInstance()
        (activity as AppCompatActivity?)!!.setSupportActionBar(binding!!.toolbar)
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayShowTitleEnabled(false)
        binding!!.toolbar.title = ""
        binding!!.toolbar.subtitle = ""
        binding!!.searchBtn.setOnClickListener {
            if (searchEtVisible) {
                binding!!.visibleLayout.visibility = View.GONE
                searchEtVisible = false
                LoadAllCoffee()
            } else {
                binding!!.visibleLayout.visibility = View.VISIBLE
                searchEtVisible = true
            }
        }
        binding!!.searchET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                try {
                    coffeeAdapter!!.getFilter().filter(charSequence)
                } catch (e: Exception) {
                    LoadAllCoffee()
                }
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                try {
                    coffeeAdapter!!.getFilter().filter(editable)
                } catch (e: Exception) {
                    LoadAllCoffee()
                }
            }
        })
        return binding!!.root
    }

    override fun onStart() {
        super.onStart()
        LoadAllCoffee()
    }

    private fun LoadAllCoffee() {
        coffeeList = ArrayList()
        val databaseReference = database!!.getReference("CoffeeMenu")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                coffeeList!!.clear()
                if (snapshot.exists()) {
                    for (ds in snapshot.children) {
                        coffeeModel = ds.getValue(CoffeeModel::class.java)
                        coffeeModel?.let { coffeeList!!.add(it) }
                    }
                    coffeeAdapter = CoffeeAdapter(activity!!, coffeeList)
                    binding!!.recycleView.adapter = coffeeAdapter
                    coffeeAdapter!!.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}