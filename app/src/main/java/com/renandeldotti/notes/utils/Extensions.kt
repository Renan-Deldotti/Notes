package com.renandeldotti.notes.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.view.inputmethod.InputMethodManager
import com.renandeldotti.notes.R

fun Context.getAppSharedPreferences(): SharedPreferences = getSharedPreferences(Constants.NOTES_APP_SHARED_PREFS_NAME, Context.MODE_PRIVATE)

fun Context.getInputMethodManager(): InputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager

fun Context.navigationBarSize(): Int {
    val resId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
    return if (resId > 0) {
        resources.getDimensionPixelSize(resId)
    } else {
        resources.getDimensionPixelSize(R.dimen.navigation_bar_height_fallback)
    }
}

fun Context.statusBarSize(): Int {
    val resId = resources.getIdentifier("status_bar_height", "dimen", "android")
    return if (resId > 0) {
        resources.getDimensionPixelSize(resId)
    } else {
        resources.getDimensionPixelSize(R.dimen.status_bar_height_fallback)
    }
}