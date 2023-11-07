package com.dreamteam.sharedream

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import coil.load
import com.bumptech.glide.Glide
import com.dreamteam.sharedream.Util.Constants
import com.dreamteam.sharedream.Util.Util
import com.dreamteam.sharedream.databinding.FragmentMypageEditProfileBinding
import com.dreamteam.sharedream.viewmodel.MyPostFeedViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class MyPageEditFragment : Fragment() {
    private var photoUri: Uri? = null

    private var _binding: FragmentMypageEditProfileBinding? = null
    private val binding get() = _binding!!

    private val myPostFeedViewModel: MyPostFeedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMypageEditProfileBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        myPostFeedViewModel.currentProfileImg.observe(viewLifecycleOwner){
            binding.profileImage.load(it)
        }
        // 키보드 내리기

        binding.profileId.text = Constants.currentUserInfo?.nickname ?: "닉네임이 설정되지 않았습니다 재접속해주세요"
        binding.profileText.setText(Constants.currentUserInfo?.intro ?: "자기소개가 설정되지 않았습니다.")

        // 이미지 수정 버튼
        binding.btnProfileImgEdit.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        // 유저 정보 수정 완료 버튼
        binding.completeButton.setOnClickListener {
            val nickname = binding.profileId.text.toString()
            val intro = binding.profileText.text.toString()

            if (binding.profileId.text.isEmpty() || binding.profileText.text.isEmpty()) {
                Toast.makeText(requireContext(), " 변경할 자기소개를 입력해주세요", Toast.LENGTH_SHORT).show()
            } else if (photoUri != null) {

                // 캐시로 대체?
                myPostFeedViewModel.uploadUserProfileImg(photoUri!!)
                myPostFeedViewModel.uploadEditUserInfo(nickname, intro)

                Toast.makeText(this.context, "회원정보가 수정되었습니다", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()

            } else {
                myPostFeedViewModel.uploadEditUserInfo(nickname, intro)
                Toast.makeText(this.context, "회원정보가 수정되었습니다", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            }

            // 키보드 입력창 내리기
            Util.hideKeypad(requireContext(),binding.root)
        }

        // 뒤로가기 버튼
        binding.backButtonProfile.setOnClickListener {
            // 키보드 입력 창 내리기
            Util.hideKeypad(requireContext(),binding.root)
            parentFragmentManager.popBackStack()
        }
    }

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                Log.d("xxxx", "selected URI : $uri")
                photoUri = uri

                Glide.with(this)
                    .load(photoUri)
                    .into(binding.profileImage)

            } else {
                Log.d("xxxx", "No media selected ")
            }
        }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}