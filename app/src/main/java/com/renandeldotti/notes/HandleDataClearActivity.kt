package com.renandeldotti.notes

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.renandeldotti.notes.databinding.ActivityHandleDataClearBinding

class HandleDataClearActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "HandleDataClearActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")

        val binding = ActivityHandleDataClearBinding.inflate(layoutInflater)

        setContentView(binding.root)
    }
}