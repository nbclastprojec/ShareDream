package com.dreamteam.sharedream

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.dreamteam.sharedream.R
import com.dreamteam.sharedream.home.HomeFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
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
        val uid = user?.uid
        if(user != null){
            checkUserDocument(uid)


        }else{
            val loginFragment = LogInMainFragment()
            fragmentTransaction.replace(R.id.fragment_container, loginFragment)
            fragmentTransaction.commit()

        }


    }
    private fun checkUserDocument(uid: String?) {
        val db = FirebaseFirestore.getInstance()
        val userCollection = db.collection("UserData")
        val document = userCollection.document(uid!!)

        document.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val documentSnapshot = task.result
                if (documentSnapshot.exists()) {

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {

                    val inputUserData = InputUserData()
                    val transaction = this.supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.fragment_container, inputUserData)
                    transaction.commit()
                }
            } else {
                Log.e("FirestoreError", "Firestore 문서 확인 실패", task.exception)
            }
        }
    }


}