package com.renandeldotti.notes.onboarding

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.renandeldotti.notes.R

class OnboardingActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "OnboardingActivity"
    }

    private lateinit var viewPager: ViewPager2
    private lateinit var onboardingAdapter: OnboardingAdapter
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        viewPager = findViewById(R.id.onboardingViewPager)
        tabLayout = findViewById(R.id.onboardingDotIndicatorTabLayout)

        val fragmentLists = listOf (
            OnboardingWelcomeFragment(),
            OnboardingFeaturesFragment(),
            OnboardingPreferencesFragment()
        )

        onboardingAdapter = OnboardingAdapter(this, fragmentLists)
        viewPager.adapter = onboardingAdapter

        TabLayoutMediator(tabLayout, viewPager) { _, _ ->}.attach()

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position == (fragmentLists.size -1)) {
                    setResult(RESULT_OK)
                    finish()
                } else {
                    super.onPageSelected(position)
                }
            }
        })
    }
}