package com.renandeldotti.notes

import android.app.KeyguardManager
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.renandeldotti.notes.databinding.ActivityAuthVerificationBinding
import com.renandeldotti.notes.onboarding.OnboardingActivity
import com.renandeldotti.notes.utils.AppAuthTypes.*
import com.renandeldotti.notes.utils.AppAuthVerifierHelper
import com.renandeldotti.notes.utils.navigationBarSize
import com.renandeldotti.notes.utils.statusBarSize

class AuthVerificationActivity: AppCompatActivity() {

    companion object {
        private const val TAG = "AuthVerificationActivity"

        private const val REQUEST_CODE_ONBOARDING_ACTIVITY = 102000
        private const val REQUEST_CODE_DEVICE_AUTH = 102001
        private const val REQUEST_CODE_IN_APP_PASSWORD = 102002
    }

    private lateinit var binding: ActivityAuthVerificationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        binding = ActivityAuthVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = resources.getColor(android.R.color.transparent, theme)
        val rootParams = binding.root.layoutParams as FrameLayout.LayoutParams
        rootParams.setMargins(0, statusBarSize(), 0, navigationBarSize())

        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        insetsController.hide(WindowInsetsCompat.Type.systemBars())

        val onboardingActivityIntent = Intent(this, OnboardingActivity::class.java)
        startActivityForResult(onboardingActivityIntent, REQUEST_CODE_ONBOARDING_ACTIVITY)
    }

    private fun checkAuthType() {
        val keyguardManager = getSystemService(KeyguardManager::class.java)
        var passwdIntent: Intent? = null
        var reqCode = -1001

        when(AppAuthVerifierHelper.getAppAuthType(this)) {
            PREFER_DEVICE_AUTH -> {
                // Show the default Android security screen
                passwdIntent = keyguardManager.createConfirmDeviceCredentialIntent("Title here", "Description here")
                reqCode = REQUEST_CODE_DEVICE_AUTH
            }
            PREFER_IN_APP_PASSWORD -> {
                // Open in-app password screen
                reqCode = REQUEST_CODE_IN_APP_PASSWORD
            }
            PREFER_NO_PASSWORD -> {
                startApplication()
                return
            }
            UNKNOWN_TYPE -> {
                // Should never reach here, so we show the
                // animation forever until the user leaves the app
                Log.d(TAG, "onCreate: unknown auth!")
            }
        }
        passwdIntent?.let {
            startActivityForResult(it, reqCode)
        }
    }

    private fun startApplication() {
        // TODO: Start the application
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(TAG, "onActivityResult - resCode: OK = $RESULT_OK, CANCELED = $RESULT_CANCELED")
        Log.d(TAG, "onActivityResult - reqCode: $requestCode, resCode: $resultCode, data: ${data?.extras.toString()}")
        when (requestCode) {
            REQUEST_CODE_ONBOARDING_ACTIVITY -> {
                Log.d(TAG, "onActivityResult: REQUEST_CODE_ONBOARDING_ACTIVITY")
            }
            REQUEST_CODE_IN_APP_PASSWORD -> {
                Log.d(TAG, "onActivityResult: REQUEST_CODE_IN_APP_PASSWORD")
            }
            REQUEST_CODE_DEVICE_AUTH -> {
                Log.d(TAG, "onActivityResult: REQUEST_CODE_DEVICE_AUTH")
            }
            else -> {
                // Don't do anything and let the user leave the app
                // TODO: Create a coroutine to leave the app after 5 seconds
                Log.d(TAG, "unknown code $requestCode, $resultCode")
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }
}