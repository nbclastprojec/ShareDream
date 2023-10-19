package com.dreamteam.sharedream

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.dreamteam.sharedream.databinding.FragmentFindPasswordBinding
import com.google.firebase.auth.FirebaseAuth


class FindPasswordFragment : Fragment() {
    private lateinit var Auth: FirebaseAuth
    private lateinit var binding: FragmentFindPasswordBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Auth=FirebaseAuth.getInstance()
        binding=FragmentFindPasswordBinding.inflate(inflater,container,false)

        binding.btnLogin.setOnClickListener {
            val email = binding.editEmail.text.toString()
            if (check()) {


                Auth.sendPasswordResetEmail(email).addOnCompleteListener {
                    if(it.isSuccessful){
                        Toast.makeText(requireContext(),"새 비밀번호를 보냈습니다.", Toast.LENGTH_SHORT).show()
                        val loginFragment=LoginFragment()
                        val transaction=requireActivity().supportFragmentManager.beginTransaction()
                        transaction.replace(R.id.fragment_container,loginFragment)
                        transaction.addToBackStack(null)
                        transaction.commit()


                    }
                    else{
                        Toast.makeText(requireContext(),"이메일이 존재하지 않아요", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }


        return binding.root
    }

    private fun check():Boolean {
        val email = binding.editEmail.text.toString()
        val password = binding.editPassword.text.toString()
        if (email.isEmpty()) {
            binding.editEmail.error = "이메일을 입력해주세요."
            return false
        } else if (password.isEmpty()) {
            binding.editPassword.error = "비밀번호를 입력해주세요."
            return false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email)
                .matches()
        ) {   //android.util.Patterns.EMAIL_ADDRESS == 안드로이드에서 제공하는 기본 이메일 형식패턴
            binding.editEmail.error = "이메일 형식이 아닙니다."
            return false
        } else if (!special(password)) {
            binding.editPassword.error = "비밀번호에 특수문자를 하나 이상 입력해 주세요."
            return false
        } else {
            Toast.makeText(requireContext(), "이메일로 비밀번호를 보냈어요!.", Toast.LENGTH_SHORT).show()
            return true

        }
    }

    fun special(text:String):Boolean{
        val specialWord= setOf('!','@','#','$','%','^','&','*','(',')','_','-','<','>','=','+','?')//특수문자
        return text.any{it in specialWord//비밀번호 중 하나(any)라도 문자 안에 특수문자(speacialWord)가 포함되어있으면 true반환
        }

    }
}