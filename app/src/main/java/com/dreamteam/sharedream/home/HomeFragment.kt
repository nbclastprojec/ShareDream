package com.dreamteam.sharedream.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.dreamteam.sharedream.R
import com.dreamteam.sharedream.databinding.FragmentHomeBinding
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import com.dreamteam.sharedream.NicknameCheckDailogFragment
import com.dreamteam.sharedream.adapter.PostClick
import com.dreamteam.sharedream.home.Edit.EditFragment
import com.dreamteam.sharedream.model.Post
import com.dreamteam.sharedream.model.PostRcv
import com.dreamteam.sharedream.view.PostDetailFragment
import com.dreamteam.sharedream.view.adapter.HomePostAdapter
import com.dreamteam.sharedream.viewmodel.MyPostFeedViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class HomeFragment : Fragment(), CategoryDialogFragment.CategorySelectionListener {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var viewModel: HomeViewModel
    private lateinit var homePostAdapter: HomePostAdapter
    private lateinit var mContext: Context
    private var selectedCategory: String = ""
    private var minPrice: Int = 0
    private var maxPrice: Int = 0


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    private val myPostFeedViewModel: MyPostFeedViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        auth = Firebase.auth
        db = Firebase.firestore

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        viewModel.refreshData.observe(viewLifecycleOwner) { refresh ->
            if (refresh) {
                homePostAdapter.onCategorySelected(selectedCategory, minPrice, maxPrice)
                Log.d("nyh", "onResume: $selectedCategory")
                viewModel.onRefreshComplete()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        val uid = auth.currentUser?.uid.toString()
        checkNickName(uid)

        binding.homeBtnRefreshList.setOnClickListener {
            myPostFeedViewModel.downloadHomePostRcv()
        }

        binding.floatingActionButton.setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.frag_edit, EditFragment())
                .addToBackStack(null).commit()
            onPause()
        }

        binding.floatingActionButton.setOnClickListener {
            Log.d("MainActivity", "nyh floatingbtn clicked")
            myPostFeedViewModel.cleanCurrentPost()
            myPostFeedViewModel.cleanLocationResult()
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

        myPostFeedViewModel.downloadHomePostRcv()




        // todo 쿼리 통해서 가져온 데이터의 마지막 Document 값을 받아와서 해당 Document 부터 X개 가져오는 로직 만들기
        myPostFeedViewModel.postResult.observe(viewLifecycleOwner) {

            homePostAdapter.submitList(it)
        }

        // 게시물 수정 시 Home Fragment 에 해당 게시글 수정 반영
        myPostFeedViewModel.editPostResult.observe(viewLifecycleOwner){
            val currentPostList = homePostAdapter.currentList.toMutableList()
            for (index in currentPostList.indices){
                if (currentPostList[index].timestamp == it.timestamp){
                    currentPostList[index] = it
                    Log.d("xxxx", " Rcv Item Change = $currentPostList")
                    updateRcv(index,it)
                }
            }
        }
    }

    private fun updateRcv(position: Int, post: PostRcv){
        val layoutManager = binding.homeRecycle.layoutManager
        val scrollPosition = if (layoutManager != null){
            (layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        } else { 0 }
        val newList = homePostAdapter.currentList.toMutableList()
        newList[position] = post
        homePostAdapter.submitList(newList)

        binding.homeRecycle.post{
            binding.homeRecycle.scrollToPosition(scrollPosition)
        }
        val sortSpinner: Spinner = binding.sortSpinner
        sortSpinner.adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.sort_home,
            android.R.layout.simple_spinner_item
        )

        sortSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                Log.d("nyh", "onItemSelected: click!")
                if (::homePostAdapter.isInitialized) when (position) {
                    0 -> setupRcv()
                    1 -> {
                        homePostAdapter.sortPriceDesc()
                        scrollToTop()
                    }
                    2 -> {
                        homePostAdapter.sortPriceAsc()
                        scrollToTop()
                    }
                    3 -> {
                        homePostAdapter.sortLikeAsc()
                        scrollToTop()
                    }
                    else -> setupRcv()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.d("nyh", "onNothingSelected: $id")
            }
        }
    }

    fun setupRcv() {
        myPostFeedViewModel.postResult.observe(viewLifecycleOwner) {
            val rcvList: List<PostRcv> = it

            homePostAdapter = HomePostAdapter(requireContext(), object : PostClick {
                override fun postClick(post: PostRcv) {
                    myPostFeedViewModel.setCurrentPost(post)

                    parentFragmentManager.beginTransaction().add(
                        R.id.frag_edit,
                        PostDetailFragment()
                    ).addToBackStack(null).commit()
                    Log.d("xxxx", " myPostFeed Item Click = $post ")
                }
            }, rcvList)

            binding.homeRecycle.apply {
                setHasFixedSize(true)
                layoutManager =
                    LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.VERTICAL,
                        false
                    )
                adapter = homePostAdapter
                addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))
            }
        }
    }


    override fun onCategorySelected(category: String) {
        selectedCategory = category
        // 카테고리에 따라 게시물을 필터링
        homePostAdapter.onCategorySelected(selectedCategory, minPrice, maxPrice)

        if (category == "max1000") {
            // "max1000" 필터를 적용하고 데이터를 필터링
            minPrice = 1
            maxPrice = 1000
        } else if (category == "max10000") {
            minPrice = 1000
            maxPrice = 10000
        }
    }

    fun checkNickName(uid: String) {
        val fireStore = FirebaseFirestore.getInstance()
        val UserData = fireStore.collection("UserData")
        val document = UserData.document(uid)

        document.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val nickname = snapshot.getString("nickname")
                val number = snapshot.getString("number")

                if (number != null) {
                    if (number.isNotEmpty() && (nickname.isNullOrEmpty() || nickname == "닉네임 설정 필요" || nickname == "")) {
                        val nicknameCheckDailogFragment = NicknameCheckDailogFragment()
                        nicknameCheckDailogFragment.show(requireFragmentManager(), "Agree1")
                    }
                }
            } else {
                Toast.makeText(context, "닉네임 설정 오류 에러", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun scrollToTop() {
        val recyclerView = binding.homeRecycle
        recyclerView.scrollToPosition(1) // 맨 위로 스크롤
    }

}