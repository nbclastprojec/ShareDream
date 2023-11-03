package com.dreamteam.sharedream.home.Edit

import CalenderFragmentDialog
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
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager

import com.dreamteam.sharedream.R
import com.dreamteam.sharedream.Util.Constants
import com.dreamteam.sharedream.adapter.ImgClick
import com.dreamteam.sharedream.databinding.ActivityEditBinding
import com.dreamteam.sharedream.model.LocationData
import com.dreamteam.sharedream.model.Post
import com.dreamteam.sharedream.view.MapViewFragment
import com.dreamteam.sharedream.view.adapter.WritePostImageAdapter
import com.dreamteam.sharedream.viewmodel.MyPostFeedViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.naver.maps.geometry.LatLng
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.concurrent.fixedRateTimer


class EditFragment : Fragment() , CalenderFragmentDialog.CalendarDataListener {

    private var _binding: ActivityEditBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private var db = Firebase.firestore
    private lateinit var storage: FirebaseStorage

    private var uris: MutableList<Uri> = mutableListOf()
    private var imgs: MutableList<String> = mutableListOf()

    private lateinit var writePostImgAdapter: WritePostImageAdapter

    private val myPostFeedViewModel: MyPostFeedViewModel by activityViewModels()

    private var locationInfo: LocationData? = null

    private var selectedDate: String =""

    var token: String = ""

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

        // 거래장소 변경 내용 반영
        myPostFeedViewModel.locationResult.observe(viewLifecycleOwner) {
            Log.d("xxxx", " edit Fragment 지도 변경 observe ")
            locationInfo = it
            Log.d("xxxx", " 글 작성 지도 위치 설정 $locationInfo")
            it?.let {
                binding.editEtvAddress.setText(it.address)
            }?:binding.editEtvAddress.setText("")
        }

        setupRcv()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 이미지 선택
        binding.editBtnSelectImg.setOnClickListener {
            uris = mutableListOf()
            pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        binding.calender.setOnClickListener {
            val calenderFragmentDialog = CalenderFragmentDialog()
            calenderFragmentDialog.setCalendarDataListener(this)
            calenderFragmentDialog.show(requireFragmentManager(), "CalenderDialog")
        }
        // 게시글 작성 완료
        binding.editBtnComplete.setOnClickListener {
            if (binding.imageCount.text == "0/10") {
                Toast.makeText(requireContext(), " 이미지는 1장 이상 업로드 해야합니다. ", Toast.LENGTH_SHORT)
                    .show()
                Log.d("xxxx", " Upload Failure ")
            } else if (
                binding.editTvTitle.text.isEmpty() || binding.editEtvAddress.text.isEmpty() || binding.editEtvDesc.text.isEmpty() || binding.editEtvPrice.text.isEmpty()
            ) {
                Toast.makeText(requireContext(), " 모든 입력 가능란은 필수 입력사항 입니다.", Toast.LENGTH_SHORT)
                    .show()
            } else if (locationInfo == null) {
                Toast.makeText(requireContext(), " 지도를 통해 거래 장소를 선택 해주세요 ", Toast.LENGTH_SHORT)
                    .show()
            } else {
                imageUpload()
            }

        }

        // todo 임시로 베너 클릭 시 Map이 나오도록 설정.
        binding.topMassage.setOnClickListener {
            parentFragmentManager.beginTransaction().add(R.id.frag_edit, MapViewFragment())
                .addToBackStack(null).commit()
        }

        // 뒤로가기 버튼
        binding.editBtnBack.setOnClickListener {
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
                uris = uriList.toMutableList()

                Log.d("xxxx", "Edit Frag Number of items selected : ${uris} ")
                writePostImgAdapter.submitList(uris)
                binding.imageCount.text = "${uris.size}/10"
                writePostImgAdapter.notifyDataSetChanged()
            } else {
                Log.d("xxxx", "Edit Frag No media selected: ")
            }
        }
    override fun onDataSelected(date: Date) {
        val calendar = Calendar.getInstance()
        val currentDate = calendar.time
        val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.KOREA)
        val currentTime = dateFormat.format(currentDate)
        Log.d("datedate","${date}")
        val formattedDate = SimpleDateFormat("yyyy년 MM월 dd일").format(date)
        binding.calender.text =currentTime+"부터, "+formattedDate+"까지"
        selectedDate = date.toString()



    }

    // 게시글 업로드 기능 - downloadUserInfo()에서 실행
    private fun postUpload(userNickname: String) {
        var category: String
        when (binding.chipgroup.checkedChipId) {
            R.id.cloths_chip1 -> category = "의류"
            R.id.machine_chip1 -> category = "가전제품"
            R.id.sport_chip1 -> category = "스포츠"
            R.id.art_chip1 -> category = "예술"
            R.id.book_chip1 -> category = "독서"
            R.id.beauty_chip1 -> category = "뷰티"
            R.id.toy_chip1 -> category = "문구"
            R.id.furniture1 -> category = "가구"
            R.id.life1 -> category = "생활"
            R.id.food1 -> category = "식품"
            R.id.kids1 -> category = "유아동/출산"
            R.id.pet1 -> category = "반려동물용품"
            R.id.etc1 -> category = "기타"
            else -> {
                Toast.makeText(requireContext(), "카테고리를 선택해주세요.", Toast.LENGTH_SHORT).show()
                return
            }
        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                token = task.result

                val postImg: List<String> = imgs.toList()
                val postLikeUsers = listOf<String>()

                val post = Post(
                    Constants.currentUserUid!!,
                    binding.editTvTitle.text.toString(),
                    binding.editEtvPrice.text.toString().replace(",","").toLong(),
                    category,
                    binding.editEtvAddress.text.toString(),
                    //todo ↓ deadline 추가 - 임시로 city 값 넣어둠
                    binding.editEtvAddress.text.toString(),
                    binding.editEtvDesc.text.toString(),
                    postImg,
                    Constants.currentUserInfo!!.nickname,
                    postLikeUsers,
                    token,
                    Timestamp.now(),
                    "교환 가능",
                    "",
                    listOf(
                        locationInfo!!.latLng.latitude,
                        locationInfo!!.latLng.longitude
                    ), // todo LatLng
                    locationInfo!!.cityInfo, // todo locationKeyword,
                    endTime=selectedDate
                )

                db.collection("Posts")
                    .add(post)
                    .addOnSuccessListener { task ->
                        val documentId = task.id
                        Log.d("xxxx", "postUpload: added with : ${task}")
                        val updatedData = mapOf("documentId" to documentId)
                        db.collection("Posts")
                            .document(documentId) // 생성된 documentId를 가리키는 참조
                            .update(updatedData) // documentId 필드를 업데이트
                            .addOnSuccessListener {
                                requireActivity().supportFragmentManager.beginTransaction()
                                    .remove(this)
                                    .commit()
                            }
                    }.addOnFailureListener { error ->
                        Log.d("xxxx", "Failed to update documentId: $error")
                    }
            }
        }
    }

    // 선택한 이미지 Storage에 업로드
    private fun imageUpload() {

        val time = getTime()
        uris?.let { uris ->
            for (i in uris.indices) {
                val uri = uris[i]
                val fileName = "${time}_$i"

                imgs.add(fileName)

                storage.reference.child("post").child("${time}_$i").putFile(uri)
                    .addOnSuccessListener {
                        // 추후에 필요한 기능 추가
                        Log.d("xxxx", "imageUpload: ${uri}")
                    }
                    .addOnFailureListener {
                        Log.d("xxxx", " Edit Frag imageUpload Failure : $it ")
                    }
            }
            downloadUserInfo()
        }
    }

    // 유저 정보에서 닉네임을 가져와 게시글에 적용 - 게시글 받아올 때마다 유저데이터를 호출하는 것보다 업로드할 때 한번만 호출하는 것이 좋아보임.
    private fun downloadUserInfo() {

        db.collection("UserData").document("${Constants.currentUserUid!!}")
            .get()
            .addOnSuccessListener {
                var nickname = it.data?.get("nickname") as String
                if (nickname != null) {
                    postUpload(nickname)
                } else {
                    Log.d("nyh", "downloadUserInfo nickname null: $nickname ")
                }
            }.addOnFailureListener { error ->
                Log.d("nyh", "downloadUserInfo nickname fail: $error")
            }
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

