package com.dreamteam.sharedream.home.Edit

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.dreamteam.sharedream.adapter.ImgClick
import com.dreamteam.sharedream.databinding.ActivityEditBinding
import com.dreamteam.sharedream.model.Post
import com.dreamteam.sharedream.view.adapter.WritePostImageAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class EditFragment : Fragment() {
    private var _binding: ActivityEditBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private var db = Firebase.firestore
    private lateinit var storage: FirebaseStorage

    private var uris: MutableList<Uri> = mutableListOf()
    private var imgs: MutableList<String> = mutableListOf()

    private lateinit var writePostImgAdapter: WritePostImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth

        storage = Firebase.storage

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ActivityEditBinding.inflate(inflater, container, false)


        setupRcv()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 이미지 선택
        binding.camera.setOnClickListener {
            uris = mutableListOf()
            pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))

        }

        // 게시글 작성 완료
        binding.btnComplete.setOnClickListener {
            imageUpload()
        }

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

    }

    private val pickMultipleMedia =
        registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(10)) { uriList ->
            if (uriList.isNotEmpty()) {
                uriList.forEach {uri ->
                    Log.d("xxxx", "Selected URI: $uri")
                }
                uris = uriList.toMutableList()

                Log.d("xxxx", "Edit Frag Number of items selected : ${uris} ")
                writePostImgAdapter.submitList(uris)
                writePostImgAdapter.notifyDataSetChanged()
            } else {
                Log.d("xxxx", "Edit Frag No media selected: ")
            }
        }

    private fun postUpload(time: String) {
        val postUid = auth.currentUser!!.uid
        val postTitle = binding.title.text.toString()
        val postPrice = binding.value.text.toString()
        val postCategory = binding.category.text.toString()
        val postAddress = binding.city.text.toString()
        //todo 기한 추가 - 임시로 city 값 넣어둠
        val postDeadline = binding.city.text.toString()
        val postDesc = binding.mainText.text.toString()
        val postImg: List<String> = imgs.toList()
        //todo 닉네임 추가
        val nickname = "닉네임입니다"
        val postLikeUsers = listOf<String>()
        val post: Post = Post(
            postUid,
            postTitle,
            postPrice,
            postCategory,
            postAddress,
            postDeadline,
            postDesc,
            postImg,
            nickname,
            postLikeUsers

        )

        db.collection("Posts")
            .add(post)
            .addOnSuccessListener {
                Log.d("xxxx", "postUpload: added with : ${it}")
                requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()


            }
    }

    private fun imageUpload() {

        val time = getTime()
        uris?.let { uris ->
            for (i in uris.indices) {
                val uri = uris[i]
                val fileName = "${time}_$i"

                imgs.add(fileName)

                storage.reference.child("post").child("${time}_$i").putFile(uri).addOnSuccessListener {
                }.addOnFailureListener {

                    Log.d("xxxx", " Edit Frag imageUpload Failure : $it ")
                }
            }
            postUpload(time)
        }
        Log.d("xxxx", " Edit Frag Post Info Img : $imgs ")
        Log.d("xxxx", "Edit Frag Post Info uris : $uris")
    }

    private fun setupRcv() {
        writePostImgAdapter = WritePostImageAdapter(object : ImgClick {
            override fun imgClick(uri: Uri) {
                Log.d("xxxx", "imgClicked: ${uri} , whole uris : $uris")
            }

        })
        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = writePostImgAdapter
        }
    }

    private fun getTime(): String {
        val currentDateTime = Calendar.getInstance().time
        val dateFormat =
            SimpleDateFormat("yyyy.MM.dd HH:mm:ss.SSS", Locale.KOREA).format(currentDateTime)

        return dateFormat
    }
}