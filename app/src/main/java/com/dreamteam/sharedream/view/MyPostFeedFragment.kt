package com.dreamteam.sharedream.view

import android.content.Intent
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
import com.dreamteam.sharedream.chat.MessageActivity
import com.dreamteam.sharedream.databinding.FragmentMyPostFeedBinding
import com.dreamteam.sharedream.model.PostRcv
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
            val rcvList: MutableList<PostRcv> = it

            // todo 글 작성 기능 완료 후 - 내가 쓴 글 _ 작성된 글이 없을 때 예외처리
            myPostFeedAdapter.submitList(rcvList)
            if (rcvList.isNotEmpty()) {
                binding.myPostFeedImgNotifyNotfound.visibility = View.GONE
            }
            Log.d("xxxx", " submitList : $rcvList")
            myPostFeedAdapter.notifyDataSetChanged()
        }


        binding.myPostFeedBtnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupRcv() {
        myPostFeedAdapter = MyPostFeedAdapter(object : PostClick {
            override fun postClick(post: PostRcv) {
                myPostFeedViewModel.currentPost.value = post
                parentFragmentManager.beginTransaction().add(
                    R.id.frag_edit,
                    PostDetailFragment()
                ).addToBackStack(null).commit()
            }
        })

        binding.myPostFeedRcv.apply {
            setHasFixedSize(true)
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = myPostFeedAdapter
            addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))
        }

    }

}