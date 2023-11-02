package com.dreamteam.sharedream.view

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
import androidx.core.net.toFile
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dreamteam.sharedream.R
import com.dreamteam.sharedream.Util.Constants
import com.dreamteam.sharedream.Util.Util
import com.dreamteam.sharedream.adapter.ImgClick
import com.dreamteam.sharedream.databinding.FragmentPostEditBinding
import com.dreamteam.sharedream.model.Post
import com.dreamteam.sharedream.model.PostRcv
import com.dreamteam.sharedream.view.adapter.WritePostImageAdapter
import com.dreamteam.sharedream.viewmodel.MyPostFeedViewModel
import com.google.android.material.chip.Chip
import com.google.firebase.Timestamp
import java.io.File
import java.net.URI
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PostEditFragment : Fragment() {

    private var _binding : FragmentPostEditBinding? = null
    private val binding get() = _binding!!

    private val myPostFeedViewModel: MyPostFeedViewModel by activityViewModels()

    private lateinit var writePostImgAdapter: WritePostImageAdapter

    private var uris: MutableList<Uri> = mutableListOf()
    private var imgs: MutableList<String> = mutableListOf()
    private var currentPost : PostRcv? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPostEditBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRcv()

        // 이미지 선택
        binding.editBtnSelectImg.setOnClickListener {
            pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        // 기존 포스트 정보 가져와서 수정 페이지에 띄우기
        var category : String
        myPostFeedViewModel.currentPostToEditPage.observe(viewLifecycleOwner){ post ->
            currentPost = post

            binding.imageCount.text = "${post.imgs.size}/10"
            uris.addAll(post.imgs)

            binding.editTvTitle.setText("${post.title}")
            binding.editEtvPrice.setText("${post.price}")
            binding.editEtvAddress.setText("${post.address}")
            binding.editEtvDesc.setText("${post.desc}")
            category = "${post.category}"

            writePostImgAdapter.submitList(uris)
            writePostImgAdapter.notifyDataSetChanged()

            var defaultChip : Int? = null
            when (category){
                "의류" -> defaultChip = R.id.cloths_chip1
                "가전제품" -> defaultChip = R.id.machine_chip1
                "스포츠" -> defaultChip = R.id.sport_chip1
                "예술" -> defaultChip = R.id.art_chip1
                "독서" -> defaultChip = R.id.book_chip1
                "뷰티" -> defaultChip = R.id.beauty_chip1
                "문구" -> defaultChip = R.id.toy_chip1
            }
            defaultChip?.let {
                val selectedChip: Chip = binding.chipgroup.findViewById(defaultChip) as Chip
                selectedChip.isChecked = true
            }
        }

        var editCategory : String = ""
        when (binding.chipgroup.checkedChipId) {
            R.id.cloths_chip1 -> editCategory = "의류"
            R.id.machine_chip1 -> editCategory = "가전제품"
            R.id.sport_chip1 -> editCategory = "스포츠"
            R.id.art_chip1 -> editCategory = "예술"
            R.id.book_chip1 -> editCategory = "독서"
            R.id.beauty_chip1 -> editCategory = "뷰티"
            R.id.toy_chip1 -> editCategory = "문구"
            else -> {
                Toast.makeText(requireContext(), "카테고리를 선택해주세요.", Toast.LENGTH_SHORT).show()

            }
        }



        // 업로드 하기
        binding.btnComplete.setOnClickListener {
            Log.d("xxxx", " postEditFrag 완료 버튼 클릭")
            val post = Post(
                Constants.currentUserUid!!,
                binding.editTvTitle.text.toString(),
                binding.editEtvPrice.text.toString().toLong(),
                editCategory,
                binding.editEtvAddress.text.toString(),
                //todo ↓ deadline 추가 - 임시로 city 값 넣어둠
                binding.editEtvAddress.text.toString(),
                binding.editEtvDesc.text.toString(),
                listOf(),
                Constants.currentUserInfo!!.nickname,
                currentPost!!.likeUsers,
                currentPost!!.token,
                currentPost!!.timestamp,
                "교환 가능",
                ""
            )
            Log.d("xxxx", "onViewCreated: ${uris}")

            val testList = mutableListOf<Any>()
            testList.addAll(uris)
            for ( index in testList.indices){
                if (currentPost!!.imgs.contains(testList[index])){
                    testList[index] = (URI(testList[index].toString()).toURL())
                } else {
                }
            }

            myPostFeedViewModel.uploadEditPost(testList,post)
            parentFragmentManager.popBackStack()
        }


        binding.topMassage.setOnClickListener {

        }

        // 뒤로가기 버튼
        binding.btnBack.setOnClickListener {
            Log.d("xxxx", " edit frag 뒤로가기 버튼 클릭 ")
            parentFragmentManager.popBackStack()
        }
    }

    // 이미지 선택 기능
    private val pickMultipleMedia =
        registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(10)) { uriList ->
            if (uriList.isNotEmpty()) {
                uriList.forEach { uri ->
                    Log.d("xxxx", "Selected URI: $uri")
                }
                uris.addAll(uriList)

                Log.d("xxxx", "Edit Frag Number of items selected : ${uris} ")
                writePostImgAdapter.submitList(uris)
                binding.imageCount.text = "${uris.size}/10"
                writePostImgAdapter.notifyDataSetChanged()
            } else {
                Log.d("xxxx", "Edit Frag No media selected: ")
            }
        }

    private fun setupRcv() {
        writePostImgAdapter = WritePostImageAdapter(object : ImgClick {
            override fun imgClick(uri: Uri) {
                Log.d("xxxx", "imgClicked: ${uri} , whole uris : $uris")
                // todo 아이템 클릭 시 다이얼로그 or 삭제 버튼
                Util.showDialog(requireContext(),"이미지 삭제","선택한 이미지를 삭제 하시겠습니까?"){
                    uris.remove(uri)
                    binding.imageCount.text = "${uris.size}/10"
                    writePostImgAdapter.notifyDataSetChanged()
                }

            }

        })
        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = writePostImgAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}