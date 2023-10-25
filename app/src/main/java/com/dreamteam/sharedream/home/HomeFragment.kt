package com.dreamteam.sharedream.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dreamteam.sharedream.DetailFrameActivity
import com.dreamteam.sharedream.DetailPageFragment
import com.dreamteam.sharedream.R
import com.dreamteam.sharedream.databinding.FragmentHomeBinding

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
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("Homefrag", "nyh backbtnsuc??")

        mContext = requireContext()
        homeAdapter = HomeAdapter(mContext)

        binding.homeRecycle.layoutManager =
            LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
        binding.homeRecycle.adapter = homeAdapter


        homeAdapter.setOnItemClickListener {
            val intent= Intent(requireContext(),DetailFrameActivity::class.java)
            startActivity(intent)


            binding.btnFilter.setOnClickListener {
                val filterDialogFragment = CategoryDialogFragment()
                //다이얼로그에있는 리스너를 달아준다
                filterDialogFragment.setCategorySelectionListener(this)
                filterDialogFragment.show(childFragmentManager, "filter_dialog_tag")
            }
        }


    }
    @SuppressLint("NotifyDataSetChanged")
    override fun onCategorySelected(category: String) {
        selectedCategory = category
        if (category.isNotEmpty()) {
            homeAdapter.filterByCategory(category)
            Log.d("HomeFrag", "nyh category = $category")
        }else {
            Log.d("nyh", "onCategorySelected: gg")
        }
        homeAdapter.notifyDataSetChanged()
    }


}