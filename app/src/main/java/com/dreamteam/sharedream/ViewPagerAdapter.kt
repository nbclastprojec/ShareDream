package com.dreamteam.sharedream

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dreamteam.sharedream.home.HomeFragment
import com.dreamteam.sharedream.home.alarm.AlarmFragment

class ViewPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    val fragments = listOf<Fragment>(HomeFragment(), AlarmFragment())

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        val fragment = fragments[position]
        Log.d("nyh ViewPagerAdapter", "createFragment called for position: $position, fragment: $fragment")
        return fragment
    }
}