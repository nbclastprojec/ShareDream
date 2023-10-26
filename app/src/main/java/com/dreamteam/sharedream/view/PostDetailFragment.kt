package com.dreamteam.sharedream.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import coil.load
import com.dreamteam.sharedream.R
import com.dreamteam.sharedream.Util.Constants
import com.dreamteam.sharedream.Util.Util
import com.dreamteam.sharedream.databinding.FragmentMyPostFeedDetailBinding
import com.dreamteam.sharedream.databinding.FragmentPostDetailBinding
import com.dreamteam.sharedream.model.Post
import com.dreamteam.sharedream.view.adapter.DetailBannerImgAdapter
import com.dreamteam.sharedream.viewmodel.MyPostFeedViewModel

class PostDetailFragment : Fragment() {
    private var _binding: FragmentPostDetailBinding? = null
    private val binding get() = _binding!!

    private val myPostFeedViewModel: MyPostFeedViewModel by activityViewModels()

    private val postInfo = mutableListOf<Post>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPostDetailBinding.inflate(inflater, container, false)


        val imgs = mutableListOf<String>()
        myPostFeedViewModel.currentPost.observe(viewLifecycleOwner) {
            binding.detailId.text = it.nickname
            binding.detailAddress.text = it.address
            binding.detailpageTitle.text = it.title
            binding.detailpageCategory.text = it.category
            binding.detailpageExplain.text = it.desc
            binding.detailMoney.text = "${it.price} 원"
            binding.detailTvLikeCount.text = "${it.likeUsers.size}"

            postInfo.add(it)
            Log.d("xxxx", " detail Page PostInfo : $postInfo")
            imgs.addAll(it.imgs)

            // 수정 버튼 visibility
            if (it.uid == Constants.currentUserUid){
                binding.detailBtnEditPost.visibility = View.VISIBLE
            }
        }


        myPostFeedViewModel.currentProfileImg.observe(viewLifecycleOwner){
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

        // 수정 버튼 클릭 이벤트
        binding.detailBtnEditPost.setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.frag_edit,PostEditFragment()).addToBackStack(null).commit()

        }

        // 좋아요 버튼 클릭 이벤트
        binding.detailLike.setOnClickListener {
            Util.showDialog(requireContext(),"관심 목록에 추가","내 관심 목록에 추가하시겠습니까?"){
                myPostFeedViewModel.addFavoritePost(Constants.currentUserUid!!,postInfo[0].imgs[0])
                Log.d("xxxx", "Detail page postInfo[0].imgs[0] =  ${postInfo[0].imgs[0]}")
            }
        }

        // 뒤로가기 버튼 클릭 이벤트
        binding.detailCancelButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}