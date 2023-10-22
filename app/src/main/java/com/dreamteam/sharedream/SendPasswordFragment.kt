package com.dreamteam.sharedream

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.dreamteam.sharedream.databinding.FragmentSendPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class SendPasswordFragment : Fragment() {
    private lateinit var binding:FragmentSendPasswordBinding
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth= FirebaseAuth.getInstance()
        binding= FragmentSendPasswordBinding.inflate(inflater,container,false)

        binding.btnLogin.setOnClickListener {
            val email = binding.editEmail.text.toString()
            if (check()) {


                auth.sendPasswordResetEmail(email).addOnCompleteListener {
                    if(it.isSuccessful){

                            val emailFragment=EmailPasswordDialogFragment()
                        emailFragment.show(requireActivity().supportFragmentManager,"Agree1")



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

        if (email.isEmpty()) {
            binding.editEmail.error = "이메일을 입력해주세요."
            return false
        }  else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email)
                .matches()
        ) {   //android.util.Patterns.EMAIL_ADDRESS == 안드로이드에서 제공하는 기본 이메일 형식패턴
            binding.editEmail.error = "이메일 형식이 아닙니다."
            return false
        } else {

            return true

        }
    }


}