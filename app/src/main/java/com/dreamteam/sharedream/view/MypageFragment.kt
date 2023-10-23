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
import com.dreamteam.sharedream.view.MyPostFeedFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.text.SimpleDateFormat
import java.util.Date

class MyPageFragment : Fragment() {
    private var _binding: FragmentMypageBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

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

        downloadProfileImg()
        downloadProfileInfo()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 내정보 수정 페이지로 이동
        binding.mypageEditButton.setOnClickListener {
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

        }

    }
    private fun downloadProfileImg() {
        val downloadTask = storage.reference.child("ProfileImg").child("${auth.currentUser!!.uid}")
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

    }

    fun downloadProfileInfo(){
        db.collection("UserData")
            .document("${auth.currentUser!!.uid}")
            .get()
            .addOnSuccessListener{
                val nickname = it.data?.get("nickname") as String
                val intro = it.data?.get("intro") as String
                Log.d("xxxx", " nickname : $nickname ")
                Log.d("xxxx", " intro : $intro ")

                binding.mypageId.text = nickname
                binding.mypageIntro.text = intro


            }.addOnFailureListener {

            }

    }

    private fun signOut() {
        auth.signOut()
        startActivity(Intent(activity,LogInActivity::class.java))
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}