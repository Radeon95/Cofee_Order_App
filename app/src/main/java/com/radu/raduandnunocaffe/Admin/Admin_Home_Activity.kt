package com.radu.raduandnunocaffe.Admin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.navigation.NavigationBarView
import com.radu.raduandnunocaffe.Admin.Fragments.Admin_Add_Menu_Fragment
import com.radu.raduandnunocaffe.Admin.Fragments.Admin_Home_Fragment
import com.radu.raduandnunocaffe.Admin.Fragments.Admin_Settings_Fragment
import com.radu.raduandnunocaffe.R
import com.radu.raduandnunocaffe.databinding.ActivityAdminHomeBinding

class Admin_Home_Activity : AppCompatActivity() {
    var binding: ActivityAdminHomeBinding? = null
    var admin_home_fragment = Admin_Home_Fragment()
    var admin_add_menu_fragment = Admin_Add_Menu_Fragment()
    var admin_settings_fragment = Admin_Settings_Fragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminHomeBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        supportFragmentManager.beginTransaction().replace(R.id.container, admin_home_fragment)
            .commit()
        binding!!.fab.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, admin_add_menu_fragment).commit()
        }
        binding!!.bottomNavigationView.setOnItemSelectedListener(NavigationBarView.OnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, admin_home_fragment).commit()
                    return@OnItemSelectedListener true
                }

                R.id.settings -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, admin_settings_fragment).commit()
                    return@OnItemSelectedListener true
                }
            }
            false
        })
    }
}