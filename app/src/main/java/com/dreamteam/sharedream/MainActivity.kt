package com.dreamteam.sharedream

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.viewpager2.widget.ViewPager2
import com.dreamteam.sharedream.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.dreamteam.sharedream.home.Edit.EditActivity
import com.dreamteam.sharedream.home.HomeAdapter

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var homeAdapter: HomeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        homeAdapter = HomeAdapter(this)
        val viewPager: ViewPager2 = binding.viewPager
        val tabLayout: TabLayout = binding.tabLayout


        val viewpagerFragmentAdapter = ViewPagerAdapter(this)
        viewPager.adapter = viewpagerFragmentAdapter

        val tabTitles = listOf("교환하기", "내소식")

        TabLayoutMediator(
            tabLayout,
            viewPager,
            { tab, position -> tab.text = tabTitles[position] }).attach()

        binding.floatingActionButton.setOnClickListener {
            Log.d("MainActivity","nyh floatingbtn clicked")
            val intent = Intent(this, EditActivity::class.java)
            startActivity(intent)
        }
    }
}