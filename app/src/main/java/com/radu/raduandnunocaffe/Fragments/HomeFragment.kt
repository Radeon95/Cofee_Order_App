package com.radu.raduandnunocaffe.Fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.radu.raduandnunocaffe.Adapters.UserAdapters.UserCoffeeAdapter
import com.radu.raduandnunocaffe.Model.CoffeeModel
import com.radu.raduandnunocaffe.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    var binding: FragmentHomeBinding? = null
    var database: FirebaseDatabase? = null
    var userCoffeeAdapter: UserCoffeeAdapter? = null
   lateinit var coffeeList: ArrayList<CoffeeModel>
    var coffeeModel: CoffeeModel? = null
    var searchEtVisible = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        database = FirebaseDatabase.getInstance()
        binding!!.searchBtn.setOnClickListener {
            if (searchEtVisible) {
                binding!!.visibleLayout.visibility = View.GONE
                searchEtVisible = false
            } else {
                binding!!.visibleLayout.visibility = View.VISIBLE
                searchEtVisible = true
            }
        }
        binding!!.searchET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                try {
                    userCoffeeAdapter!!.getFilter().filter(charSequence)
                } catch (e: Exception) {
                    LoadAllCoffee()
                }
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                try {
                    userCoffeeAdapter!!.getFilter().filter(editable)
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
                    userCoffeeAdapter = UserCoffeeAdapter(activity!!, coffeeList)
                    val staggeredGridLayoutManager =
                        StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                    binding!!.recycleView.layoutManager = staggeredGridLayoutManager
                    binding!!.recycleView.adapter = userCoffeeAdapter
                    userCoffeeAdapter!!.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}