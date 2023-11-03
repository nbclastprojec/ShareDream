package com.dreamteam.sharedream

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
    private val db : FirebaseFirestore = Firebase.firestore

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

        //FCM설정, Token값 가져오기
        //FCMService().getFirebaseToken()
        //PostNotification 대응
        checkAppPushNotification()

        //사용 안하면 삭제하기
        //DynamicLink 수신확인
//        initDynamicLink()


        homeAdapter = HomeAdapter(this)
        val viewPager: ViewPager2 = binding.viewPager
        val tabLayout: TabLayout = binding.tabLayout


        val viewpagerFragmentAdapter = ViewPagerAdapter(this)
        viewPager.adapter = viewpagerFragmentAdapter

        val tabTitles = listOf("교환하기", "내소식")

  /*      if (intent.hasExtra("open_fragment")) {
            val fragmentToOpen = intent.getStringExtra("open_fragment")
            if (fragmentToOpen == "alarm_fragment") {
                val transaction = supportFragmentManager.beginTransaction()
               // val alarmFragment = AlarmFragment()
                val alarmFragmentIndex = 1
                viewPager.setCurrentItem(alarmFragmentIndex, true)
                transaction.replace(R.id.viewPager, alarmFragment)
            }
        }*/

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

        // 권한이 있을 때
        // TODO: 권한이 허용된 경우에 실행할 작업을 정의하세요
    }

    /** 권한 요청 */
    private val permissionPostNotification =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                //권한 허용
            } else {
                checkAppPushNotification()
            }
        }

    /** DynamicLink */
//    private fun initDynamicLink() {
//        val dynamicLinkData = intent.extras
//        if (dynamicLinkData != null) {
//            var dataStr = "DynamicLink 수신받은 값\n"
//            for (key in dynamicLinkData.keySet()) {
//                dataStr += "key: $key / value: ${dynamicLinkData.getString(key)}\n"
//            }
//
//            binding.textView13.text = dataStr
//        }
//    }
    override fun onNewIntent(intent: Intent?) {
        Log.e("YMC", "nyh MainActivity onNewIntent")
        super.onNewIntent(intent)
    }
}