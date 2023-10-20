package com.dreamteam.sharedream.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dreamteam.sharedream.databinding.FragmentHomeBinding
import com.dreamteam.sharedream.home.Edit.HomeViewModel

class HomeFragment : Fragment(), CategoryDialogFragment.CategorySelectionListener {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var mContext: Context
    private lateinit var homeAdapter: HomeAdapter
    private val viewModel: HomeViewModel by viewModels()
    private var selectedCategory: String = ""


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        homeAdapter.postDataFromFirestore()
        Log.d("HomeFrag onResume", "nyh backbtnsuc??")

        viewModel.refreshData.observe(viewLifecycleOwner) { refresh ->
            if (refresh) {
                homeAdapter.postDataFromFirestore()
                viewModel.onRefreshComplete()
            }
        }
        viewModel.refreshData.observe(viewLifecycleOwner){ refresh ->
            if(refresh){
                homeAdapter.filterByCategory(selectedCategory)
                Log.d("nyh", "onResume: $selectedCategory")
                viewModel.onRefreshComplete()

            }

        }

        binding.btnFilter.setOnClickListener {
            val filterDialogFragment = CategoryDialogFragment()
            filterDialogFragment.setCategorySelectionListener(this) // 리스너 설정
            Log.d("HomeFrag", "nyh 왜안되냐고ㅡㅡ")
            filterDialogFragment.show(childFragmentManager, "filter_dialog_tag")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("Homefrag", "nyh backbtnsuc??")

        mContext = requireContext()
        homeAdapter = HomeAdapter(mContext)

        binding.homeRecycle.layoutManager =
            LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
        binding.homeRecycle.adapter = homeAdapter

        binding.btnFilter.setOnClickListener {
            val filterDialogFragment = CategoryDialogFragment()
            filterDialogFragment.setCategorySelectionListener(this)
            filterDialogFragment.show(childFragmentManager, "filter_dialog_tag")
        }
    }

    override fun onCategorySelected(category: String) {
        selectedCategory = category

        if (category.isNotEmpty()) {
            homeAdapter.filterByCategory(category)
            Log.d("HomeFrag", "nyh category = $category")
        } else {
            Log.d("nyh", "onCategorySelected: gg")
        }

        homeAdapter.notifyDataSetChanged()
    }
}
