package com.radu.raduandnunocaffe.Admin.Fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.radu.raduandnunocaffe.Adapters.CoffeeSettingsAdapter
import com.radu.raduandnunocaffe.Model.CoffeeModel
import com.radu.raduandnunocaffe.databinding.FragmentAdminSettingsBinding

class Admin_Settings_Fragment : Fragment() {
    var binding: FragmentAdminSettingsBinding? = null
    var coffeeModel: CoffeeModel? = null
    var searchEtVisible = false
    var coffeeSettingsAdapter: CoffeeSettingsAdapter? = null
    lateinit var coffeeList: ArrayList<CoffeeModel>
    var database: FirebaseDatabase? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAdminSettingsBinding.inflate(inflater, container, false)
        database = FirebaseDatabase.getInstance()
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
                    coffeeSettingsAdapter!!.getFilter().filter(charSequence)
                } catch (e: Exception) {
                    LoadAllCoffee()
                }
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                try {
                    coffeeSettingsAdapter!!.getFilter().filter(editable)
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
                    coffeeSettingsAdapter = CoffeeSettingsAdapter(activity!!, coffeeList)
                    binding!!.recycleView.adapter = coffeeSettingsAdapter
                    coffeeSettingsAdapter!!.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}