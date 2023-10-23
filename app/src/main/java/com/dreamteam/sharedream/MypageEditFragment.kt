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
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.dreamteam.sharedream.databinding.FragmentMypageProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.ktx.storage
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MyPageEditFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private lateinit var storage: FirebaseStorage

    private lateinit var photoUri: Uri

    private var _binding: FragmentMypageProfileBinding? = null
    private val binding get() = _binding!!

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
        _binding = FragmentMypageProfileBinding.inflate(inflater, container, false)

        val downloadTask = storage.reference.child("images").child("${auth.currentUser!!.uid}")
            .downloadUrl
        downloadTask.addOnSuccessListener{
            photoUri = it
            Log.d("xxxx", "downloadTask Successful uri : $it")
            if (it != null)
                Glide.with(this)
                    .load(photoUri)
                    .into(binding.profileImage)
        }.addOnFailureListener{
            Log.d("xxxx", "downloadTask Failure Exception : $it")
        }

        db.reference.child("Users").child(auth.currentUser!!.uid).addValueEventListener(
            object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("xxxx", "edit page snapshot: ${snapshot}")
                    if (snapshot.exists()){
                        val nickname = snapshot.child("nickname").getValue(String::class.java)
                        val address = snapshot.child("address").getValue(String::class.java)
                        val phone = snapshot.child("phone").getValue(String::class.java)
                        val intro = snapshot.child("intro").getValue(String::class.java)

                        if (nickname != null && address != null && phone != null){
                            binding.profileAddress.setText(address)
                            binding.profileNickname.setText(nickname)
                            binding.profileNumber.text = phone
                            binding.profileText.setText(intro)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("xxxx", " user info download failure error : $error")
                }

            }
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnProfileImgEdit.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.completeButton.setOnClickListener {
            val address = binding.profileAddress.text.toString()
            val nickname = binding.profileNickname.text.toString()
            val intro = binding.profileText.text.toString()
            imageUpload()
            profileUpload(nickname,intro,address)
        }

        binding.backButtonProfile.setOnClickListener {
            val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(requireView().windowToken, 0)
//            parentFragmentManager.beginTransaction().remove(R.id.main_frame_layout, MyPageFragment()).commit()
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

    private fun profileUpload(nickname : String, intro: String, address: String) {
        val databaseRef = db.reference.child("Users").child(auth.currentUser!!.uid)
        databaseRef.apply {
            child("address").setValue(address)
            child("nickname").setValue(nickname)
            child("intro").setValue(intro)
        }
    }

    private fun imageUpload() {

        val uploadTask = storage.reference.child("images").child("${auth.currentUser!!.uid}")
            .putFile(photoUri!!)
        uploadTask.addOnSuccessListener {
            // 파일 저장 성공 시 이벤트
            Log.d("xxxx", " img upload successful ")
        }.addOnFailureListener {
            // 파일 저장 실패 시 이벤트
            Log.d("xxxx", " img upload failure : $it ")
        }
    }

    private fun getTime(): String {
        val currentDateTime = Calendar.getInstance().time

        return SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.KOREA).format(currentDateTime)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}