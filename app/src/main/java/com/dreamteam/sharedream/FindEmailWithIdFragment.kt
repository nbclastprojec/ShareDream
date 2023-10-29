package com.dreamteam.sharedream

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.dreamteam.sharedream.databinding.FragmentFindEmailDialogBinding
import com.dreamteam.sharedream.databinding.FragmentFindEmailWithIdBinding
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot


class FindEmailWithIdFragment : Fragment() {
    lateinit var auth: FirebaseAuth
    lateinit var binding: FragmentFindEmailWithIdBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFindEmailWithIdBinding.inflate(inflater, container, false)

        binding.btnFindId.setOnClickListener {
            val email = binding.editEmail.text.toString()
            val number = binding.editNumber.text.toString()
            FindId(email, number)
        }
        return binding.root
    }




    private fun FindId(email: String, number: String) {
        val db = FirebaseFirestore.getInstance()
        val ud = db.collection("UserData")

        val query: Query = ud.whereEqualTo("email",email).whereEqualTo("number", number)

        query.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val result = task.result
                    if (result != null && !result.isEmpty) {
                        val userDocument = result.documents[0]
                        val id = userDocument.getString("id")
                        if (id != null) {
                            Log.d("FindEmailWithIdFragment", "Found id: $id")

                            val bundle = Bundle()
                            bundle.putString("userId", id)

                            val findEmailDialogFragment = FindEmailDialogFragment()
                            findEmailDialogFragment.arguments = bundle
                            findEmailDialogFragment.show(requireActivity().supportFragmentManager, "IdSend")
                        } else {
                            Log.d("FindEmailWithIdFragment", "일치하는 아이디가 없어요.")
                        }
                    } else {
                        Log.d("FindEmailWithIdFragment", "No matching user found.")
                        Toast.makeText(requireContext(),"일치하는 정보가 없어요",Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("FindEmailWithIdFragment", "Error: ${task.exception?.message}")
                }
            }
    }
    }
