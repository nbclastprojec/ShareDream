package com.dreamteam.sharedream

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.viewpager2.widget.ViewPager2
import com.dreamteam.sharedream.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = Firebase.auth
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
            supportFragmentManager.beginTransaction().replace(R.id.main_frame_layout,MyPageFragment()).addToBackStack(null).commit()
        }

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        checkInviteLink()
    }

    private fun checkInviteLink() {
        Firebase.dynamicLinks
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                var deeplink: Uri? = null
                if (pendingDynamicLinkData != null) {
                    deeplink = pendingDynamicLinkData.link
                }

                if (deeplink != null) {
                    Toast.makeText(this, "DeepLink Test 입니다", Toast.LENGTH_LONG).show()
                    binding.textView14.text = "둘"
                } else {
                    Log.d("xxxx", "getDynamicLink: no link found")
                }
            }
            .addOnFailureListener(this) { e -> Log.w("xxxx", "getDynamicLink:onFailure", e) }
    }

}
