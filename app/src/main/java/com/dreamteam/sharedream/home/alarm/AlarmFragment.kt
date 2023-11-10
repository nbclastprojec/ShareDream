package com.dreamteam.sharedream.home.alarm

import AlarmPost
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dreamteam.sharedream.R
import com.dreamteam.sharedream.databinding.FragmentAlarmBinding
import com.dreamteam.sharedream.model.PostRcv
import com.dreamteam.sharedream.view.PostDetailFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class AlarmFragment : Fragment() {

    val auth = Firebase.auth
    val db = Firebase.firestore

    private lateinit var alarmadapter: AlarmPostAdapter
    private lateinit var binding: FragmentAlarmBinding
    private lateinit var mContext: Context
    private val viewModel: AlarmViewModel by activityViewModels()

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

        alarmadapter = AlarmPostAdapter(mContext, object : AlarmPostAdapter.OnItemClickListener {
            override fun onItemClick(post: AlarmPost) {
                viewModel.onPostClicked(post)
                viewModel.selectedPost.observe(viewLifecycleOwner) { selectedPost ->
                    Log.d("nyh", "onItemClick: ${selectedPost?.documentId}")
                    if (selectedPost != null) {
                        val docuId = selectedPost.documentId
                        db.collection("Posts").whereEqualTo("documentId", docuId)
                            .get()
                            .addOnSuccessListener { querySnapshot ->
                                val detailList = mutableListOf<PostRcv>()

                                for (i in querySnapshot.documents) {
                                    val data = i.toObject(PostRcv::class.java)
                                    data?.let {
                                        detailList.add(it)
                                    }
                                }

                                if (detailList.isNotEmpty()) {
                                    val selectedPost = detailList.firstOrNull { it.documentId == docuId }
                                    if (selectedPost != null) {
                                        val postDetailFragment = PostDetailFragment()

                                        // Post 객체를 Bundle에 추가하여 PostDetailFragment로 전달
                                        val args = Bundle()
                                        args.putSerializable("post", selectedPost)
                                        postDetailFragment.arguments = args
                                        Log.d("nyh", "onViewCreated: 전달하는 args $args")

                                        val transaction = parentFragmentManager.beginTransaction()
                                        transaction.replace(R.id.frag_edit, postDetailFragment)
                                        transaction.addToBackStack(null)
                                        transaction.commit()
                                    } else {
                                        Log.d("nyh", "onItemClick: selectedPost from Firestore is null")
                                    }
                                } else {
                                    Log.d("nyh", "onItemClick: Firestore data is empty")
                                }
                            }
                            .addOnFailureListener { e ->
                                Log.e("nyh", "Error fetching Firestore data: $e")
                            }
                    } else {
                        // Handle the case where selectedPost is null
                        Log.d("nyh", "onItemClick: selectedPost is null")
                    }
                }
            }
        })

        binding.alarmRecycler.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
        binding.alarmRecycler.adapter = alarmadapter

        viewModel.notiData.observe(viewLifecycleOwner) { notiList ->
            notiList?.let {
                Log.d("nyh", "onViewCreated notiList: $notiList")
                alarmadapter.setData(notiList)
            }
        }
        viewModel.getNotiList()
    }

}
