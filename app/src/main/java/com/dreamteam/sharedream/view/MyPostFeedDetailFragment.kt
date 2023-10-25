package com.dreamteam.sharedream.view

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import coil.load
import com.dreamteam.sharedream.databinding.FragmentMyPostFeedDetailBinding
import com.dreamteam.sharedream.view.adapter.DetailBannerImgAdapter
import com.dreamteam.sharedream.viewmodel.MyPostFeedViewModel

class MyPostFeedDetailFragment : Fragment() {
    private var _binding: FragmentMyPostFeedDetailBinding? = null
    private val binding get() = _binding!!

    private val myPostFeedViewModel: MyPostFeedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyPostFeedDetailBinding.inflate(inflater, container, false)


        val imgs = mutableListOf<String>()
        myPostFeedViewModel.currentPost.observe(viewLifecycleOwner) {
            binding.detailId.text = it.nickname
            binding.detailAddress.text = it.address
            binding.detailpageTitle.text = it.title
            binding.detailpageCategory.text = it.category
            binding.detailpageExplain.text = it.desc
            binding.detailMoney.text = "${it.price} 원"
            binding.detailTvLikeCount.text = "${it.likeUsers.size}"

            imgs.addAll(it.imgs)

        }

        myPostFeedViewModel.currentPostProfileImg.observe(viewLifecycleOwner){
            binding.datailProfile.load(it)
        }

        // Viewpager 적용
        val viewPager: ViewPager2 = binding.detailImgViewpager
        val adapter = DetailBannerImgAdapter(imgs)
        viewPager.adapter = adapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.detailCancelButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}