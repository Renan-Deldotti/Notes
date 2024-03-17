package com.renandeldotti.notes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.renandeldotti.notes.databinding.ActivityMainBinding
import com.renandeldotti.notes.utils.SharedPrefsMgt
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController = findNavController(R.id.main_navHostFragment)
        setupActionBarWithNavController(navController)

        lifecycleScope.launch {
            val isSet = SharedPrefsMgt.hasUserPassedOnboard(this@MainActivity)
            Log.d(TAG, "onCreate: isSet: $isSet")

            SharedPrefsMgt.setUserPassedOnboard(this@MainActivity)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}