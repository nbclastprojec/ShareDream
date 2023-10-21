package com.dreamteam.sharedream

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.dreamteam.sharedream.adapter.WritePostImageAdapter
import com.dreamteam.sharedream.adapter.ImgClick
import com.dreamteam.sharedream.databinding.FragmentEditBinding
import com.dreamteam.sharedream.model.Post
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class EditFragment : Fragment() {
    private var _binding: FragmentEditBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private var db = Firebase.firestore
    private lateinit var storage: FirebaseStorage

    private var uris: List<Uri>? = null
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
        _binding = FragmentEditBinding.inflate(inflater, container, false)


        setupRcv()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 이미지 선택
        binding.writeBtnAddImg.setOnClickListener {
            pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        // 게시글 작성 완료
        binding.writeBtnComplete.setOnClickListener {
            imageUpload()
        }

        binding.editFragMenuTitme.setOnClickListener {
            Log.d("xxxx", " title 클릭 ")
            postUpload()
        }

    }

    private val pickMultipleMedia =
        registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(10)) { uriList ->
            if (uriList.isNotEmpty()) {
                uris = uriList
                Log.d("xxxx", "Edit Frag Number of items selected : ${uriList.size} ")
                writePostImgAdapter.submitList(uriList)
                writePostImgAdapter.notifyDataSetChanged()
            } else {
                Log.d("xxxx", "Edit Frag No media selected: ")
            }
        }

    private fun postUpload() {
        val postUid = auth.currentUser!!.uid
        val postTitle = binding.writeTitle.text.toString()
        val postPrice = binding.writePrice.text.toString()
        val postCategory = binding.writeCategory.text.toString()
        val postAddress = binding.writeAddress.text.toString()
        val postDeadline = binding.writeDeadline.text.toString()
        val postDesc = binding.writeDesc.text.toString()
        val postImg: List<String> = imgs.toList()
        val post: Post = Post(
            postUid,
            postTitle,
            postPrice,
            postCategory,
            postAddress,
            postDeadline,
            postDesc,
            postImg
        )


        db.collection("Posts")
            .add(post)
            .addOnSuccessListener {
                Log.d("xxxx", "postUpload: added with : ${it}")
            }
    }

    private fun imageUpload() {

        val time = getTime()
        uris?.let { uris ->
            for (i in uris.indices) {
                val uri = uris[i]
                val fileName = "${time}_$i"

                imgs.add(fileName)

                storage.reference.child("post").child(fileName).putFile(uri).addOnSuccessListener {
                }.addOnFailureListener {

                    Log.d("xxxx", " Edit Frag imageUpload Failure : $it ")
                }
            }
            postUpload()
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
        binding.writeRcvImgSelected.apply {
            setHasFixedSize(true)
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = writePostImgAdapter
        }
    }

    private fun getTime(): String {
        val currentDateTime = Calendar.getInstance().time
        val dateFormat =
            SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.KOREA).format(currentDateTime)

        return dateFormat
    }
}