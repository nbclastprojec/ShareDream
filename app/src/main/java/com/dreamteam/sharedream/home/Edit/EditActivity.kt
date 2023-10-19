package com.dreamteam.sharedream.home.Edit

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.dreamteam.sharedream.databinding.ActivityEditBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.text.SimpleDateFormat


@Suppress("DEPRECATION")
class EditActivity : AppCompatActivity() {
    private val binding by lazy { ActivityEditBinding.inflate(layoutInflater) }
    val db = Firebase.firestore
    private lateinit var uri: Uri

    private val registerForActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                RESULT_OK -> {
                    // 변수 uri에 전달 받은 이미지 uri를 넣어준다.
                    uri = result.data?.data!!
                    // 이미지를 ImageView에 표시한다.
                    binding.imageView9.setImageURI(uri)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        binding.imageView9.setOnClickListener {
            // ACTION_PICK을 사용하여 앨범을 호출
            var intent= Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI )
            registerForActivityResult.launch(intent)

        }



        binding.btnComplete.setOnClickListener {

            imageUpload(uri)

            val title = binding.title.text.toString()
            val value = binding.value.text.toString().toInt()
            val category = binding.category.text.toString()
            val city = binding.city.text.toString()
            val during = binding.during.text.toString()
            val mainText = binding.mainText.text.toString()

            val edit = hashMapOf(
                "title" to title,
                "value" to value,
                "category" to category,
                "city" to city,
                "during" to during,
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
            binding.btnBack.setOnClickListener{
                finish()
            }
        }
    }

    private fun imageUpload(uri:Uri?){

        if(uri == null) {
            Log.e(TAG, "URI is null. Image upload failed.")
            Toast.makeText(this, "이미지를 선택해주세요.", Toast.LENGTH_SHORT).show()
            return
        }
        // storage 인스턴스 생성
        val storage = Firebase.storage
        // storage 참조
        val storageRef = storage.getReference("image")
        // 파일 경로와 이름으로 참조 변수 생성
        val fileName = SimpleDateFormat("yyyyMMddHHmmss").format(java.util.Date())
        val mountainsRef = storageRef.child("${fileName}.png")

        val uploadTask = mountainsRef.putFile(uri)
        uploadTask.addOnSuccessListener { task ->

            Toast.makeText(this, "사진 업로드 성공", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            // 파일 업로드 실패
            Toast.makeText(this, "사진 업로드 실패", Toast.LENGTH_SHORT).show();
        }
    }
}