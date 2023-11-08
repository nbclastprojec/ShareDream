package com.dreamteam.sharedream.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dreamteam.sharedream.R
import com.dreamteam.sharedream.adapter.PostClick
import com.dreamteam.sharedream.databinding.FragmentMyPageFavoritePostBinding
import com.dreamteam.sharedream.model.PostRcv
import com.dreamteam.sharedream.view.adapter.MyPageFavoritePostAdapter
import com.dreamteam.sharedream.viewmodel.MyPostFeedViewModel


class MyPageFavoritePost : Fragment() {

    private var _binding: FragmentMyPageFavoritePostBinding? = null
    private val binding: FragmentMyPageFavoritePostBinding get() = _binding!!

    private val myPostFeedViewModel : MyPostFeedViewModel by activityViewModels()

    private lateinit var rcvAdapter : MyPageFavoritePostAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyPageFavoritePostBinding.inflate(inflater, container, false)

        myPostFeedViewModel.downloadFavoritePost()
        setUpRcv()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.myFavoriteBtnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        myPostFeedViewModel.favoritePostResult.observe(viewLifecycleOwner){
            rcvAdapter.submitList(it)
        }
    }

    private fun setUpRcv(){
        rcvAdapter = MyPageFavoritePostAdapter(object : PostClick {
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
            adapter = rcvAdapter
            addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}