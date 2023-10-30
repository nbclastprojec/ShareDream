package com.dreamteam.sharedream

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.dreamteam.sharedream.databinding.FragmentNicknameCheckDailalogBinding

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class NicknameCheckDailogFragment : DialogFragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var fireStore:FirebaseFirestore
    private var _binding: FragmentNicknameCheckDailalogBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding=FragmentNicknameCheckDailalogBinding.inflate(inflater,container,false)
        auth= FirebaseAuth.getInstance()
        fireStore= FirebaseFirestore.getInstance()
        val userUid=auth.currentUser?.uid.toString()

        binding.nickNameCreateBtn.setOnClickListener {
            if (userUid != null) {
                val collection = fireStore.collection("UserData")
                val document = collection.document(userUid)
                val nickNameValue = binding.editNickName.text.toString()
                val userData = hashMapOf("nickname" to nickNameValue)

                document.update(userData as Map<String, Any>)
                    .addOnSuccessListener {
                        dismiss()
                    }
                    .addOnFailureListener { e ->

                    }
            }
        }




        return binding.root
    }
}