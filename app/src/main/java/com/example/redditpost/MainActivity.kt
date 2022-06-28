package com.example.redditpost

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       (application as MyApplication).provideAppComponent().inject(this)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        //handle bottom navigation
        val navigationBottom by lazy {
            findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
        }
        val navController = findNavController(R.id.myNaveHostFragment)
        navigationBottom.setupWithNavController(navController)
    }
}