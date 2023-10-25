package com.dreamteam.sharedream

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.dreamteam.sharedream.Util.Constants
import com.dreamteam.sharedream.databinding.ActivityMainBinding
import com.dreamteam.sharedream.home.Edit.EditActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.dreamteam.sharedream.home.HomeAdapter
import com.dreamteam.sharedream.home.Search.SeachFragment
import com.dreamteam.sharedream.viewmodel.PostViewModel
import com.dreamteam.sharedream.viewmodel.PostViewModelProvider
import com.google.firebase.BuildConfig
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var homeAdapter: HomeAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var postViewModel: PostViewModel

    init {
        auth = FirebaseAuth.getInstance()
        Constants.currentUserUid = auth.currentUser!!.uid
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        Log.d("xxxx", "MainACtivityOnCreate: ${Constants.currentUserUid} ")
        Log.isLoggable("Glide", Log.DEBUG)

        homeAdapter = HomeAdapter(this)
        val viewPager: ViewPager2 = binding.viewPager
        val tabLayout: TabLayout = binding.tabLayout



        // shared ViewModel
        val factory = PostViewModelProvider(this)
        postViewModel = ViewModelProvider(this, factory)[PostViewModel::class.java]


        val viewpagerFragmentAdapter = ViewPagerAdapter(this)
        viewPager.adapter = viewpagerFragmentAdapter

        val tabTitles = listOf("교환하기", "내소식")
        binding.button.setOnClickListener {

            auth.signOut()
            Toast.makeText(this, "로그아웃", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LogInActivity::class.java)
            startActivity(intent)
            finish()


        }

        TabLayoutMediator(
            tabLayout,
            viewPager,
            { tab, position -> tab.text = tabTitles[position] }).attach()


        binding.editTextSearchView.setOnClickListener {

            supportFragmentManager.beginTransaction()
                .replace(R.id.frag_edit, SeachFragment())
                .addToBackStack(null)
                .commit()

        }

        binding.btnMypage.setOnClickListener {
            supportFragmentManager.beginTransaction().add(R.id.frag_edit, MyPageFragment())
                .addToBackStack(null).commit()
        }
    }
}