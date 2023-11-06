package com.dreamteam.sharedream

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.dreamteam.sharedream.Util.Constants
import com.dreamteam.sharedream.databinding.FragmentNicknameCheckDailalogBinding
import com.dreamteam.sharedream.model.UserData

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject


class NicknameCheckDailogFragment : DialogFragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var fireStore: FirebaseFirestore
    private var _binding: FragmentNicknameCheckDailalogBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNicknameCheckDailalogBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        fireStore = FirebaseFirestore.getInstance()
        val userUid = auth.currentUser?.uid.toString()

        binding.nickNameCreateBtn.setOnClickListener {
            if (userUid != null) {
                val collection = fireStore.collection("UserData")
                val document = collection.document(userUid)
                val nickNameValue = binding.editNickName.text.toString()
                val userData = hashMapOf("nickname" to nickNameValue)

                document.update(userData as Map<String, Any>)
                    .addOnSuccessListener {
                        collection.document("${auth.currentUser!!.uid}")
                            .get()
                            .addOnSuccessListener {
                                Constants.currentUserInfo = it.toObject<UserData>()
                            }
                        dismiss()
                    }
                    .addOnFailureListener { e ->

                    }
            }
        }




        return binding.root
    }
}