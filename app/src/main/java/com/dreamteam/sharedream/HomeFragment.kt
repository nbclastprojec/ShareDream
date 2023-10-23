package com.dreamteam.sharedream

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dreamteam.sharedream.ViewModel.HomeViewModel
import com.dreamteam.sharedream.adapter.HomePostAdapter
import com.dreamteam.sharedream.adapter.PostClick
import com.dreamteam.sharedream.databinding.FragmentHomeBinding
import com.dreamteam.sharedream.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class HomeFragment : Fragment() {
    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var auth: FirebaseAuth
    private lateinit var homePostAdapter : HomePostAdapter
    private lateinit var db : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        db = Firebase.firestore

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater,container, false)



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        homeViewModel = (activity as MainActivity).hom

        binding.floatingActionButton.setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.main_frame_layout, EditFragment()).addToBackStack(null).commit()
            onPause()
        }
        homeViewModel = HomeViewModel()

        setupRcv()

        homeViewModel.postDownload()

        db.collection("Posts").addSnapshotListener{ querySnapshot, exception ->
            if (exception != null ) {

                Log.d("xxxx", " Home Frag 리스닝 에러 : $exception ")
                return@addSnapshotListener
            }

            Log.d("xxxx", " Home Frag SnapshotListener ")
            homeViewModel.postDownload()
        }

        homeViewModel.postResult.observe(viewLifecycleOwner){
            val rcvList : List<Post> = it

            Log.d("xxxx", " Home Frag Observe ")
            homePostAdapter.submitList(rcvList)
            homePostAdapter.notifyDataSetChanged()
        }
    }

    override fun onResume() {
        super.onResume()
        homePostAdapter.notifyDataSetChanged()
    }

    override fun onPause() {
        super.onPause()
        Log.d("xxxx", " Home fragment onPause ")
    }

    fun setupRcv() {
        homePostAdapter = HomePostAdapter(object : PostClick {
            override fun postClick(post: Post) {
                Log.d("xxxx", "postClick: ")
            }
        })

        binding.homePostRcv.apply {
            setHasFixedSize(true)
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = homePostAdapter
            addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))
        }

//        downloadPostInfo()
    }

    // todo 업로드 날짜 기준으로 정렬이긴 하지만 img 파일 이름의 업로드 날짜 기준이므로 가독성이 안좋음 update에 추가
    private fun downloadPostInfo(){
        db.collection("Posts").orderBy("uploadDate",
            Query.Direction.DESCENDING).get().addOnSuccessListener { querySnapshot ->
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
                            document.data?.get("uploadDate") as String,
//                                document.data?.get("thumbnail") as Uri
                        )
                    )
                    Log.d("xxxx", " document data : ${document.data} ")
                }

                homePostAdapter.apply {
                    submitList(rcvList)
                    notifyDataSetChanged()
                }
            }
        }
    }

}