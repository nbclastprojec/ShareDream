package com.dreamteam.sharedream

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.dreamteam.sharedream.databinding.FragmentSignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.FileOutputStream


class SignUpFragment : Fragment() {
    private lateinit var binding:FragmentSignupBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var storage : FirebaseStorage


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        storage = Firebase.storage
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentSignupBinding.inflate(inflater,container,false)
        auth = FirebaseAuth.getInstance()


        binding.agree1.setOnClickListener{
            val agreeDialog=AgreeFragment()
            agreeDialog.show(requireActivity().supportFragmentManager,"Agree1")
        }
        binding.agree2.setOnClickListener{
            val agreeDialog=PersonalAgree()
            agreeDialog.show(requireActivity().supportFragmentManager,"Agree2")
        }


        binding.btnSignup.setOnClickListener {


            check()


            }



        return binding.root
    }

    private fun check() {
        val checkbox=binding.checkBox
        val email = binding.editEmail.text.toString()
        val password = binding.editPassword.text.toString()
        val passwordCk=binding.editPasswordCheck.text.toString()
        val number=binding.editPhoneNumber.text.toString()
        val id = binding.eidtId.text.toString()
        if (email.isEmpty()) {
            binding.editEmail.error = "이메일을 입력해주세요."

        } else if (password.isEmpty()) {
            binding.editPassword.error = "비밀번호를 입력해주세요."

        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email)
                .matches()
        ) {   //android.util.Patterns.EMAIL_ADDRESS == 안드로이드에서 제공하는 기본 이메일 형식패턴
            binding.editEmail.error = "이메일 형식이 아닙니다."

        } else if (!special(password)) {
            binding.editPassword.error = "최소 하나 이상의 특수문자를 입력해 주세요."

        } else if (password!=passwordCk){
            binding.editPasswordCheck.error="비밀번호와 똑같이 입력해 주세요."


        } else if (!phone(number)){
            binding.editPassword.error="핸드폰 번호는 숫자로만 이루어져야 합니다."



        }

        else if (!checkbox.isChecked) {
            Toast.makeText(requireContext(),"약관동의를 체크해주세요.",Toast.LENGTH_SHORT).show()

        }else {
            checkId(id)




        }

    }




    fun special(text:String):Boolean{
        val specialWord= setOf('!','@','#','$','%','^','&','*','(',')','_','-','<','>','=','+','?')//특수문자
        return text.any{it in specialWord//비밀번호 중 하나(any)라도 문자 안에 특수문자(speacialWord)가 포함되어있으면 true반환
        }

    }
    fun phone(text: String):Boolean{
        return text.all {it.isDigit()}
    }
    private fun checkId(id: String) {
        val firestore = FirebaseFirestore.getInstance()
        val UserData = firestore.collection("UserData")
        UserData.whereEqualTo("id", id).addSnapshotListener{ snapshots, b ->
            if(snapshots?.documents?.isEmpty() == true){
                Toast.makeText(requireContext(), "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                auth.signOut()
                val loginFragment=LoginFragment()
                val transaction=requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fragment_container,loginFragment)
                transaction.addToBackStack(null)
                transaction.commit()

                Log.d("taskTest","tastTest")
                createAccount()
            }else{
                Log.d("taskTestTwo","tastTest")
                binding.eidtId.error="중복된 아이디가 존재합니다."
            }


        }


    }
    private fun createAccount(){
        auth.createUserWithEmailAndPassword(binding.editEmail.text.toString(),binding.editPassword.text.toString()).addOnCompleteListener {
            if(it.isSuccessful){
                val user=auth.currentUser
                val uid=user?.uid
                val firestore=FirebaseFirestore.getInstance()
                val userCollection=firestore.collection("UserData")
                val userDocument = userCollection.document(uid?:"")


                val userData= hashMapOf(
                    "email" to binding.editEmail.text.toString(),
                    "number" to binding.editPhoneNumber.text.toString(),
                    "id" to binding.eidtId.text.toString(),
                    "nickname" to "닉네임 설정 필요",
                    "intro" to "자기소개가 아직 입력되지 않았습니다.",
                )

                userDocument.set(userData).addOnSuccessListener {
                    Toast.makeText(requireContext(),"회원가입 성공",Toast.LENGTH_SHORT).show()

                    imageUpload()
                }
                    .addOnFailureListener { e->

                        Toast.makeText(requireContext(),"회원가입 실패",Toast.LENGTH_SHORT).show()

                    }


            }
        }
    }
    private fun imageUpload() {

        val defaultImageUri: Uri = drawableToUri(requireContext(), R.drawable.profile_circle)
        Log.d("xxxx", "imageUpload uid : ${auth.currentUser!!.uid} uri : $defaultImageUri")
        val uploadTask = storage.reference.child("ProfileImg").child("${auth.currentUser!!.uid}")
            .putFile(defaultImageUri!!)
        uploadTask.addOnSuccessListener {
            // 파일 저장 성공 시 이벤트
            Log.d("xxxx", " img upload successful ")
        }.addOnFailureListener {
            // 파일 저장 실패 시 이벤트
            Log.d("xxxx", " img upload failure : $it ")
        }
    }


    fun drawableToUri(context: Context, drawableId: Int): Uri {
        val resources = context.resources
        val drawable = resources.getDrawable(drawableId, null)
        val bitmap = (drawable as BitmapDrawable).bitmap

        // 비트맵을 파일로 저장하고 해당 파일의 URI 생성
        val file = File(context.cacheDir, "temp_image.png")
        file.createNewFile()

        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)

        outputStream.flush()
        outputStream.close()

        return Uri.fromFile(file)
    }
}



