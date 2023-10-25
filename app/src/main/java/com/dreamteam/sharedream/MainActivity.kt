package com.dreamteam.sharedream

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.dreamteam.sharedream.databinding.ActivityMainBinding
import com.dreamteam.sharedream.home.Edit.EditActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.dreamteam.sharedream.home.HomeAdapter
import com.dreamteam.sharedream.home.Search.SeachFragment
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var homeAdapter: HomeAdapter
    private lateinit var auth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

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

        auth=FirebaseAuth.getInstance()



        val viewpagerFragmentAdapter = ViewPagerAdapter(this)
        viewPager.adapter = viewpagerFragmentAdapter

        val tabTitles = listOf("교환하기", "내소식")
//        binding.button.setOnClickListener {
//
//            auth.signOut()
//            Toast.makeText(this,"로그아웃",Toast.LENGTH_SHORT).show()
//            val intent=Intent(this,LogInActivity::class.java)
//            startActivity(intent)
//            finish()
//
//        }
        if (intent.hasExtra("open_fragment")) {
            val fragmentToOpen = intent.getStringExtra("open_fragment")
            if(fragmentToOpen == "alarm_fragment") {
                val transaction = supportFragmentManager.beginTransaction()
                val alarmFragment = AlarmFragment()
                val alarmFragmentIndex = 1
                viewPager.setCurrentItem(alarmFragmentIndex, true)
                transaction.replace(R.id.viewPager, alarmFragment)
            }
        }

        TabLayoutMediator(
            tabLayout,
            viewPager,
            { tab, position -> tab.text = tabTitles[position] }).attach()

        binding.floatingActionButton.setOnClickListener {
            Log.d("MainActivity","nyh floatingbtn clicked")
            val intent = Intent(this, EditActivity::class.java)
            startActivity(intent)
        }
        binding.editTextSearchView.setOnClickListener {

            supportFragmentManager.beginTransaction()
                .replace(R.id.frag_edit,SeachFragment())
                .addToBackStack(null)
                .commit()

        }
    }

    //android 13 postnotification
    private fun checkAppPushNotification(){

        //android 13 이상 && 푸시권한 없음
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            PackageManager.PERMISSION_DENIED == ContextCompat.checkSelfPermission(this, "android.permission.POST_NOTIFICATIONS")
        ) {
            // 푸쉬 권한 없음
            permissionPostNotification.launch("android.permission.POST_NOTIFICATIONS")
            return
        }

        // 권한이 있을 때
        // TODO: 권한이 허용된 경우에 실행할 작업을 정의하세요
    }

    /** 권한 요청 */
    private val permissionPostNotification = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
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