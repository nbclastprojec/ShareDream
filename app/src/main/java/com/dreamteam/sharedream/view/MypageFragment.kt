package com.dreamteam.sharedream

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import coil.load
import com.bumptech.glide.Glide
import com.dreamteam.sharedream.Util.Constants
import com.dreamteam.sharedream.Util.Util
import com.dreamteam.sharedream.chat.ChatFragment
import com.dreamteam.sharedream.databinding.FragmentMypageBinding
import com.dreamteam.sharedream.view.InquiryFragment
import com.dreamteam.sharedream.view.MyPageFavoritePost
import com.dreamteam.sharedream.view.MyPostFeedFragment
import com.dreamteam.sharedream.viewmodel.MyPostFeedViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class MyPageFragment : Fragment() {
    private var _binding: FragmentMypageBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    private val myPostFeedViewModel: MyPostFeedViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        db = Firebase.firestore
        storage = Firebase.storage

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMypageBinding.inflate(inflater, container, false)



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mypageId.text = Constants.currentUserInfo?.nickname ?: "닉네임이 설정되지 않았습니다 재접속해주세요"
        binding.mypageIntro.setText(Constants.currentUserInfo?.intro ?: "자기소개가 설정되지 않았습니다.")
        myPostFeedViewModel.downloadCurrentProfileImg(Constants.currentUserUid!!)

        // 변경된 자기소개, 닉네임 반영
        myPostFeedViewModel.myPageResult.observe(viewLifecycleOwner) {
            binding.mypageIntro.text = it.intro
            binding.mypageId.text = it.nickname
        }

        // 수정된 이미지로 변경
        myPostFeedViewModel.currentProfileImg.observe(viewLifecycleOwner) {
            binding.mypageImage.load(it)
        }


        // 내정보 수정 페이지로 이동
        binding.mypageEditButton.setOnClickListener {
            myPostFeedViewModel.downloadCurrentProfileImg(Constants.currentUserUid!!)
            parentFragmentManager.beginTransaction()
                .replace(R.id.frag_edit, MyPageEditFragment()).addToBackStack("myPageEdit").commit()
        }

        // 내가 쓴 글로 이동
        binding.btnMyPostFeed.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.frag_edit, MyPostFeedFragment()).addToBackStack(null).commit()
        }

        // 로그아웃 버튼
        binding.btnSignout.setOnClickListener {
            signOut()
        }

        // 뒤로가기 버튼
        binding.backButtonMypage.setOnClickListener {
            parentFragmentManager.beginTransaction().remove(this).commit()

        }

        // 1:1 문의하기 버튼
        binding.btnInquiry.setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.frag_edit,InquiryFragment()).addToBackStack(null).commit()
        }

        // 내 채팅 목록 버튼 클릭 이벤트
        binding.btnChatlist.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.frag_edit, ChatFragment()).addToBackStack(null).commit()
        }

        // 내 관심 목록으로 이동 버튼
        binding.btnMyFavoriteList.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.frag_edit, MyPageFavoritePost()).addToBackStack(null).commit()
        }


    }

    private fun downloadProfileImg() {
        val downloadTask = storage.reference.child("ProfileImg").child(Constants.currentUserUid!!)
            .downloadUrl
        downloadTask.addOnSuccessListener {
            Log.d("xxxx", "downloadTask Successful uri : $it")
            if (it != null)
                Glide.with(this)
                    .load(it)
                    .into(binding.mypageImage)
        }.addOnFailureListener {
            Log.d("xxxx", "downloadTask Failure Exception : $it")
        }

    }

    fun downloadProfileInfo() {
        db.collection("UserData")
            .document("${Constants.currentUserUid}")
            .get()
            .addOnSuccessListener {
                val nickname = it.data?.get("nickname") as String
                val intro = it.data?.get("intro") as String
                Log.d("xxxx", " nickname : $nickname ")
                Log.d("xxxx", " intro : $intro ")

                binding.mypageId.text = nickname
                binding.mypageIntro.text = intro


            }.addOnFailureListener {

            }
    }

    private fun deleteUserAccount() {

        Util.showDialog(requireContext(), "회원 탈퇴", "회원 탈퇴 시 기존 정보를 다시 복구할 수 없습니다.") {
            auth.signOut()
            auth.currentUser!!.delete()
            startActivity(Intent(activity, LogInActivity::class.java))
            requireActivity().finish()
        }
    }

    private fun signOut() {


        Util.showDialog(requireContext(), "로그아웃 ", "로그아웃 하시겠습니까?") {
            auth.signOut()
            startActivity(Intent(activity, LogInActivity::class.java))
            requireActivity().finish()
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}