package com.dreamteam.sharedream

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.dreamteam.sharedream.databinding.FragmentLoginMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider


class LogInMainFragment : Fragment() {
    private lateinit var binding:FragmentLoginMainBinding
    private lateinit var auth:FirebaseAuth
    private lateinit var googleSignInAccount: GoogleSignInClient



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth=FirebaseAuth.getInstance()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentLoginMainBinding.inflate(inflater,container,false)


        binding.btnLogin.setOnClickListener {
            val loginFragment=LoginFragment()
            val transaction=requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container,loginFragment)
            transaction.addToBackStack(null)
            transaction.commit()

        }

            val gso=GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            googleSignInAccount= GoogleSignIn.getClient(requireContext(),gso)

        binding.btnGoogleLogin.setOnClickListener {
            val intent=googleSignInAccount.signInIntent
            startActivityForResult(intent,200)
            val homeFragment=HomeFragment()
            val transaction=requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container,homeFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }









        return binding.root
    }

    override fun onActivityResult(requestCode:Int,resultCode:Int, data: Intent?){
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==200){
            val task=GoogleSignIn.getSignedInAccountFromIntent(data)
            val account= task.getResult(ApiException::class.java)
            val credential=
                GoogleAuthProvider.getCredential(account.idToken,null)
            FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener {task ->
                if (task.isSuccessful){
                    Toast.makeText(requireContext(),"구글 로그인 성공",Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(requireContext(),task.exception?.message,Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


}