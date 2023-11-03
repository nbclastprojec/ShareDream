package com.dreamteam.sharedream

import YourDetailRecyclerViewAdapter
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.dreamteam.sharedream.databinding.FragmentYourDetailBinding
import com.dreamteam.sharedream.model.PostRcv
import com.dreamteam.sharedream.view.PostDetailFragment
import com.dreamteam.sharedream.viewmodel.MyPostFeedViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class YourDetailFragment : Fragment() {
    private lateinit var _binding: FragmentYourDetailBinding
    private val binding get() = _binding!!
    private val myPostFeedViewModel: MyPostFeedViewModel by activityViewModels()
    private val currentPostInfo = mutableListOf<PostRcv>()
    private val adapter=YourDetailRecyclerViewAdapter()



    private fun YoureDetailInfo(postRcv: PostRcv) {
        binding.profileId.text = postRcv.nickname
        binding.profileTittle.text = postRcv.nickname + "님의 프로필 입니다."
        val UserUid = postRcv.uid
        val firestore = Firebase.firestore
        val documentReference = firestore.collection("UserData").document(UserUid)

        documentReference.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val intro = documentSnapshot.getString("intro")
                    binding.profileText.text = intro.toString()
                } else {
                }
            }
            .addOnFailureListener { exception ->
            }


    }

    private fun InputInforForRecycler(userUid: String) {
        val db = Firebase.firestore
        val query = db.collection("Posts").whereEqualTo("uid", userUid)

        query.get()
            .addOnSuccessListener { querySnapshot ->
                val posts = mutableListOf<PostRcv>()
                if (!querySnapshot.isEmpty) {
                    val documents = querySnapshot.documents

                    for (document in documents) {
                        val title = document.getString("title") ?: ""
                        val category = document.getString("category") ?: ""
                        val price = document.getLong("price") ?: 0
                        val endDate = document.getString("endTime") ?: ""
                        val address = document.getString("address") ?: ""
                        val timestamp = document.getTimestamp("timestamp")
                        val desc= document.getString("desc")?:""
                        val nickname=document.getString("nickname")?:""
                        val imgs = document.get("imgs") as List<String>?
                        val imgUris = imgs?.map { Uri.parse(it) } ?: emptyList()
                        val state=document.getString("state")?:""

                        timestamp?.let {
                            val post = PostRcv(
                                uid = userUid,
                                title = title,
                                price = price,
                                category = category,
                                address = address,
                                deadline = endDate,
                                desc = desc,
                                imgs= imgUris,
                                nickname =nickname,
                                likeUsers = emptyList(),
                                token = "",
                                timestamp = it,
                                state =state,
                                documentId = document.id,
                                endDate = endDate
                            )
                            posts.add(post)
                        }
                    }
                }
                adapter.setItem(posts)
            }
            .addOnFailureListener { exception ->

            }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentYourDetailBinding.inflate(inflater, container, false)

        binding.yourDetailRecyclerView.layoutManager=LinearLayoutManager(requireContext())
        binding.yourDetailRecyclerView.adapter=adapter
        adapter.onItemClickListener = object : YourDetailRecyclerViewAdapter.OnItemClickListener {
            override fun onItemClick(item: PostRcv, position: Int) {

                val clickedItem = item
                Log.d("etwwett", clickedItem.toString())
                myPostFeedViewModel.setCurrentPost(clickedItem)

                val fragment = PostDetailFragment()
                val transaction = parentFragmentManager.beginTransaction()
                transaction.replace(R.id.frag_edit, fragment)
                transaction.addToBackStack(null)
                transaction.commit()
            }
        }





        myPostFeedViewModel.currentProfileImg.observe(viewLifecycleOwner) {
            binding.profileImage.load(it)
        }

        myPostFeedViewModel.currentPost.observe(viewLifecycleOwner) { postRcv ->
            if (postRcv != null) {
                YoureDetailInfo(postRcv)
                InputInforForRecycler(postRcv.uid)
            }
        }

        binding.backButtonProfile.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return binding.root
    }
}