package com.dreamteam.sharedream

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.dreamteam.sharedream.databinding.FragmentLoginBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot


class LoginFragment : Fragment() {
    private lateinit var auth:FirebaseAuth
    private lateinit var binding:FragmentLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
            }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        auth= FirebaseAuth.getInstance()

        binding.findPassword.setOnClickListener {
            val sendEmailFragment=SendPasswordFragment()
            val transaction=requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container,sendEmailFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
        binding.btnBack.setOnClickListener {
            val mainLogInMainFragment=LogInMainFragment()
            val transaction=requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container,mainLogInMainFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        binding.tvFindID.setOnClickListener {
            val findEmailFragment=FindEmailWithIdFragment()
            val transaction=requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container,findEmailFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }


        binding.btnLogin.setOnClickListener {
            val id = binding.editId.text.toString()
            val password = binding.editPassword.text.toString()

            if (check()) {
                findEmail(id)
                    .addOnCompleteListener { Task ->
                        if (Task.isSuccessful) {
                            val email = Task.result
                            if (email != "일치하는 사용자를 찾을 수 없습니다.") {
                                Log.d("checkEmail","Found documentEmail: $email")
                                auth.signInWithEmailAndPassword(email, password)
                                    .addOnCompleteListener { loginTask ->
                                        if (loginTask.isSuccessful) {
                                            val intent = Intent(activity, MainActivity::class.java)
                                            startActivity(intent)
                                        } else {
                                            Toast.makeText(
                                                requireContext(),
                                                "아이디 및 비밀번호를 잘못 입력하셨습니다.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "일치하는 사용자를 찾을 수 없습니다.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "오류 발생",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }
        return binding.root
    }


    private fun check():Boolean {
        val email = binding.editId.text.toString()
        val password = binding.editPassword.text.toString()
        if (email.isEmpty()) {
            binding.editId.error = "아이디를 입력해주세요."
            return false
        } else if (password.isEmpty()) {
            binding.editPassword.error = "비밀번호를 입력해주세요."
            return false
        } else if (!special(password)) {
            binding.editPassword.error = "최소 하나 이상의 특수문자를 입력해 주세요."
            return false
        } else {

            return true

        }
    }




    fun special(text:String):Boolean{
        val specialWord= setOf('!','@','#','$','%','^','&','*','(',')','_','-','<','>','=','+','?')//특수문자
        return text.any{it in specialWord//비밀번호 중 하나(any)라도 문자 안에 특수문자(speacialWord)가 포함되어있으면 true반환
        }
    }

    fun findEmail(id: String): Task<String> {
        val db = FirebaseFirestore.getInstance()
        val ud = db.collection("UserData")

        val query: Query = ud.whereEqualTo("id", id)

        return query.get().continueWith { task ->
            if (task.isSuccessful) {
                val result: QuerySnapshot = task.result!!
                if (!result.isEmpty) {
                    val userDocument = result.documents[0]
                    val email = userDocument.getString("email")
                    if (email != null) {
                        return@continueWith email
                    } else {
                        return@continueWith "사용자 정보 인식 실패"
                    }
                } else {
                    return@continueWith "일치하는 사용자를 찾을 수 없습니다."
                }
            } else {
                return@continueWith "오류 발생"
            }
        }
    }

}