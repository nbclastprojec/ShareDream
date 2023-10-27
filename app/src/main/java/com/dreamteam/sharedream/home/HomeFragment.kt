package com.dreamteam.sharedream.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.dreamteam.sharedream.R
import com.dreamteam.sharedream.databinding.FragmentHomeBinding
import android.widget.LinearLayout
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import com.dreamteam.sharedream.adapter.PostClick
import com.dreamteam.sharedream.home.Edit.EditFragment
import com.dreamteam.sharedream.model.AlarmPost
import com.dreamteam.sharedream.view.MyPostFeedDetailFragment
import com.dreamteam.sharedream.view.adapter.HomePostAdapter
import com.dreamteam.sharedream.viewmodel.MyPostFeedViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class HomeFragment : Fragment(),CategoryDialogFragment.CategorySelectionListener {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HomeViewModel

    private lateinit var auth: FirebaseAuth
    private lateinit var homePostAdapter: HomePostAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var mContext: Context
    private var selectedCategory: String = ""


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    private val myPostFeedViewModel: MyPostFeedViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        db = Firebase.firestore

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        viewModel.refreshData.observe(viewLifecycleOwner) { refresh ->
            if (refresh) {
                homePostAdapter.onCategorySelected(selectedCategory)
                Log.d("nyh", "onResume: $selectedCategory")
                viewModel.onRefreshComplete()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)



        binding.floatingActionButton.setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.frag_edit, EditFragment())
                .addToBackStack(null).commit()
            onPause()
        }

        binding.floatingActionButton.setOnClickListener {
            Log.d("MainActivity", "nyh floatingbtn clicked")
            parentFragmentManager.beginTransaction().replace(R.id.frag_edit, EditFragment())
                .addToBackStack(null).commit()
        }

        binding.btnFilter.setOnClickListener {
            val filterDialogFragment = CategoryDialogFragment()
            //다이얼로그에있는 리스너를 달아준다
            filterDialogFragment.setCategorySelectionListener(this)
            filterDialogFragment.show(childFragmentManager, "filter_dialog_tag")
        }


        setupRcv()

        myPostFeedViewModel.postDownload()

        db.collection("Posts").addSnapshotListener { querySnapshot, exception ->
            if (exception != null) {

                Log.d("xxxx", " Home Frag 리스닝 에러 : $exception ")
                return@addSnapshotListener
            }

            Log.d("xxxx", " Home Frag SnapshotListener ")
            myPostFeedViewModel.postDownload()
        }

        // todo Home Frag 게시글 LiveData Observe
        myPostFeedViewModel.postResult.observe(viewLifecycleOwner) {
            val rcvList: MutableList<AlarmPost>? = it

            Log.d("xxxx", " Home Frag Observe ")
            homePostAdapter.submitList(rcvList)
            homePostAdapter.notifyDataSetChanged()
        }
    }

    fun setupRcv() {
        myPostFeedViewModel.postResult.observe(viewLifecycleOwner) {
            val rcvList: List<AlarmPost> = it

            homePostAdapter = HomePostAdapter(requireContext(),object : PostClick {
                override fun postClick(post: AlarmPost) {
                    myPostFeedViewModel.currentPost.value = post
                    myPostFeedViewModel.getCurrentProfileImg(post.uid)
                    parentFragmentManager.beginTransaction().add(
                        R.id.frag_edit,
                        MyPostFeedDetailFragment()
                    ).addToBackStack(null).commit()
                    Log.d("xxxx", " myPostFeed Item Click = $post ")
                }
            }, rcvList)

            binding.homeRecycle.apply {
                setHasFixedSize(true)
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                adapter = homePostAdapter
                addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))
            }
        }
    }

    override fun onCategorySelected(category: String) {
        selectedCategory = category
        // 카테고리에 따라 게시물을 필터링
        homePostAdapter.onCategorySelected(selectedCategory)

    }
//    @SuppressLint("NotifyDataSetChanged")
//    override fun onCategorySelected(category: String) {
//
//        if (category.isEmpty()) {
//            homePostAdapter.submitList(currentList)
//        } else {
//            // 카테고리에 따라 게시물을 필터링하고 어댑터를 업데이트합니다.
//            val filteredList = currentList.filter { it.category == category }
//            homePostAdapter.submitList(filteredList)
//        }
//        homePostAdapter.notifyDataSetChanged()
//    }
}




//class HomeFragment : Fragment(),CategoryDialogFragment.CategorySelectionListener {
//    private lateinit var binding: FragmentHomeBinding
//    private lateinit var mContext: Context
//    private lateinit var homeAdapter: HomeAdapter
//    private val viewModel: HomeViewModel by viewModels()
//    private var selectedCategory: String = ""
//
//
//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        mContext = context
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        binding = FragmentHomeBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onResume() {
//        super.onResume()
//
//        homeAdapter.postDataFromFirestore()
//        Log.d("HomeFrag onResume", "nyh backbtnsuc??")
//
//        viewModel.refreshData.observe(viewLifecycleOwner) { refresh ->
//            if (refresh) {
//                homeAdapter.postDataFromFirestore()
//                viewModel.onRefreshComplete()
//            }
//        }
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        Log.d("Homefrag", "nyh backbtnsuc??")
//
//        mContext = requireContext()
//        homeAdapter = HomeAdapter(mContext)
//
//        binding.homeRecycle.layoutManager =
//            LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
//        binding.homeRecycle.adapter = homeAdapter
//
//
//        homeAdapter.setOnItemClickListener {
//            val intent= Intent(requireContext(),DetailFrameActivity::class.java)
//            startActivity(intent)
//
//
//        binding.btnFilter.setOnClickListener {
//            val filterDialogFragment = CategoryDialogFragment()
//            //다이얼로그에있는 리스너를 달아준다
//            filterDialogFragment.setCategorySelectionListener(this)
//            filterDialogFragment.show(childFragmentManager, "filter_dialog_tag")
//        }
//    }
//
//
//}
//    @SuppressLint("NotifyDataSetChanged")
//    override fun onCategorySelected(category: String) {
//        selectedCategory = category
//        if (category.isNotEmpty()) {
//            homeAdapter.filterByCategory(category)
//            Log.d("HomeFrag", "nyh category = $category")
//        }else {
//            Log.d("nyh", "onCategorySelected: gg")
//        }
//        homeAdapter.notifyDataSetChanged()
//    }
//}
//