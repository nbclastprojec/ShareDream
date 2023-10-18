package com.dreamteam.sharedream

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.viewpager2.widget.ViewPager2
import com.dreamteam.sharedream.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
        private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(binding.root)

            checkInviteLink()

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
                Log.d("xxxx", " 메인 액티비티 ${FirebaseAuth.getInstance().currentUser?.uid}")
            }
        }

    private fun checkInviteLink(){
        Firebase.dynamicLinks
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                var deeplink: Uri? = null
                if(pendingDynamicLinkData != null) {
                    deeplink = pendingDynamicLinkData.link
                }

                if(deeplink != null) {
                    binding.textView14.text = "둘임"
                }
                else {
                    Log.d("TAG", "getDynamicLink: no link found")
                }
            }
            .addOnFailureListener(this) { e -> Log.w("TAG", "getDynamicLink:onFailure", e) }
    }
    }
