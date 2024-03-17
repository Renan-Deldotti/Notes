package com.renandeldotti.notes.onboarding

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class OnboardingAdapter(fa: FragmentActivity, private val fl: List<Fragment>) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int = fl.size

    override fun createFragment(position: Int): Fragment = fl[position]
}