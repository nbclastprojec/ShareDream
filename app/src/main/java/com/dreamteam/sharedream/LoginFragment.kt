package com.dreamteam.sharedream

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.dreamteam.sharedream.databinding.FragmentLoginBinding
import com.dreamteam.sharedream.databinding.FragmentLoginMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment

        binding.btnLogin.setOnClickListener {
            val email: String = binding.editEmail.text.toString()
            val password : String = binding.editPassword.text.toString()

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
                Toast.makeText(requireContext(),"이메일과 비밀번호는 필수 입력사항 입니다",Toast.LENGTH_SHORT).show()
            } else {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            startActivity(Intent(requireActivity(),MainActivity::class.java))
                            requireActivity().finish()
                        } else {
                            Toast.makeText(requireActivity(),"입력하신 정보와 일치하는 회원 정보가 없습니다",Toast.LENGTH_SHORT).show()
                            binding.editPassword.text.clear()
                        }
                    }
            }
        }

        binding.btnSignUp.setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.login_frame_layout,SignUpFragment()).addToBackStack(null).commit()
        }


        return binding.root
    }
}