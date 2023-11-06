package com.dreamteam.sharedream.view

import CalenderFragmentDialog
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
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dreamteam.sharedream.EditCalenderDialog
import com.dreamteam.sharedream.R
import com.dreamteam.sharedream.Util.Constants
import com.dreamteam.sharedream.Util.Util
import com.dreamteam.sharedream.adapter.ImgClick
import com.dreamteam.sharedream.databinding.FragmentPostEditBinding
import com.dreamteam.sharedream.model.LocationData
import com.dreamteam.sharedream.model.Post
import com.dreamteam.sharedream.model.PostRcv
import com.dreamteam.sharedream.view.MapViewFragment.Companion.EDITABLE
import com.dreamteam.sharedream.view.adapter.WritePostImageAdapter
import com.dreamteam.sharedream.viewmodel.MyPostFeedViewModel
import com.google.android.material.chip.Chip
import com.naver.maps.geometry.LatLng
import java.net.URI
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class PostEditFragment : Fragment(), EditCalenderDialog.CalendarDataListener {

    private var _binding : FragmentPostEditBinding? = null
    private val binding get() = _binding!!

    private val myPostFeedViewModel: MyPostFeedViewModel by activityViewModels()

    private lateinit var writePostImgAdapter: WritePostImageAdapter

    private var uris: MutableList<Uri> = mutableListOf()
    private var currentPost: PostRcv? = null

    private var locationLatLng: LocationData? = null

    private var selectedDate: String =""

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

            //
            uris.addAll(post.imgs)

            binding.imageCount.text = "${uris.size}/10"
            binding.editTvTitle.setText("${post.title}")
            binding.editEtvPrice.setText("${post.price}")
            binding.editEtvAddress.setText("${post.address}")
            binding.editEtvDesc.setText("${post.desc}")
            category = "${post.category}"
            binding.calender.setText(formatDateString(post.endDate))

            // 기존 수정 전 게시글 위치 정보 지정해주기
            locationLatLng = LocationData(LatLng(post.locationLatLng[0],post.locationLatLng[1]),post.address,post.locationKeyword)

            // 기존 수정 전 게시글 마감 기한 정보 지정해주기
            selectedDate = post.endDate

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

        var editCategory: String = ""
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

        // 거래장소 변경 내용 반영
        myPostFeedViewModel.locationResult.observe(viewLifecycleOwner){
            it?.let {
                locationLatLng = it
                binding.editEtvAddress.setText(it.address)
                Log.d("xxxx", " 위치 변경 locationLatLng = ${locationLatLng}")
            }
        }


        // 업로드 하기 todo 게시글 수정 시 기존 이미지 삭제하기
        binding.btnComplete.setOnClickListener {
            Log.d("xxxx", " 수정 완료 버튼 클릭, 수정된 카테고리 : $editCategory")
            Log.d("xxxx", " postEditFrag 완료 버튼 클릭")
            // 게시글 수정을 감지하여 현재 포스트 정보를 변경해주는 Listener 추가 - 디테일 페이지를 닫을 시 stop
//            myPostFeedViewModel.startListening(currentPost!!.timestamp)
            val post = Post(
                Constants.currentUserUid!!,
                binding.editTvTitle.text.toString(),
                binding.editEtvPrice.text.toString().replace(",","").toLong(),
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
                currentPost!!.state,
                currentPost!!.documentId,
                listOf(locationLatLng!!.latLng.latitude,locationLatLng!!.latLng.longitude),
                currentPost!!.locationKeyword,
                selectedDate,
            )

            // 디테일 페이지로 수정 된 게시글 정보 이동하기
            myPostFeedViewModel.setRevisedPost(myPostFeedViewModel.postToPostRcv(post, uris))
            Log.d("xxxx", "onViewCreated: ${uris}")

            val uriAndUrlList = mutableListOf<Any>()
            uriAndUrlList.addAll(uris)
            for (index in uriAndUrlList.indices) {
                if (currentPost!!.imgs.contains(uriAndUrlList[index])) {
                    uriAndUrlList[index] = (URI(uriAndUrlList[index].toString()).toURL())
                } else {
                }
            }

            myPostFeedViewModel.uploadEditPost(uriAndUrlList, post)

            parentFragmentManager.popBackStack()
        }


        binding.editBtnLocationPick.setOnClickListener {

            // 위치 권한 확인 후 없다면 요청, 있다면 MapView
            if (!Util.permissionCheck(this.requireContext())) {
                ActivityCompat.requestPermissions(requireActivity(), Util.PERMISSIONS, 5000)
            }
            else {
                parentFragmentManager.beginTransaction().add(R.id.frag_edit, MapViewFragment(EDITABLE))
                    .addToBackStack(null).commit()
            }
        }

        // 뒤로가기 버튼
        binding.btnBack.setOnClickListener {
            Log.d("xxxx", " edit frag 뒤로가기 버튼 클릭 ")
            parentFragmentManager.popBackStack()
        }

        // 마감 기한 지정하기
        binding.calender.setOnClickListener {
            val editCalenderFragmentDialog = EditCalenderDialog(formatDateString(currentPost?.endDate ?: ""))
            editCalenderFragmentDialog.setCalendarDataListener(this)
            editCalenderFragmentDialog.show(requireFragmentManager(), "CalenderDialog")
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

    fun formatDateString(inputDate: String): String {
        val dateParts = inputDate.split("~")

        if (dateParts.size == 2) {
            val startDate = dateParts[0].trim()
            val endDate = dateParts[1].trim()

            val dateFormat = SimpleDateFormat("EEE MM dd HH:mm:ss zzz yyyy", Locale.ENGLISH)
            val startDateFormatted = SimpleDateFormat("yyyy년 MM월 dd일", Locale.ENGLISH).format(dateFormat.parse(startDate))
            val endDateFormatted = SimpleDateFormat("yyyy년 MM월 dd일", Locale.ENGLISH).format(dateFormat.parse(endDate))
            val resultText = "$startDateFormatted 부터, $endDateFormatted 까지"

            return resultText
            Log.d("afaaaaaaa",resultText)

        }
        return inputDate
    }

    override fun onDataSelected(date: Date) {
        val currentDate = currentPost?.endDate
        val dateParts = currentDate?.split("~")
        if (dateParts?.size == 2) {
            val startDate = dateParts[0].trim()
            val dateFormat = SimpleDateFormat("EEE MM dd HH:mm:ss zzz yyyy", Locale.ENGLISH)
            val startDateFormatted = SimpleDateFormat("yyyy년 MM월 dd일", Locale.ENGLISH).format(dateFormat.parse(startDate))

            Log.d("datedate", "${date}")
            val formattedDate = SimpleDateFormat("yyyy년 MM월 dd일").format(date)
            val endTime =
                SimpleDateFormat("EEE MM dd HH:mm:ss zzz yyyy", Locale.ENGLISH).format(date)
            val resultText = "$startDateFormatted 부터, $formattedDate 까지"
            binding.calender.text = resultText
            selectedDate = startDate + "~" + endTime
            Log.d("asasd", selectedDate)
        }

    }
}