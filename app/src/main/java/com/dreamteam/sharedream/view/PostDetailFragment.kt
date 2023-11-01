package com.dreamteam.sharedream.view

import android.annotation.SuppressLint
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
import com.dreamteam.sharedream.model.Post
import com.dreamteam.sharedream.model.PostRcv
import com.dreamteam.sharedream.view.adapter.DetailBannerImgAdapter
import com.dreamteam.sharedream.viewmodel.MyPostFeedViewModel
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Date

@Suppress("DEPRECATION")
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
            binding.detailpageTime.text = "${time(it.timestamp)}"

            // 게시물 작성자 프로필 이미지 받아오기
            myPostFeedViewModel.downloadCurrentProfileImg(it.uid)

            currentPostInfo.add(it)
            Log.d("xxxx", " detail Page PostInfo : $currentPostInfo")
            imgs.addAll(it.imgs)

            // 수정 버튼 visibility
            if (it.uid == Constants.currentUserUid) {
                binding.detailBtnEditPost.visibility = View.VISIBLE
            }

            // 관심 목록에 있는 아이템일 경우 binding
            if (it.likeUsers.contains(Constants.currentUserUid)) {
                binding.detailBtnSubFavorite.visibility = View.VISIBLE
                binding.detailLike.setImageResource(R.drawable.detail_ic_test_fill_heart)
            } else {
                binding.detailBtnSubFavorite.visibility = View.GONE
            }
        }

        myPostFeedViewModel.currentProfileImg.observe(viewLifecycleOwner) {
            binding.datailProfile.load(it)
        }

        // Viewpager 적용
        val viewPager: ViewPager2 = binding.detailImgViewpager
        val adapter = DetailBannerImgAdapter(imgs)
        viewPager.adapter = adapter

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 관심목록 추가 시 카운트에 반영
        myPostFeedViewModel.likeUsersCount.observe(viewLifecycleOwner) {
            binding.detailTvLikeCount.text = "${it.size}"
            // 관심 목록에 있는 아이템일 경우 binding
            if (it.contains(Constants.currentUserUid)) {
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
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.frag_edit, PostEditFragment()).addToBackStack(null).commit()

        }

        // 관심 목록 추가 버튼 클릭 이벤트
        binding.detailBtnAddFavorite.setOnClickListener {
            Util.showDialog(requireContext(), "관심 목록에 추가", "내 관심 목록에 추가하시겠습니까?") {
                myPostFeedViewModel.addOrSubFavoritePost(currentPostInfo[0].timestamp)
                Log.d(
                    "xxxx",
                    " detail like btn clicked, post timestamp  =  ${currentPostInfo[0].timestamp}"
                )
            }
        }

        // 관심 목록 제거 버튼 클릭 이벤트
        binding.detailBtnSubFavorite.setOnClickListener {
            Util.showDialog(requireContext(), "관심 목록에서 제거", "내 관심 목록에서 게시글의 아이템을 제거하시겠습니까?") {
                myPostFeedViewModel.addOrSubFavoritePost(currentPostInfo[0].timestamp)
            }
        }

        // 뒤로가기 버튼 클릭 이벤트
        binding.detailCancelButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.detailChatButton.setOnClickListener {
            getUserInformation()
        }

        val post = arguments?.getSerializable("post") as Post?

        if (post != null) {
            binding.detailId.text = post.nickname
            binding.detailId.text = post.nickname
            binding.detailAddress.text = post.address
            binding.detailpageTitle.text = post.title
            binding.detailpageCategory.text = post.category
            binding.detailpageExplain.text = post.desc
            binding.detailMoney.text = "${post.price} 원"
            binding.detailTvLikeCount.text = "${post.likeUsers.size}"
            binding.detailpageTime.text = time(post.timestamp)

            myPostFeedViewModel.downloadCurrentProfileImg(post.uid)

            if (post.uid == Constants.currentUserUid) {
                binding.detailBtnEditPost.visibility = View.VISIBLE
            } else {
                binding.detailBtnEditPost.visibility = View.GONE
            }

            // 관심 목록에 있는 아이템의 경우 아이콘 변경 및 관심 목록 제거 버튼 표시
            if (post.likeUsers.contains(Constants.currentUserUid)) {
                binding.detailLike.setImageResource(R.drawable.detail_ic_test_fill_heart)
                binding.detailBtnSubFavorite.visibility = View.VISIBLE
            } else {
                binding.detailLike.setImageResource(R.drawable.like)
                binding.detailBtnSubFavorite.visibility = View.GONE
            }

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

    private fun time(timestamp: Timestamp): String {
        val date: Date = timestamp.toDate()
        // 1. 날짜 형식으로 만들기
        // timestamp를 Date 객체로 변환

        // SimpleDateFormat으로 원하는 형식으로 변환
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")

//            postDate.text = dateFormat.format(date)

        // 2. 현재 날짜, 시간 기준으로 만들기 ( 1시간 전, 2일 전, 1달 전)
        val currentDateTime: Date = Date()
        val diff: Long = currentDateTime.time - date.time
        // 분 단위 차이
        val minutes: Long = diff / (1000 * 60)
        val hours: Long = minutes / 60
        val day: Long = hours / 24
        val week: Long = day / 7
        val month: Long = day / 30
        val year: Long = month / 12

        val result: String =
            when {
                minutes < 1 -> "방금 전"
                minutes < 60 -> "${minutes}분 전"
                hours < 24 -> "${hours}시간 전"
                day in 1..6 -> "${day}일 전"
                day in 7..13 -> "지난 주"
                day in 14..30 -> "${week}주 전"
                month in 1..12 -> "${month}달 전"
                year in 1..100 -> "${year}년 전"
                else -> "${dateFormat.format(date)}"
            }

        return result
    }

    private fun getSearch() {

    }
}