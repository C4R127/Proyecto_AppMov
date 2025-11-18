package com.example.primeravance

import android.os.Bundle
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.primeravance.databinding.ActivityMainBinding

class   MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navBar

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        
        // Hide/show bottom navigation based on current destination
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_welcome,
                R.id.loginFragment,
                R.id.registerFragment,
                R.id.forgotPasswordFragment,
                R.id.splashFragment -> {
                    navView.visibility = View.GONE
                }
                else -> {
                    navView.visibility = View.VISIBLE
                }
            }
        }
        
        // Setup bottom navigation
        navView.setupWithNavController(navController)
    }
}
