package com.dreamteam.sharedream

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.dreamteam.sharedream.view.MyPostFeedDetailFragment

class DetailFrameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_frame)

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        val detailPageFragment = MyPostFeedDetailFragment()
        fragmentTransaction.replace(R.id.fragment_container, detailPageFragment)
        fragmentTransaction.commit()
        val intent = getIntent()
        val value = intent.getStringExtra("value")
        Log.d("document111","documentId:$value")

        val bundle = Bundle()
        bundle.putString("SendDetail", value)
        detailPageFragment.arguments = bundle



    }
}