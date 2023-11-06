package com.dreamteam.sharedream

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.dreamteam.sharedream.databinding.FragmentYourDetailPageBinding
import com.dreamteam.sharedream.model.PostRcv
import com.dreamteam.sharedream.view.PostDetailFragment
import com.dreamteam.sharedream.view.adapter.YourDetailRecyclerViewAdapter
import com.dreamteam.sharedream.viewmodel.MyPostFeedViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class YourDetailPage : Fragment() {
    private lateinit var _binding: FragmentYourDetailPageBinding
    private val binding get() = _binding!!
    private val myPostFeedViewModel: MyPostFeedViewModel by activityViewModels()
    private val currentPostInfo = mutableListOf<PostRcv>()
    private val adapter = YourDetailRecyclerViewAdapter()


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
                        val desc = document.getString("desc") ?: ""
                        val nickname = document.getString("nickname") ?: ""
                        val deadline = document.getString("deadline")?:""
                        val imgs = document.get("imgs") as List<String>
                        val token =document.getString("token")?:""
                        val imgUris = imgs?.map { Uri.parse(it) } ?: emptyList()
                        val state = document.getString("state") ?: ""
                        val documentId=document.getString("documentId")?:""
                        val locationLatLng = if (document.contains("locationLatLng")) {
                            document["locationLatLng"] as List<Double>
                        } else {
                            emptyList()
                        }
                        val locationKeyword = if (document["locationKeyword"] is List<*>) {
                            (document["locationKeyword"] as List<*>).filterIsInstance<String>()
                        } else {
                            emptyList()
                        }

                        Log.d("asfas", imgUris.toString())



                        timestamp?.let {
                            val post = PostRcv(

                                userUid,
                                title,
                                price,
                                category,
                                address,
                                deadline,
                                desc,
                                imgUris,
                                nickname,
                                listOf(),
                                token,
                                Timestamp.now(),
                                state,
                                documentId,
                                locationLatLng,
                                locationKeyword,
                                endDate


                            )
                            posts.add(post)
                            Log.d("afsasffas",posts.toString())
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
        _binding = FragmentYourDetailPageBinding.inflate(inflater, container, false)

        binding.yourDetailRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.yourDetailRecyclerView.adapter = adapter
        adapter.onItemClickListener = object : YourDetailRecyclerViewAdapter.OnItemClickListener {
            override fun onItemClick(item: PostRcv, position: Int) {
                val imageNames = item.imgs.map { it.toString() }
                getImageUris(imageNames,
                    successCallback = { imageUris ->
                        val updatedItem = item.copy(imgs = imageUris.map { Uri.parse(it) })
                        myPostFeedViewModel.currentPost.postValue(updatedItem)

                        val fragment = PostDetailFragment()
                        val transaction = parentFragmentManager.beginTransaction()
                        transaction.replace(R.id.frag_edit, fragment)
                        transaction.addToBackStack(null)
                        transaction.commit()
                    },
                    failureCallback = {
                        Toast.makeText(requireContext(), "이미지로드실패", Toast.LENGTH_SHORT)

                    }
                )
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

    private fun getImageUris(
        imageNames: List<String>,
        successCallback: (List<String>) -> Unit,
        failureCallback: () -> Unit
    ) {
        val storageReference = Firebase.storage.reference.child("post")

        val imageUris = mutableListOf<String>()
        val totalImages = imageNames.size

        imageNames.forEach { imageName ->
            val imageReference = storageReference.child(imageName)

            imageReference.downloadUrl
                .addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    imageUris.add(imageUrl)

                    if (imageUris.size == totalImages) {
                        successCallback(imageUris)
                    }
                }
                .addOnFailureListener {
                    failureCallback()
                }
        }
    }
}