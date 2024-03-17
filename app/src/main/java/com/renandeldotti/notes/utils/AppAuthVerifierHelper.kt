package com.renandeldotti.notes.utils

import android.app.KeyguardManager
import android.content.Context
import android.util.Log

object AppAuthVerifierHelper {
    private fun isDeviceRooted(context: Context): Boolean = true

    private fun userPreferNoPassword(context: Context): Boolean = false

    private fun userHasDeviceAuthMethod(keyguardManager: KeyguardManager) = keyguardManager.isKeyguardSecure

    private fun userPreferDeviceAuth(context: Context) : Boolean = true


    /*
     * In the first "login" phase we will offer two options
     * Secure:
     *      Must set an in-app password
     *      Can prefer to open with device auth
     * Non-Secure:
     *      No password, app data will be always open
     */
    fun getAppAuthType(context: Context) : AppAuthTypes {
        Log.d("N-AAVH", "enter")
        if (userPreferNoPassword(context)) {
            return AppAuthTypes.PREFER_NO_PASSWORD
        }

        val isRooted = isDeviceRooted(context)
        Log.d("N-AAVH", "r: $isRooted")

        val keyguardManager = context.getSystemService(KeyguardManager::class.java)
        if (userPreferDeviceAuth(context) && userHasDeviceAuthMethod(keyguardManager)) {
            return AppAuthTypes.PREFER_DEVICE_AUTH
        }

        return AppAuthTypes.PREFER_IN_APP_PASSWORD
    }
}

enum class AppAuthTypes {
    PREFER_NO_PASSWORD,
    PREFER_IN_APP_PASSWORD,
    PREFER_DEVICE_AUTH,
    UNKNOWN_TYPE
}