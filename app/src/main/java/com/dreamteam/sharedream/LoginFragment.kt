package com.dreamteam.sharedream

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.dreamteam.sharedream.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth



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


        binding.btnLogin.setOnClickListener {
            val homeFragment= HomeFragment()
            val transaction=requireActivity().supportFragmentManager.beginTransaction()
            val email = binding.editEmail.text.toString()
            val password= binding.editPassword.text.toString()
            if(check()){
                auth.signInWithEmailAndPassword(email,password).addOnCompleteListener {
                    if(it.isSuccessful){
                        transaction.replace(R.id.fragment_container,homeFragment)
                        transaction.addToBackStack(null)
                        transaction.commit()


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
            binding.editPassword.error = "최소 하나 이상의 특수문자를 입력해 주세요."
            return false
        } else {
            Toast.makeText(requireContext(), "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show()
            return true

        }
    }




    fun special(text:String):Boolean{
        val specialWord= setOf('!','@','#','$','%','^','&','*','(',')','_','-','<','>','=','+','?')//특수문자
        return text.any{it in specialWord//비밀번호 중 하나(any)라도 문자 안에 특수문자(speacialWord)가 포함되어있으면 true반환
        }
    }

}