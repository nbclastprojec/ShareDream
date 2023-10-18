package com.dreamteam.sharedream

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.viewpager2.widget.ViewPager2
import com.dreamteam.sharedream.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var auth:FirebaseAuth
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



        }
    }

