package com.dreamteam.sharedream

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.dreamteam.sharedream.databinding.FragmentMypageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.text.SimpleDateFormat
import java.util.Date

class MyPageFragment : Fragment() {
    private var _binding: FragmentMypageBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    private lateinit var db: FirebaseDatabase
    private lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        db = Firebase.database
        storage = Firebase.storage

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMypageBinding.inflate(inflater, container, false)

        val downloadTask = storage.reference.child("images").child("${auth.currentUser!!.uid}")
            .downloadUrl
        downloadTask.addOnSuccessListener{
            Log.d("xxxx", "downloadTask Successful uri : $it")
            if (it != null)
                Glide.with(this)
                    .load(it)
                    .into(binding.mypageImage)
        }.addOnFailureListener{
            Log.d("xxxx", "downloadTask Failure Exception : $it")
        }

        db.reference.child("Users").child("${auth.currentUser!!.uid}").addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("xxxx", "snapshot: ${snapshot}")
                    if (snapshot.exists()) {
                        val nickname = snapshot.child("nickname").getValue(String::class.java)
                        var intro = snapshot.child("intro").getValue(String::class.java)
                        if (intro == null){
                            intro = "소개말이 아직 작성되지 않았습니다."
                        }

                        if (nickname != null) {
                            // 변경된 데이터를 처리하거나 UI 업데이트 로직 구현
                            binding.mypageId.text = nickname
                            binding.mypageExplain.text = intro
                        }
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("xxxx", "onCancelled: 데이터 호출 실패")
                }

            }
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 내정보 수정 페이지로 이동
        binding.mypageEditButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.main_frame_layout, MyPageEditFragment()).addToBackStack("myPageEdit").commit()
        }

        // 내가 쓴 글로 이동
        binding.mypageBtnFeed.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.main_frame_layout, MyPostFeedFragment()).addToBackStack(null).commit()
        }

        // 로그아웃 버튼
        binding.logoutButton.setOnClickListener {
            var builder = AlertDialog.Builder(requireContext())
                .setTitle("로그아웃")
                .setMessage("로그아웃 하시겠습니까?")

            val listener = object : DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    when(which){
                        DialogInterface.BUTTON_POSITIVE ->
                            signOut()
                        DialogInterface.BUTTON_NEGATIVE ->
                            return
                    }
                }
            }
            builder.setPositiveButton("확인",listener)
            builder.setNegativeButton("취소",listener)

            builder.show()
        }

        binding.backButtonMypage.setOnClickListener {
            parentFragmentManager.beginTransaction().remove(this).commit()
//            parentFragmentManager.popBackStack()
        }

    }

    private fun signOut() {
        auth.signOut()
        startActivity(Intent(activity,LogInActivity::class.java))
        requireActivity().finish()
    }

//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
}