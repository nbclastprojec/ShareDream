package com.dreamteam.sharedream

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment

import com.dreamteam.sharedream.R
import com.dreamteam.sharedream.databinding.FragmentFindIdBinding
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class FindIdFragment : Fragment() {
    lateinit var auth:FirebaseAuth
    lateinit var binding:FragmentFindIdBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentFindIdBinding.inflate(inflater,container,false)
        val db= FirebaseFirestore.getInstance()
        binding.btnLogin.setOnClickListener {
            val phoneNumber = binding.editEmail.text.toString()
            findEmailWithNumber(phoneNumber)
                .addOnSuccessListener(OnSuccessListener { email ->
                    // 이메일을 찾았을 때의 처리
                    if (email != null) {
                        showEmailToast(email)
                    } else {

                    }
                })
                .addOnFailureListener(OnFailureListener { exception ->
                    // 오류 발생 시의 처리
                    showEmailNotFoundToast()
                })
        }




        return binding.root
    }

    fun findEmailWithNumber(Number:String): Task<String> {
        val number=binding.editEmail.text.toString()
        val db= FirebaseFirestore.getInstance()
        val ud=db.collection("UserData")
        val query=ud.whereEqualTo("number",number)


        return query.get()
            .continueWith { task ->
                if (task.isSuccessful && !task.result?.isEmpty!!) {
                    val user = task.result?.documents?.get(0)?.data
                    val email = user?.get("email") as? String
                    return@continueWith email ?: "일치하는 사용자를 찾을 수 없습니다."
                } else {
                    return@continueWith "오류 발생"
                }
            }
    }

    fun showEmailToast(email: String) {
        Toast.makeText(context, "찾은 이메일: $email", Toast.LENGTH_SHORT).show()
    }

    fun showEmailNotFoundToast() {
        Toast.makeText(context, "일치하는 사용자를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
    }

}