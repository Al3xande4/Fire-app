package com.example.fireapplicatioin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.fireapplicatioin.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        val navController = this.findNavController(R.id.myNavHostFragment)


        val sharedPreferences = getSharedPreferences(getPrefsName(), 0)
        val hasLoggedIn = sharedPreferences.getBoolean("hasLoggedIn", false)
        if (!hasLoggedIn) {
            val intent = Intent(this, EnterActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.bottomNavigation.setupWithNavController(navController)


        binding.bottomNavigation2.setItemSelected(R.id.navigation_fire, true)
        binding.bottomNavigation2.setOnItemSelectedListener {
            when (it) {
                R.id.navigation_fire -> setCurrentFragment(FireFragment())
                R.id.navigation_news -> setCurrentFragment(NewsFragment())
                R.id.navigation_account -> setCurrentFragment(AccountFragment())
            }
        }


        binding.bottomNavigation3.setNavigationChangeListener { _: View, position: Int ->
            when (position) {
                1 -> setCurrentFragment(FireFragment())
                2 -> setCurrentFragment(AccountFragment())
                0 -> setCurrentFragment(NewsFragment())
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