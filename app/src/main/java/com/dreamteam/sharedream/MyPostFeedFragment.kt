package com.dreamteam.sharedream

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dreamteam.sharedream.adapter.MyPostFeedAdapter
import com.dreamteam.sharedream.adapter.PostClick
import com.dreamteam.sharedream.databinding.FragmentMyPostFeedBinding
import com.dreamteam.sharedream.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MyPostFeedFragment : Fragment() {

    private var _binding: FragmentMyPostFeedBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var myPostFeedAdapter: MyPostFeedAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        db = Firebase.firestore

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyPostFeedBinding.inflate(inflater, container, false)

        setupRcv()



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.writepageTitle.setOnClickListener {
            downloadPostInfo()
        }

    }

    private fun downloadPostInfo() {

        db.collection("Posts")
            .whereEqualTo("uid", "${auth.currentUser!!.uid}")
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val documentSnapshot = querySnapshot.documents
                    Log.d("xxxx", " testA[0].data : ${documentSnapshot[0].data} ")

                    val rcvList: MutableList<Post> = mutableListOf()

                    for (document in documentSnapshot) {
                        rcvList.add(
                            Post(
                                document.data?.get("uid") as String,
                                document.data?.get("title") as String,
                                document.data?.get("price") as String,
                                document.data?.get("category") as String,
                                document.data?.get("address") as String,
                                document.data?.get("deadline") as String,
                                document.data?.get("desc") as String,
                                document.data?.get("imgs") as List<String>,
//                                document.data?.get("thumbnail") as Uri
                            )
                        )
                    }
                    Log.d("xxxx", " result : $rcvList")

                    myPostFeedAdapter.apply {
                        submitList(rcvList)
                        notifyDataSetChanged()
                    }
                }
            }
            .addOnFailureListener {
                Log.d("xxxx", " Failure : $it ")
            }
    }

    private fun setupRcv() {
        myPostFeedAdapter = MyPostFeedAdapter(binding.root,object : PostClick {
            override fun postClick(post: Post) {
                Log.d("xxxx", "postClick: ")
            }

        })

        binding.postFeedRcv.apply {
            setHasFixedSize(true)
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = myPostFeedAdapter
            addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))
        }
    }
}