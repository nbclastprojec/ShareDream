package com.dreamteam.sharedream

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dreamteam.sharedream.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LogInActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)





        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()



        auth= Firebase.auth
        val user=auth.currentUser
        if(user != null){
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()

        }else{
            val loginFragment = LogInMainFragment()
            fragmentTransaction.replace(R.id.fragment_container, loginFragment)
            fragmentTransaction.commit()

        }
    }
}