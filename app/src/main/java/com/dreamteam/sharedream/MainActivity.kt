package com.dreamteam.sharedream

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.dreamteam.sharedream.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

    class MainActivity : AppCompatActivity() {
        private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(binding.root)

            val viewPager: ViewPager2 = binding.viewPager
            val tabLayout: TabLayout = binding.tabLayout


            val viewpagerFragmentAdapter = ViewPagerAdapter(this)
            viewPager.adapter = viewpagerFragmentAdapter

            val tabTitles = listOf("교환하기", "내소식")


            TabLayoutMediator(
                tabLayout,
                viewPager,
                { tab, position -> tab.text = tabTitles[position] }).attach()

            binding.btnHome.setOnClickListener {

            }
        }
    }