package com.dreamteam.sharedream.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import coil.load
import com.dreamteam.sharedream.R
import com.dreamteam.sharedream.Util.Constants
import com.dreamteam.sharedream.Util.Util
import com.dreamteam.sharedream.chat.MessageActivity
import com.dreamteam.sharedream.databinding.FragmentPostDetailBinding
import com.dreamteam.sharedream.model.PostRcv
import com.dreamteam.sharedream.view.adapter.DetailBannerImgAdapter
import com.dreamteam.sharedream.viewmodel.MyPostFeedViewModel

class PostDetailFragment : Fragment() {
    private var _binding: FragmentPostDetailBinding? = null
    private val binding get() = _binding!!

    private val myPostFeedViewModel: MyPostFeedViewModel by activityViewModels()

    private val currentPostInfo = mutableListOf<PostRcv>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPostDetailBinding.inflate(inflater, container, false)


        val imgs = mutableListOf<Uri>()
        myPostFeedViewModel.currentPost.observe(viewLifecycleOwner) {
            binding.detailId.text = it.nickname
            binding.detailAddress.text = it.address
            binding.detailpageTitle.text = it.title
            binding.detailpageCategory.text = it.category
            binding.detailpageExplain.text = it.desc
            binding.detailMoney.text = "${it.price} 원"
            binding.detailTvLikeCount.text = "${it.likeUsers.size}"

            currentPostInfo.add(it)
            Log.d("xxxx", " detail Page PostInfo : $currentPostInfo")
            imgs.addAll(it.imgs)

            // 수정 버튼 visibility
            if (it.uid == Constants.currentUserUid){
                binding.detailBtnEditPost.visibility = View.VISIBLE
            }

            // 관심 목록에 있는 아이템일 경우 binding
            if (it.likeUsers.contains(Constants.currentUserUid)){
                binding.detailBtnSubFavorite.visibility = View.VISIBLE
                binding.detailLike.setImageResource(R.drawable.detail_ic_test_fill_heart)
            } else {
                binding.detailBtnSubFavorite.visibility = View.GONE
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

        // 관심목록 추가 시 카운트에 반영
        myPostFeedViewModel.likeUsersCount.observe(viewLifecycleOwner){
            binding.detailTvLikeCount.text = "${it.size}"
            // 관심 목록에 있는 아이템일 경우 binding
            if (it.contains(Constants.currentUserUid)){
                binding.detailBtnSubFavorite.visibility = View.VISIBLE
                binding.detailLike.setImageResource(R.drawable.detail_ic_test_fill_heart)
            } else {
                binding.detailBtnSubFavorite.visibility = View.GONE
                binding.detailLike.setImageResource(R.drawable.like)
            }
        }

        // 게시물 수정 버튼 클릭 이벤트
        binding.detailBtnEditPost.setOnClickListener {
            myPostFeedViewModel.currentPostToEditPage.postValue(currentPostInfo[0])
//            myPostFeedViewModel.downloadEditPostDefault(currentPostInfo[0].timestamp)
            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.frag_edit,PostEditFragment()).addToBackStack(null).commit()

        }

        // 관심 목록 추가 버튼 클릭 이벤트
        binding.detailBtnAddFavorite.setOnClickListener {
            Util.showDialog(requireContext(),"관심 목록에 추가","내 관심 목록에 추가하시겠습니까?"){
                myPostFeedViewModel.addOrSubFavoritePost(Constants.currentUserUid!!,currentPostInfo[0].timestamp)
                Log.d("xxxx", " detail like btn clicked, post timestamp  =  ${currentPostInfo[0].timestamp}")
            }
        }

        // 관심 목록 제거 버튼 클릭 이벤트
        binding.detailBtnSubFavorite.setOnClickListener {
            Util.showDialog(requireContext(),"관심 목록에서 제거","내 관심 목록에서 게시글의 아이템을 제거하시겠습니까?"){
                myPostFeedViewModel.addOrSubFavoritePost(Constants.currentUserUid!!,currentPostInfo[0].timestamp)
            }
        }

        // 뒤로가기 버튼 클릭 이벤트
        binding.detailCancelButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.detailChatButton.setOnClickListener {
            getUserInformation()
        }
    }
    private fun getUserInformation() {
        val documentIdString = currentPostInfo[0].toString()
        val parts = documentIdString.split("documentId=")
        if (parts.size > 1) {
            val endIndex = parts[1].indexOf(")")
            if (endIndex != -1) {
                val documentId = parts[1].substring(0, endIndex)
                startMessageActivity(documentId)
                Log.d("afafafafafa", "$documentId ")
            }
        }
    }





    private fun startMessageActivity(documentId: String) {
        val intent = Intent(requireContext(), MessageActivity::class.java)
        intent.putExtra("documentId", documentId)
        startActivity(intent)
    }
}