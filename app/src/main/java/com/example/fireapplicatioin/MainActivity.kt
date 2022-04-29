package com.example.fireapplicatioin

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.fireapplicatioin.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        val navController = this.findNavController(R.id.myNavHostFragment)

        drawerLayout = binding.drawerLayout
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
        NavigationUI.setupWithNavController(binding.navView, navController)

        val sharedPreferences = getSharedPreferences(getPrefsName(), 0)
        val hasLoggedIn = sharedPreferences.getBoolean("hasLoggedIn", false)

        if (!hasLoggedIn) {
            val intent = Intent(this, EnterActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.bottomNavigation.selectedItemId = R.id.nav_fire
        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_fire -> setCurrentFragment(MapFragment())
                R.id.nav_account -> setCurrentFragment(AcountFragment())
                R.id.nav_news -> setCurrentFragment(NewsFragment())
            }
            return@setOnItemSelectedListener true
        }

        binding.bottomNavigation2.setItemSelected(R.id.nav_fire, true)
        binding.bottomNavigation2.setOnItemSelectedListener {
            when (it) {
                R.id.nav_fire -> setCurrentFragment(MapFragment())
                R.id.nav_account -> setCurrentFragment(AcountFragment())
                R.id.nav_news -> setCurrentFragment(NewsFragment())
            }
        }

        binding.bottomNavigation3.setNavigationChangeListener { view: View, position: Int ->
            when (view.id) {
                R.id.c_item_fire -> setCurrentFragment(MapFragment())
                R.id.c_item_account -> setCurrentFragment(AcountFragment())
                R.id.c_item_news -> setCurrentFragment(NewsFragment())
            }
        }

    }

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.body_container, fragment)
            commit()
        }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.myNavHostFragment)
        return NavigationUI.navigateUp(navController, drawerLayout)
    }
}