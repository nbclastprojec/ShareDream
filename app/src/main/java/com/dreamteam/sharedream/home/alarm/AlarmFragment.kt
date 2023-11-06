package com.dreamteam.sharedream.home.alarm

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.dreamteam.sharedream.Util.Constants.Constants.currentUserUid
import com.dreamteam.sharedream.databinding.FragmentAlarmBinding
import com.dreamteam.sharedream.model.Post
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class AlarmFragment : Fragment() {

    val auth = Firebase.auth
    val db = Firebase.firestore

    private lateinit var alarmadapter: AlarmPostAdapter
    private lateinit var binding: FragmentAlarmBinding
    private lateinit var mContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlarmBinding.inflate(inflater, container, false)
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getMyalarm()
        alarmadapter = AlarmPostAdapter(mContext)

        binding.alarmRecycler.layoutManager =
            LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
        binding.alarmRecycler.adapter = alarmadapter

    }

    private fun getMyalarm() {
        val postCollection = db.collection("Posts")
        //query 객체 만들고 get으로 가져오기
        postCollection.whereEqualTo("uid", currentUserUid)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val alarmList = mutableListOf<Post>()

                //for문으로 data를 searchList에 넣어주고 adapter로 전달하기

                for (i in querySnapshot.documents) {
                    val data = i.toObject(Post::class.java)
                    data?.let {
                        alarmList.add(it)
                        Log.d("nyh", "searchTitle: $alarmList")
                    }
                }
                alarmadapter.setData(alarmList)
                val startPosition = alarmList.size
                alarmadapter.notifyItemRangeInserted(startPosition, alarmList.size)
            }.addOnFailureListener { exception ->
                Log.d("nyh", "searchTitle fail : $exception")
            }
    }
}