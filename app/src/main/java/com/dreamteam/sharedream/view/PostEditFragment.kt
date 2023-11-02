package com.dreamteam.sharedream.view

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dreamteam.sharedream.NicknameCheckDailogFragment
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
import java.net.URI
import java.text.DecimalFormat

class PostEditFragment : Fragment() {

    private var _binding: FragmentPostEditBinding? = null
    private val binding get() = _binding!!

    private val myPostFeedViewModel: MyPostFeedViewModel by activityViewModels()

    private lateinit var writePostImgAdapter: WritePostImageAdapter

    private var uris: MutableList<Uri> = mutableListOf()
    private var imgs: MutableList<String> = mutableListOf()
    private var currentPost: PostRcv? = null

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

        // Text Watcher
        convertCurrencyWon(binding.editEtvPrice)

        // 이미지 선택
        binding.editBtnSelectImg.setOnClickListener {
            pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        // 기존 포스트 정보 가져와서 수정 페이지에 띄우기
        var category: String
        myPostFeedViewModel.currentPostToEditPage.observe(viewLifecycleOwner) { post ->
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

            var defaultChip: Int? = null
            when (category) {
                "의류" -> defaultChip = R.id.cloths_chip1
                "가전제품" -> defaultChip = R.id.machine_chip1
                "스포츠" -> defaultChip = R.id.sport_chip1
                "예술" -> defaultChip = R.id.art_chip1
                "독서" -> defaultChip = R.id.book_chip1
                "뷰티" -> defaultChip = R.id.beauty_chip1
                "문구" -> defaultChip = R.id.toy_chip1
                "가구" -> defaultChip = R.id.furniture1
                "생활" -> defaultChip = R.id.life1
                "식품" -> defaultChip = R.id.food1
                "유아동/출산" -> defaultChip = R.id.kids1
                "반려동물용품" -> defaultChip = R.id.pet1
                "기타" -> defaultChip = R.id.etc1
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
            R.id.furniture1 -> editCategory = "가구"
            R.id.life1 -> editCategory = "생활"
            R.id.food1 -> editCategory = "식품"
            R.id.kids1 -> editCategory = "유아동/출산"
            R.id.pet1 -> editCategory = "반려동물용품"
            R.id.etc1 -> editCategory = "기타"
            else -> {
                Toast.makeText(requireContext(), "카테고리를 선택해주세요.", Toast.LENGTH_SHORT).show()

            }
        }


        // 업로드 하기 todo 게시글 수정 시 기존 이미지 삭제하기
        binding.btnComplete.setOnClickListener {
            Log.d("xxxx", " postEditFrag 완료 버튼 클릭")
            // 게시글 수정을 감지하여 현재 포스트 정보를 변경해주는 Listener 추가 - 디테일 페이지를 닫을 시 stop
//            myPostFeedViewModel.startListening(currentPost!!.timestamp)
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
                "",
                ""



            )

            // 디테일 페이지로 수정 된 게시글 정보 이동하기
            myPostFeedViewModel.setRevisedPost(myPostFeedViewModel.postToPostRcv(post,uris))
            Log.d("xxxx", "onViewCreated: ${uris}")

            val testList = mutableListOf<Any>()
            testList.addAll(uris)
            for (index in testList.indices) {
                if (currentPost!!.imgs.contains(testList[index])) {
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
                Util.showDialog(requireContext(), "이미지 삭제", "선택한 이미지를 삭제 하시겠습니까?") {
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

    private fun convertCurrencyWon(editText: EditText) = with(binding) {
        var result = ""
        val decimalFormat = DecimalFormat("#,###")

        editText.addTextChangedListener(object : TextWatcher {
            @RequiresApi(Build.VERSION_CODES.M)
            override fun beforeTextChanged(charSequence: CharSequence?, i1: Int, i2: Int, i3: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val inputText = s.toString()
                if (inputText != result) {
                    val numericValue = inputText.replace(",", "").toLongOrNull()
                    if (numericValue != null) {
                        result = decimalFormat.format(numericValue)
                        editText.removeTextChangedListener(this)
                        editText.setText(result)
                        editText.setSelection(result.length)
                        editText.addTextChangedListener(this)
                    }
                }
            }

            // 이 밑으론 해당 글과는 딱히 관련 없는 코드로 무시해도 된다.
            @RequiresApi(Build.VERSION_CODES.M)
            override fun afterTextChanged(editable: Editable?) {
            }
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}