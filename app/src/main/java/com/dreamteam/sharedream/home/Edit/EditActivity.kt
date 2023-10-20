package com.dreamteam.sharedream.home.Edit

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.dreamteam.sharedream.R
import com.dreamteam.sharedream.databinding.ActivityEditBinding
import com.dreamteam.sharedream.model.PostData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.text.SimpleDateFormat


class EditActivity : AppCompatActivity() {
    private val binding by lazy { ActivityEditBinding.inflate(layoutInflater) }
    val db = Firebase.firestore
    private var uriList = ArrayList<Uri>()
    private val maxNumber = 10
    lateinit var adapter: EditImageAdapter
    private lateinit var auth: FirebaseAuth
    private val postData = PostData()


    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        adapter = EditImageAdapter(this, uriList)
        binding.recyclerView.adapter = adapter

        binding.recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)


        // ImageView를 클릭할 경우
        binding.imageView9.setOnClickListener {
            if (uriList.count() == maxNumber) {
                //maxNumber가 되면 Toast를 띄우고
                Toast.makeText(this, "이미지는 최대 ${maxNumber}징 까지 첨부할 수 있습니다.", Toast.LENGTH_SHORT)
                    .show()
                //return한ㄷ.
                return@setOnClickListener
            }

            //ACTION_PICK을 통해 앨범으로 이동한다
            val intent = Intent(Intent.ACTION_PICK)
            //type을 image/*로 지정
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            registerForActivityResult.launch(intent)
        }


        binding.btnComplete.setOnClickListener {
            for (i in 0 until uriList.count()) {
                //uriList에 i만큼 imageUplod
                imageUpload(uriList.get(i), i)
                try {
                    //5초동안 대기
                    Thread.sleep(500)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }

            val title = binding.title.text.toString()
            val city = binding.city.text.toString()
            val mainText = binding.mainText.text.toString()

            val valueStr = binding.value.text.toString()
            val value = valueStr.toIntOrNull()

            if (value == null) {
                Toast.makeText(
                    this,
                    "값어치를 올바르게 입력해주세요.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val category = binding.category.text.toString()
                when (binding.chipgroup.checkedChipId) {
                R.id.cloths_chip -> "의류"
                R.id.machine_chip -> "가전제품"
                R.id.sport_chip -> "스포츠"
                R.id.art_chip -> "예술"
                R.id.book_chip -> "독서"
                R.id.beauty_chip -> "뷰티"
                R.id.toy_chip -> "문구"
                else -> {
                    Toast.makeText(
                        this,
                        "카테고리를 선택 해 주세요",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
            }

            val edit = hashMapOf(
                "title" to title,
                "value" to value,
                "category" to category,
                "city" to city,
                "mainText" to mainText
            )

            db.collection("Post")
                .add(edit)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                }

            setResult(Activity.RESULT_OK)
            finish()
            binding.btnBack.setOnClickListener {
                finish()
            }

        }

    }


    // 이미지 선택 화면을 호출하고 선택한 이미지를 처리ㅁㄴ
    @SuppressLint("NotifyDataSetChanged")
    private val registerForActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                RESULT_OK -> {
                    val clipData = result.data?.clipData
                    if (clipData != null) { // 이미지를 여러 개 선택할 경우
                        val clipDataSize = clipData.itemCount
                        val selectableCount = maxNumber - uriList.count()
                        if (clipDataSize > selectableCount) { // 최대 선택 가능한 개수를 초과해서 선택한 경우
                            Toast.makeText(
                                this,
                                "이미지는 최대 ${selectableCount}장까지 첨부할 수 있습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            // 선택 가능한 경우 ArrayList에 가져온 uri를 넣어준다.
                            for (i in 0 until clipDataSize) {
                                uriList.add(clipData.getItemAt(i).uri)
                            }
                        }
                    } else {
                        // 이미지를 한 개만 선택할 경우 null이 올 수 있다.
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

    //textView를 카운팅해주기
    fun printCount() {
        val text = "${uriList.count()}/${maxNumber}"
        binding.imageCount.text = text
    }

    // 파일 업로드
    // 파일을 가리키는 참조를 생성한 후 putFile에 이미지 파일 uri를 넣어 파일을 업로드한다.
    @SuppressLint("SimpleDateFormat")
    private fun imageUpload(uri: Uri?, count: Int) {

        if (uri == null) {
            Log.e(TAG, "URI is null. Image upload failed.")
            Toast.makeText(this, "이미지를 선택해주세요.", Toast.LENGTH_SHORT).show()
            return
        }
        val storage = Firebase.storage
        // storage 참조

        // filename이 같으면 안되므로 기존 패턴 뒤에 count까지 붙여준다
        val fileName = SimpleDateFormat("yyyyMMddHHmmss_${count}").format(java.util.Date())
        //ui로 이미지 분리저장if
        val mountainsRef = storage.reference.child("image").child(auth.uid!!).child(fileName)
        val uploadTask = mountainsRef.putFile(uri)

        uploadTask.addOnSuccessListener { task ->
            Toast.makeText(this, "사진 업로드 성공", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "사진 업로드 실패", Toast.LENGTH_SHORT).show();
        }
    }
}