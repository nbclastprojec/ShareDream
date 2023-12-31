package com.dreamteam.sharedream.home.alarm

import AlarmPost
import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
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
import com.dreamteam.sharedream.model.Post
import com.dreamteam.sharedream.model.PostRcv
import com.dreamteam.sharedream.view.PostDetailFragment
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class AlarmFragment : Fragment() {

    val auth = Firebase.auth
    val db = Firebase.firestore
    val storage = Firebase.storage

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

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        alarmadapter = AlarmPostAdapter(mContext)

        alarmadapter.onAlarmItemClickListener = { post ->
            onAlarmItemClick(post)
        }
        alarmadapter.onAlarmItemDeleteClickListener = { position ->
            onDeleteAlarmItem(position)
        }

        alarmadapter.onChatItemDeleteClickListener = { position ->
            onDeleteChatItem(position)
        }




        binding.alarmRecycler.layoutManager =
            LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
        binding.alarmRecycler.adapter = alarmadapter

        viewModel.notiData.observe(viewLifecycleOwner) { notiList ->
            notiList?.let {
                viewModel.getNotiList()
                alarmadapter.setData(notiList)
                alarmadapter.notifyDataSetChanged()
            }
        }
        viewModel.notiChatData.observe(viewLifecycleOwner) { notiChatList ->
            notiChatList?.let{
                viewModel.getChatNotiList()
                alarmadapter.setChatData(notiChatList)
                alarmadapter.notifyDataSetChanged()
            }
        }
        viewModel.getNotiList()
        viewModel.getChatNotiList()
    }

    private fun onAlarmItemClick(post: AlarmPost) {
        viewModel.onPostClicked(post)
        viewModel.selectedPost.observe(viewLifecycleOwner) { selectedPost ->
            Log.d("nyh", "onItemClick: docuId = ${selectedPost?.documentId}")
            if (selectedPost != null) {
                val docuId = selectedPost.documentId
                Log.d("nyh", "onItemClick: docuId = $docuId")
                db.collection("Posts").whereEqualTo("documentId", docuId)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        val detailList = mutableListOf<PostRcv>()

                        for (i in querySnapshot.documents) {
                            val data = i.toObject(Post::class.java)
                            data?.let {
                                convertPostToPostRcv(
                                    it,
                                    querySnapshot,
                                    detailList
                                ) { convertedData ->
                                    if (convertedData.isNotEmpty()) {
                                        val selectedPost =
                                            convertedData.firstOrNull { it.documentId == docuId }
                                        if (selectedPost != null) {
                                            val postDetailFragment = PostDetailFragment()

                                            // Post 객체를 Bundle에 추가하여 PostDetailFragment로 전달
                                            val args = Bundle()
                                            args.putSerializable("post", selectedPost)
                                            postDetailFragment.arguments = args
                                            Log.d("nyh", "onViewCreated: 전달하는 args $args")

                                            val transaction =
                                                parentFragmentManager.beginTransaction()
                                            transaction.replace(
                                                R.id.frag_edit,
                                                postDetailFragment
                                            )
                                            transaction.addToBackStack(null)
                                            transaction.commit()
                                        }
                                    }
                                }
                            }
                        }
                    }
            }
        }
    }

    private fun convertPostToPostRcv(
        post: Post,
        querySnapshot: QuerySnapshot,
        postRcvList: MutableList<PostRcv>,
        callback: (List<PostRcv>) -> Unit
    ) {
        val postImgUris: List<String> = post.imgs
        val postImgList: MutableList<Uri> = mutableListOf()

        val downloadTasks = mutableListOf<Task<Uri>>()
        for (uri in postImgUris) {
            val downloadTask = storage.reference.child("post").child(uri).downloadUrl
            downloadTasks.add(downloadTask)
        }

        Tasks.whenAllSuccess<Uri>(downloadTasks)
            .addOnSuccessListener { uriList ->
                postImgList.addAll(uriList)

                if (postImgUris.size == postImgList.size) {
                    var postRcv = PostRcv(
                        uid = post.uid,
                        title = post.title,
                        price = post.price.toString().replace(",", "").toLong(),
                        category = post.category,
                        address = post.address,
                        deadline = post.deadline,
                        desc = post.desc,
                        imgs = postImgList,
                        nickname = post.nickname,
                        likeUsers = post.likeUsers,
                        token = post.token,
                        timestamp = post.timestamp,
                        state = post.state,
                        documentId = post.documentId,
                        locationLatLng = post.locationLatLng,
                        locationKeyword = post.locationKeyword,
                        endDate = post.endTime
                    )

                    var inserted = false
                    for (index in postRcvList.indices) {
                        if (postRcv.timestamp > postRcvList[index].timestamp) {
                            postRcvList.add(index, postRcv)
                            inserted = true
                            break
                        }
                    }
                    if (!inserted) {
                        postRcvList.add(postRcv)
                    }
                    if (postRcvList.size == querySnapshot.size()) {
                        callback(postRcvList) // 변환한 목록을 콜백으로 반환
                    }
                }
            }
    }
    private fun onDeleteAlarmItem(position: Int) {
        if (position < alarmadapter.alarmItem.size) {
            val post = alarmadapter.alarmItem[position] ?: return
            val myDocuId = post.myDocuId

            val db = Firebase.firestore
            db.collection("notifyList")
                .document(myDocuId)
                .delete()
                .addOnSuccessListener {
                    Log.d("nyh", "deleteItem: 삭제완료")
                    Log.d("nyh", "deleteItem: $myDocuId")

                    val mutableAlarmItem = alarmadapter.alarmItem.toMutableList()
                    mutableAlarmItem.removeAt(position)
                    alarmadapter.alarmItem = mutableAlarmItem

                    alarmadapter.notifyItemRemoved(position)
                }
                .addOnFailureListener { e ->
                    Log.e("nyh", "deleteItem: $e")
                }
        }
    }

    private fun onDeleteChatItem(position: Int) {
        val chatPosition = position - alarmadapter.alarmItem.size
        if (chatPosition >= 0 && chatPosition < alarmadapter.chatItems.size) {
            val chatItem = alarmadapter.chatItems[chatPosition] ?: return
            val myDocuId = chatItem.myDocuId

            val db = Firebase.firestore
            if (myDocuId != null) {
                db.collection("notifyChatList")
                    .document(myDocuId)
                    .delete()
                    .addOnSuccessListener {
                        Log.d("nyh", "deleteItem: 삭제완료")
                        Log.d("nyh", "deleteItem: $myDocuId")

                        val mutableChatItems = alarmadapter.chatItems.toMutableList()
                        mutableChatItems.removeAt(chatPosition)
                        alarmadapter.chatItems = mutableChatItems

                        alarmadapter.notifyItemRemoved(position)
                    }
                    .addOnFailureListener { e ->
                        Log.e("nyh", "deleteItem: $e")
                    }
            }
        }
    }

}