package com.dreamteam.sharedream

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.dreamteam.sharedream.databinding.FragmentInputUserDataBinding
import com.google.android.gms.auth.api.Auth
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging

class InputUserData : Fragment() {
    private lateinit var binding: FragmentInputUserDataBinding
    private lateinit var auth: FirebaseAuth
    var token = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInputUserDataBinding.inflate(inflater, container, false)
        binding.btnSignup.setOnClickListener {
            if (check()) {
                val nickname = binding.editNickName.text.toString()
                val number = binding.editNumber.text.toString()
                getInformation(number, nickname, token)
                Log.d("nyh", "onCreateView: $token")
            }
        }
        binding.agree1.setOnClickListener {
            val agreeDialog = AgreeFragment()
            agreeDialog.show(requireActivity().supportFragmentManager, "Agree1")
        }
        binding.agree2.setOnClickListener {
            val agreeDialog = PersonalAgree()
            agreeDialog.show(requireActivity().supportFragmentManager, "Agree2")
        }

        return binding.root
    }

    fun getInformation(number: String, nickname: String, token: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {

                Log.d("nyh", "getInformation:token $token")

                if (currentUser != null) {
                    val token = task.result
                    val uid = currentUser.uid
                    val email = currentUser.email
                    val id = email

                    val userData = hashMapOf(
                        "email" to email,
                        "number" to number,
                        "nickname" to nickname,
                        "id" to id,
                        "token" to token

                    )

                    val db = FirebaseFirestore.getInstance()
                    val userCollection = db.collection("UserData")
                    val document = userCollection.document(uid)

                    document.set(userData)
                        .addOnSuccessListener {
                            val intent = Intent(requireContext(), MainActivity::class.java)
                            startActivity(intent)
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(requireContext(), "데이터 저장 실패: $e", Toast.LENGTH_SHORT)
                                .show()
                        }
                } else {
                    Toast.makeText(requireContext(), "Input Error", Toast.LENGTH_SHORT).show()
                }
            }
        }.addOnFailureListener { e ->
            Log.d("nyh", "getInformation: fail $e ")
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun check(): Boolean {
        val checkbox = binding.checkBox
        val nickname = binding.nickName.text.toString()
        val number = binding.editNumber.text.toString()

        if (!phone(number)) {
            binding.editNumber.error = "핸드폰 번호는 숫자로만 이루어져야 합니다."
            return false


        } else if (!nickname.matches(Regex("^[가-힣]*$"))) {
            binding.nickName.error = "닉네임은 한글로만 이뤄져야해요. "
            return false
        } else if (!checkbox.isChecked) {
            Toast.makeText(requireContext(), "약관동의를 체크해주세요.", Toast.LENGTH_SHORT).show()
            return false
        } else {
            return true
        }


    }

    fun phone(text: String): Boolean {
        return text.all { it.isDigit() }
    }


}