package com.dreamteam.sharedream.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dreamteam.sharedream.R
import com.dreamteam.sharedream.adapter.PostClick
import com.dreamteam.sharedream.databinding.FragmentMyPostFeedBinding
import com.dreamteam.sharedream.model.AlarmPost
import com.dreamteam.sharedream.view.adapter.MyPostFeedAdapter
import com.dreamteam.sharedream.viewmodel.MyPostFeedViewModel

class MyPostFeedFragment : Fragment() {
    private var _binding: FragmentMyPostFeedBinding? = null
    private val binding get() = _binding!!

    private lateinit var myPostFeedAdapter: MyPostFeedAdapter
    private val myPostFeedViewModel: MyPostFeedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyPostFeedBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        myPostFeedViewModel = MyPostFeedViewModel()

        setupRcv()
        myPostFeedViewModel.postFeedDownload()

        myPostFeedViewModel.postFeedResult.observe(viewLifecycleOwner) {
            val rcvList: MutableList<AlarmPost> = it

            // todo 글 작성 기능 완료 후 - 내가 쓴 글 _ 작성된 글이 없을 때 예외처리
            myPostFeedAdapter.submitList(rcvList)
            Log.d("xxxx", " submitList : $rcvList")
            myPostFeedAdapter.notifyDataSetChanged()
        }


        binding.backButtonWritepage.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupRcv() {
        myPostFeedAdapter = MyPostFeedAdapter(object : PostClick {
            override fun postClick(post: AlarmPost) {
                myPostFeedViewModel.currentPost.value = post
                myPostFeedViewModel.testA()
                parentFragmentManager.beginTransaction().add(
                    R.id.frag_edit,
                    MyPostFeedDetailFragment()
                ).addToBackStack(null).commit()
                Log.d("xxxx", " myPostFeed Item Click = $post ")
            }
        }
        )

        binding.myPostFeedRcv.apply {
            setHasFixedSize(true)
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = myPostFeedAdapter
            addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))
        }

    }
}