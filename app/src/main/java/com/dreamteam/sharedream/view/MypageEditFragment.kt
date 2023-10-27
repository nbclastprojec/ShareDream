package com.dreamteam.sharedream

import android.app.Instrumentation.ActivityResult
import android.content.Context
import android.content.Intent
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
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    private lateinit var photoUri: Uri

    private var _binding: FragmentMypageEditProfileBinding? = null
    private val binding get() = _binding!!

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
        _binding = FragmentMypageEditProfileBinding.inflate(inflater, container, false)

        downloadProfileImg()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.profileId.text = Constants.currentUserInfo?.nickname?:"닉네임이 설정되지 않았습니다 재접속해주세요"
        binding.profileText.setText(Constants.currentUserInfo?.intro?:"자기소개가 설정되지 않았습니다.")
        binding.btnProfileImgEdit.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.profileId
        binding.completeButton.setOnClickListener {
            if (binding.profileId.text.isEmpty() || binding.profileText.text.isEmpty()) {
                Toast.makeText(requireContext()," 변경할 자기소개를 입력해주세요",Toast.LENGTH_SHORT).show()
            } else if (photoUri != null) {
                val nickname = binding.profileId.text.toString()
                val intro = binding.profileText.text.toString()
                imageUpload()
                profileUpload(nickname, intro)
                parentFragmentManager.popBackStack()
            } else {

            }
        }

        binding.backButtonProfile.setOnClickListener {
            val inputMethodManager =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(requireView().windowToken, 0)
            Toast.makeText(this.context,"회원정보가 수정되었습니다",Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()

        }

        myPostFeedViewModel.currentProfileImg.observe(viewLifecycleOwner){
            binding.profileImage.load(it)
        }
    }
    private fun downloadProfileImg(){
        val downloadTask = storage.reference.child("ProfileImg").child("${Constants.currentUserUid}}")
            .downloadUrl
        downloadTask.addOnSuccessListener {
            photoUri = it
            Log.d("xxxx", "downloadTask Successful uri : $it")
            if (it != null)
                Glide.with(this)
                    .load(photoUri)
                    .into(binding.profileImage)
        }.addOnFailureListener {
            Log.d("xxxx", "downloadTask Failure Exception : $it")
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

    private fun profileUpload(nickname: String, intro: String) {
        db.collection("UserData")
            .document(Constants.currentUserUid!!)
            .update(
                "nickname", "${binding.profileId.text}",
                "intro", "${binding.profileText.text}"
            )
    }

    private fun imageUpload() {

        val uploadTask = storage.reference.child("ProfileImg").child("${Constants.currentUserUid}}")
            .putFile(photoUri!!)
        uploadTask.addOnSuccessListener {
            // 파일 저장 성공 시 이벤트
            Log.d("xxxx", " img upload successful ")
        }.addOnFailureListener {
            // 파일 저장 실패 시 이벤트
            Log.d("xxxx", " img upload failure : $it ")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}