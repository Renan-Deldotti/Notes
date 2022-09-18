package com.renandeldotti.notes.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.view.inputmethod.InputMethodManager

fun Context.getAppSharedPreferences(): SharedPreferences = getSharedPreferences(Constants.NOTES_APP_SHARED_PREFS_NAME, Context.MODE_PRIVATE)

fun Context.getInputMethodManager(): InputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager