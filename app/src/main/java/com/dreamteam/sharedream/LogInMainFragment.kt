package com.dreamteam.sharedream

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.dreamteam.sharedream.databinding.FragmentLoginMainBinding
import com.dreamteam.sharedream.home.HomeFragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlin.math.log


class LogInMainFragment : Fragment() {
    private lateinit var binding:FragmentLoginMainBinding
    var auth:FirebaseAuth?=null
    private lateinit var googleSignInAccount: GoogleSignInClient
    val Google_Request_Code=99



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth=FirebaseAuth.getInstance()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentLoginMainBinding.inflate(inflater,container,false)

            val gso=GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            googleSignInAccount= GoogleSignIn.getClient(requireContext(),gso)


        binding.btnLogin.setOnClickListener {
            val loginFragment=LoginFragment()
            val transaction=requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container,loginFragment)
            transaction.addToBackStack(null)
            transaction.commit()

        }

        binding.btnGoogleLogin.setOnClickListener {
            signIn()
        }

        binding.tvSignup.setOnClickListener {
            val signUpFragment=SignUpFragment()
            val transaction=requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container,signUpFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }











        return binding.root
    }
    private fun signIn(){
        val signinIntent=googleSignInAccount.signInIntent
        startActivityForResult(signinIntent,Google_Request_Code)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==Google_Request_Code) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try{
                val account=task.getResult(ApiException::class.java)
                Log.d("googleLogin","googleWithFireBaseLogin:"+account.id)
                googleLogIn(account.idToken!!)

            }
            catch (e:ApiException){
                Log.w("googleSignFailed","Google sign in failed",e)
                Toast.makeText(requireContext(),"로그인실패",Toast.LENGTH_SHORT).show()
            }
        }

    }
    private fun googleLogIn(idToken:String) {
        val credential=GoogleAuthProvider.getCredential(idToken,null)
        auth?.signInWithCredential(credential)?.addOnCompleteListener(requireActivity()) { task ->
        if (task.isSuccessful){
            Log.d("google1","로그인성공")
            val user= auth!!.currentUser
            loginIntent()

        }
            else{
        Log.w("GloginFailed","signInWithCredential:failure",task.exception)
        }

        }

    }

    private fun loginIntent(){
        val loginFragment=LoginFragment()
        val transaction=requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, loginFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}