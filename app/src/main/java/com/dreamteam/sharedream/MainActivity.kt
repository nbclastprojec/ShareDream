package com.dreamteam.sharedream

import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.dreamteam.sharedream.Util.Constants
import com.dreamteam.sharedream.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.dreamteam.sharedream.home.HomeAdapter
import com.dreamteam.sharedream.home.HomeFragment
import com.dreamteam.sharedream.home.Search.SeachFragment
import com.dreamteam.sharedream.model.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var homeAdapter: HomeAdapter
    private val auth: FirebaseAuth = Firebase.auth
    private val db: FirebaseFirestore = Firebase.firestore

    init {
        Constants.currentUserUid = auth.currentUser!!.uid
        db.collection("UserData").document("${auth.currentUser!!.uid}")
            .get()
            .addOnSuccessListener {
                Constants.currentUserInfo = it.toObject<UserData>()
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        Log.d("xxxx", "MainACtivityOnCreate: ${Constants.currentUserUid} ")
        Log.isLoggable("Glide", Log.DEBUG)

        checkAppPushNotification()


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


        binding.editTextSearchView.setOnClickListener {

            supportFragmentManager.beginTransaction()
                .replace(R.id.frag_edit, SeachFragment())
                .addToBackStack(null)
                .commit()

        }

        binding.btnHome.setOnClickListener {
            Log.d("xxxx", " MainActivity Mypage Btn Click ${Constants.currentUserInfo}")
            supportFragmentManager.beginTransaction().add(R.id.frag_edit, MyPageFragment())
                .addToBackStack(null).commit()
        }
    }

    //android 13 postnotification
    private fun checkAppPushNotification() {

        //android 13 이상 && 푸시권한 없음
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            PackageManager.PERMISSION_DENIED == ContextCompat.checkSelfPermission(
                this,
                "android.permission.POST_NOTIFICATIONS"
            )
        ) {
            // 푸쉬 권한 없음
            permissionPostNotification.launch("android.permission.POST_NOTIFICATIONS")
            return
        }
    }


    private val permissionPostNotification =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                //권한 허용
            } else {
                checkAppPushNotification()
            }
        }

    override fun onNewIntent(intent: Intent?) {
        Log.e("YMC", "nyh MainActivity onNewIntent")
        super.onNewIntent(intent)
    }


}