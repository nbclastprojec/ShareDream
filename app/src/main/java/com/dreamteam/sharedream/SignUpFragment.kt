package com.dreamteam.sharedream

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.dreamteam.sharedream.databinding.FragmentSignupBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay

class SignUpFragment : Fragment() {
    private lateinit var auth : FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment

        db = Firebase.database
        auth = Firebase.auth

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.agree1.setOnClickListener {
            auth?.signOut()
        }
        binding.agree2.setOnClickListener {
            Log.d("xxxx", " 약관 버튼 클릭")
            Log.d("xxxx", " 1. ${auth.currentUser?.uid}")
            val data = Firebase.database
            val testA = data.reference.child("test").child("")

            testA.setValue("Test2")
        }

        fun isEmailValid(email: String): Boolean {
            val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
            return email.matches(emailPattern.toRegex())
        }
        binding.btnSignup.setOnClickListener {
            Log.d("xxxx", " signUp btn clicked")
            val signUpEmail = binding.editEmail.text.toString()
            val signUpPassword = binding.editPassword.text.toString()
            val signUpNickname = binding.editNickname.text.toString()
            if (isEmailValid(signUpEmail)) {
                if (signUpNickname.length >= 2) {
                    if (signUpPassword.length > 5) {
                        createAccount(signUpEmail, signUpPassword)
                    } else {
                        Toast.makeText(requireContext(), "비밀번호는 6글자 이상이어야 합니다", Toast.LENGTH_SHORT)
                            .show()
                    }

                } else {
                    Toast.makeText(requireContext(), " 닉네임은 2글자 이상이어야 합니다.", Toast.LENGTH_SHORT)
                        .show()
                }

            } else {
                Toast.makeText(requireContext(), "이메일 형식이 올바르지 않습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createAccount(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful){
                        Log.d("xxxx", " tast.isSuccessful ")
                        val signUpEmail = binding.editEmail.text.toString()
                        val signUpPassword = binding.editPassword.text.toString()
                        val signUpNickname = binding.editNickname.text.toString()
                        val signUpNumber = binding.editPhoneNumber.text.toString()

                        val firebaseUser: FirebaseUser? = task.result?.user
                        if (firebaseUser != null){
                            Log.d("xxxx", " firebaseUser != null /  ${ task.result?.user?.uid}")
                            val userId = firebaseUser?.uid
                            val reference = db.reference.child("Users").child(userId!!)
                            val hashMap: HashMap<String, Any> = HashMap()
                            hashMap["id"] = userId
                            hashMap["nickname"] = signUpNickname
                            hashMap["email"] = signUpEmail
                            hashMap["pw"] = signUpPassword
                            hashMap["address"] = "선택 지역 없음"
                            hashMap["phone"] = signUpNumber
                            hashMap["intro"] = "소개말이 아직 작성되지 않았습니다."
                            reference.setValue(hashMap).addOnCompleteListener {
                                if (it.isSuccessful) {
                                    val intent = Intent(activity, MainActivity::class.java)
                                    startActivity(intent)
                                    requireActivity().finish()
                                    Log.d("xxxx", " referce addOnComplete Successful ")
                                } else {
                                    Toast.makeText(requireContext(), "계정 생성 실패", Toast.LENGTH_SHORT).show()
                                    Log.d("xxxx", "referce addOnComplete Fail")
                                }
                            }
                        }
                    }
                }
            Log.d("xxxx", "createAccount Successful")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}