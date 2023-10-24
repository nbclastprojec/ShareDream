package com.dreamteam.sharedream.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dreamteam.sharedream.adapter.PostClick
import com.dreamteam.sharedream.databinding.FragmentMyPostFeedBinding
import com.dreamteam.sharedream.model.PostData
import com.dreamteam.sharedream.view.adapter.MyPostFeedAdapter

class MyPostFeedFragment: Fragment() {
    private var _binding : FragmentMyPostFeedBinding? = null
    private val binding get() = _binding!!

    private lateinit var myPostFeedAdapter : MyPostFeedAdapter
    private lateinit var myPostFeedViewModel: MyPostFeedViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyPostFeedBinding.inflate(inflater,container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        myPostFeedViewModel = MyPostFeedViewModel()

        setupRcv()
        myPostFeedViewModel.postFeedDownload()

        myPostFeedViewModel.postFeedResult.observe(viewLifecycleOwner){
            val rcvList : List<PostData> = it

            myPostFeedAdapter.submitList(rcvList)
            Log.d("xxxx", " submitList : $rcvList")
            myPostFeedAdapter.notifyDataSetChanged()
        }

        binding.backButtonWritepage.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupRcv() {
        myPostFeedAdapter = MyPostFeedAdapter(object : PostClick{
            override fun postClick(post: PostData) {
                Log.d("xxxx", " myPostFeed Item Click = $post ")
            }
        })

        binding.myPostFeedRcv.apply {
            setHasFixedSize(true)
            layoutManager =
                LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
            adapter = myPostFeedAdapter
            addItemDecoration(DividerItemDecoration(context,LinearLayout.VERTICAL))
        }

    }
}