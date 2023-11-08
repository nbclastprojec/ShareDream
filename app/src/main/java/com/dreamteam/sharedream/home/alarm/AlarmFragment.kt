package com.dreamteam.sharedream.home.alarm

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
import com.dreamteam.sharedream.Util.Constants
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

        alarmadapter = AlarmPostAdapter(mContext)

        binding.alarmRecycler.layoutManager =
            LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
        binding.alarmRecycler.adapter = alarmadapter

        viewModel.notiData.observe(viewLifecycleOwner) { notiList ->
            notiList?.let {
                Log.d("nyh", "onViewCreated notiList: $notiList")
                alarmadapter.setData(notiList)
            }
        }
        viewModel.getNotiList()

        viewModel.getDetailList()

        viewModel.detailData.observe(viewLifecycleOwner) { detailData ->

            val bundle = Bundle()
            bundle.putSerializable("detailData", ArrayList(detailData))

            val postDetailFragment = PostDetailFragment()
            postDetailFragment.arguments = bundle

            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.frag_edit, postDetailFragment)
            transaction.addToBackStack(null)
            transaction.commit()

        }
    }
}
//
//    fun getPostDetail(callback: (List<PostRcv>) -> Unit) {
//        db.collection("Posts")
//            .whereEqualTo("documentId", Constants.currentDocumentId)
//            .get()
//            .addOnSuccessListener { task ->
//                val detailList = mutableListOf<PostRcv>()
//
//                for (i in task.documents) {
//                    val data = i.toObject(PostRcv::class.java)
//                    data?.let {
//                        detailList.add(it)
//                    }
//                }
//
//                // 데이터를 번들에 포장
//                val bundle = Bundle()
//                bundle.putSerializable("detailData", ArrayList(detailList))
//
//                // 번들을 다음 프래그먼트로 전달
//                val postDetailFragment = PostDetailFragment()
//                postDetailFragment.arguments = bundle
//
//                val transaction = parentFragmentManager.beginTransaction()
//                transaction.replace(R.id.frag_edit, postDetailFragment)
//                transaction.addToBackStack(null)
//                transaction.commit()
//            }
//            .addOnFailureListener { e ->
//                Log.e("nyh", "getPostDetail: failure", e)
//            }
//    }
