package com.radu.raduandnunocaffe

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.radu.raduandnunocaffe.Fragments.CartFragment
import com.radu.raduandnunocaffe.Fragments.HomeFragment
import com.radu.raduandnunocaffe.Fragments.OrdersFragment
import com.radu.raduandnunocaffe.Fragments.ProfileFragment
import com.radu.raduandnunocaffe.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    var binding: ActivityMainBinding? = null
    var homeFragment = HomeFragment()
    var cartFragment = CartFragment()
    var profileFragment = ProfileFragment()
    var ordersFragment = OrdersFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        supportFragmentManager.beginTransaction().replace(R.id.container, homeFragment).commit()
        binding!!.bottomNavigation.setOnItemSelectedListener(NavigationBarView.OnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    supportFragmentManager.beginTransaction().replace(R.id.container, homeFragment)
                        .commit()
                    return@OnItemSelectedListener true
                }

                R.id.cart -> {
                    supportFragmentManager.beginTransaction().replace(R.id.container, cartFragment)
                        .commit()
                    return@OnItemSelectedListener true
                }

                R.id.profile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, profileFragment).commit()
                    return@OnItemSelectedListener true
                }

                R.id.orders -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, ordersFragment).commit()
                    return@OnItemSelectedListener true
                }
            }
            false
        })
    }

    override fun onStart() {
        super.onStart()
        val badgeDrawable = binding!!.bottomNavigation.getOrCreateBadge(R.id.cart)
        val badgeDrawable1 = binding!!.bottomNavigation.getOrCreateBadge(R.id.orders)
        LoadCartCount(badgeDrawable)
        LoadOrdersCount(badgeDrawable1)
    }

    private fun LoadOrdersCount(badgeDrawable1: BadgeDrawable) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        databaseReference.child(FirebaseAuth.getInstance().uid!!).child("Orders")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val count = snapshot.childrenCount
                    badgeDrawable1.number = count.toInt()
                    if (count == 0L) {
                        badgeDrawable1.isVisible = false
                    } else {
                        badgeDrawable1.isVisible = true
                        badgeDrawable1.backgroundColor = resources.getColor(R.color.brown)
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun LoadCartCount(badgeDrawable: BadgeDrawable) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        databaseReference.child(FirebaseAuth.getInstance().uid!!).child("CartItems")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val count = snapshot.childrenCount
                    badgeDrawable.number = count.toInt()
                    if (count == 0L) {
                        badgeDrawable.isVisible = false
                    } else {
                        badgeDrawable.isVisible = true
                        badgeDrawable.backgroundColor = resources.getColor(R.color.brown)
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }
}