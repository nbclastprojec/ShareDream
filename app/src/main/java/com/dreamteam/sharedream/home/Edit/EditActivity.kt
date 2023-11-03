package com.dreamteam.sharedream.home.Edit

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dreamteam.sharedream.R
import com.dreamteam.sharedream.Util.Constants
import com.dreamteam.sharedream.databinding.ActivityEditBinding
import com.dreamteam.sharedream.model.PostData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.ktx.storage
import java.text.SimpleDateFormat


class EditActivity : AppCompatActivity() {
    private val binding by lazy { ActivityEditBinding.inflate(layoutInflater) }
    val db = Firebase.firestore
    private var uriList = ArrayList<Uri>()
    private val maxNumber = 10
    lateinit var adapter: EditImageAdapter
    private lateinit var auth: FirebaseAuth
    private var postData = PostData()
    private var imageUploadCount = 0
    private var totalImages = 0
    var token: String? = null

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        adapter = EditImageAdapter(this, uriList)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // 이미지 선택 버튼 클릭 시
        binding.imageView9.setOnClickListener {
            if (uriList.count() == maxNumber) {
                Toast.makeText(this, "이미지는 최대 $maxNumber 개까지 첨부할 수 있습니다.", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            //앨범호출 후 여러장 선택하기
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            registerForActivityResult.launch(intent)
        }



        binding.editBtnComplete.setOnClickListener {



            // uriList에 값 들어오면 uploadAta실행
            if (uriList.isNotEmpty()) {
                uploadData()
            } else {
                Toast.makeText(this, "이미지를 선택해주세요.", Toast.LENGTH_SHORT).show()
            }
        }


    }

    @SuppressLint("NotifyDataSetChanged")
    private val registerForActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                RESULT_OK -> {
                    val clipData = result.data?.clipData
                    if (clipData != null) {
                        val clipDataSize = clipData.itemCount
                        val selectableCount = maxNumber - uriList.count()
                        if (clipDataSize > selectableCount) {
                            Toast.makeText(
                                this,
                                "이미지는 최대 $selectableCount 개까지 첨부할 수 있습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            for (i in 0 until clipDataSize) {
                                uriList.add(clipData.getItemAt(i).uri)
                            }
                        }
                    } else {
                        val uri = result?.data?.data
                        if (uri != null) {
                            uriList.add(uri)
                        }
                    }
                    adapter.notifyDataSetChanged()
                    printCount()
                }
            }
        }

    //카운트 하기
    fun printCount() {
        val text = "${uriList.count()}/$maxNumber"
        binding.imageCount.text = text
    }

    @SuppressLint("SimpleDateFormat")
    private fun imageUpload(uri: Uri, count: Int, onComplete: (String) -> Unit) {
        auth = FirebaseAuth.getInstance()
        if (uri == null) {
            Toast.makeText(this, "이미지를 선택해주세요.", Toast.LENGTH_SHORT).show()
            return
        }


        val storage = Firebase.storage
        val fileName = SimpleDateFormat("yyyyMMddHHmmssSSS_${count}").format(java.util.Date())
        val mountainsRef = storage.reference.child("image").child(fileName)
        //.child(auth.currentUser?.uid!!) uid폴더 추가
        val uploadTask = mountainsRef.putFile(uri)

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                throw task.exception!!
            }
            mountainsRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                postData.image = fileName
                onComplete(fileName)
                Toast.makeText(this, "사진 업로드 성공", Toast.LENGTH_SHORT).show()
            } else {
                val error = task.exception
                Log.e(TAG, "이미지 업로드 실패: $error")
                Toast.makeText(this, "사진 업로드 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun uploadData() {
        val title = binding.editTvTitle.text.toString()
        val city = binding.editEtvAddress.text.toString()
        val mainText = binding.editEtvDesc.text.toString()
        val valueStr = binding.editEtvPrice.text.toString()
        val value = valueStr.toIntOrNull()

        if (value == null) {
            Toast.makeText(this, "값어치를 올바르게 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        var category: String
        when (binding.chipgroup.checkedChipId) {
            R.id.cloths_chip1 -> category = "의류"
            R.id.machine_chip1 -> category = "가전제품"
            R.id.sport_chip1 -> category = "스포츠"
            R.id.art_chip1 -> category = "예술"
            R.id.book_chip1 -> category = "독서"
            R.id.beauty_chip1 -> category = "뷰티"
            R.id.toy_chip1 -> category = "문구"
            else -> {
                Toast.makeText(this, "카테고리를 선택해주세요.", Toast.LENGTH_SHORT).show()
                return
            }
        }
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                token = task.result
            }
        }

        // 이미지 업로드 완료 시 데이터 넣기
        val onComplete: (String) -> Unit = { fileName ->

            var edit = hashMapOf(
                "title" to title,
                "value" to value,
                "category" to category,
                "city" to city,
                "mainText" to mainText,
                "uid" to Constants.currentUserUid,
                "image" to fileName,
                "token" to token
            )

            db.collection("Post")
                .add(edit)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "nyh ID: $documentReference")
                    Log.d(TAG, "nyh Image: $fileName")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                }

            setResult(Activity.RESULT_OK)
            finish()
        }

        // 이미지 선택 후 업로드
        totalImages = uriList.size
        for (i in 0 until uriList.count()) {
            imageUpload(uriList[i], i) { imageFileName ->
                imageUploadCount++
                if (imageUploadCount == totalImages) {
                    onComplete(imageFileName)
                }
            }
        }
    }

}