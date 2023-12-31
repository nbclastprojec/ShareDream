package com.dreamteam.sharedream.home

import android.app.AlertDialog
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
import androidx.activity.addCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import com.dreamteam.sharedream.NicknameCheckDailogFragment
import com.dreamteam.sharedream.Util.ToastMsg
import com.dreamteam.sharedream.adapter.PostClick
import com.dreamteam.sharedream.home.Edit.EditFragment
import com.dreamteam.sharedream.model.PostRcv
import com.dreamteam.sharedream.view.PostDetailFragment
import com.dreamteam.sharedream.view.adapter.HomePostAdapter
import com.dreamteam.sharedream.viewmodel.MyPostFeedViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var viewModel: HomeViewModel
    private lateinit var homePostAdapter: HomePostAdapter
    private lateinit var mContext: Context
    private var selectedCategory: String = ""

    override fun onAttach(context: Context) {

        super.onAttach(context)
        mContext = context
    }

    private val myPostFeedViewModel: MyPostFeedViewModel by activityViewModels()
    private val categoryViewModel: CategoryViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        auth = Firebase.auth
        homePostAdapter = HomePostAdapter(requireContext(), object : PostClick {
            override fun postClick(post: PostRcv) {
                myPostFeedViewModel.setCurrentPost(post)
                parentFragmentManager.beginTransaction().add(
                    R.id.frag_edit,
                    PostDetailFragment()
                ).addToBackStack(null).commit()
            }
        }, emptyList())
        setupRcv()
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        viewModel.refreshData.observe(viewLifecycleOwner) { refresh ->
            if (refresh) {
                Log.d("nyh", "onResume: $selectedCategory")
                viewModel.onRefreshComplete()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val callback = requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {

            DieDialog()
        }
        callback.isEnabled = true


        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        val uid = auth.currentUser?.uid.toString()
        checkNickName(uid)


        binding.homeBtnRefreshList.setOnClickListener {
            myPostFeedViewModel.downloadHomePostRcv()
        }

        binding.floatingActionButton.setOnClickListener {
            myPostFeedViewModel.cleanCurrentPost()
            myPostFeedViewModel.cleanLocationResult()
            parentFragmentManager.beginTransaction().replace(R.id.frag_edit, EditFragment())
                .addToBackStack(null).commit()
        }

        myPostFeedViewModel.downloadHomePostRcv()

        binding.btnFilter.setOnClickListener {
            val categoryDialogFragment = CategoryDialogFragment()
            categoryDialogFragment.show(childFragmentManager, "CategoryDialog")
        }

        myPostFeedViewModel.postResult.observe(viewLifecycleOwner) { posts ->
            homePostAdapter.submitList(posts)
        }
        categoryViewModel.selectedCategory.observe(viewLifecycleOwner) { selectedCategory ->
            viewModel.sortCategorys(selectedCategory)
            Log.d("nyh", "Selected Category in HomeFragment: $selectedCategory")

        }

        // 게시물 수정 시 Home Fragment 에 해당 게시글 수정 반영
        myPostFeedViewModel.editPostResult.observe(viewLifecycleOwner) {
            val currentPostList = homePostAdapter.currentList.toMutableList()
            for (index in currentPostList.indices) {
                if (currentPostList[index].timestamp == it.timestamp) {
                    currentPostList[index] = it
                    Log.d("xxxx", " Rcv Item Change = $currentPostList")
                    updateRcv(index, it)
                }
            }

        }

        viewModel.sortCategory.observe(viewLifecycleOwner) { result ->
            homePostAdapter.submitList(result)
            homePostAdapter.notifyDataSetChanged()
        }
        myPostFeedViewModel.postResult.observe(viewLifecycleOwner) { posts ->
            homePostAdapter.submitList(posts)
        }
        categoryViewModel.selectedCategory.observe(viewLifecycleOwner) { selectedCategory ->
            viewModel.sortCategorys(selectedCategory)
            Log.d("nyh", "Selected Category in HomeFragment: $selectedCategory")

        }
        categoryViewModel.seletedPrice.observe(viewLifecycleOwner) { priceRange ->
            val minPrice = priceRange.first
            val maxPrice = priceRange.second
            homePostAdapter.filteredPrice(minPrice, maxPrice)
        }
        viewModel.sortCategory.observe(viewLifecycleOwner) { result ->
            homePostAdapter.submitList(result)
            homePostAdapter.notifyDataSetChanged()
        }
        val sortSpinner = binding.sortSpinner
        sortSpinner.adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.sort_home,
            android.R.layout.simple_spinner_item
        )
        sortSpinner.setSelection(0)

        sortSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (position) {
                    0 -> viewModel.sortCategorys("")
                    1 -> homePostAdapter.sortPriceDesc()
                    2 -> homePostAdapter.sortPriceAsc()
                    3 -> homePostAdapter.sortLikeDesc()
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                viewModel.sortCategorys("")
            }
        }
    }

    private fun updateRcv(position: Int, post: PostRcv) {
        val layoutManager = binding.homeRecycle.layoutManager
        val scrollPosition = if (layoutManager != null) {
            (layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        } else {
            0
        }
        val newList = homePostAdapter.currentList.toMutableList()
        newList[position] = post
        homePostAdapter.submitList(newList)

        binding.homeRecycle.post {
            binding.homeRecycle.scrollToPosition(scrollPosition)
        }
    }

    fun setupRcv() {
        myPostFeedViewModel.postResult.observe(viewLifecycleOwner) { posts ->
            binding.homeRecycle.apply {
                setHasFixedSize(true)
                layoutManager =
                    LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.VERTICAL,
                        false
                    )
                adapter = homePostAdapter
            }
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
                ToastMsg.makeToast(requireContext(), "닉네임 설정이 올바르지 않습니다.")
            }
        }
    }
    private fun DieDialog() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("종료")
        alertDialogBuilder.setMessage("정말로 종료하시겠습니까?")
        alertDialogBuilder.setPositiveButton("예") { _, _ ->
            requireActivity().finish()
        }
        alertDialogBuilder.setNegativeButton("아니오") { _, _ ->

        }
        alertDialogBuilder.create().show()
    }


}