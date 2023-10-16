package com.dreamteam.sharedream

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.dreamteam.sharedream.databinding.FragmentEditBinding
import com.dreamteam.sharedream.model.HomeData
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class EditFragment : Fragment() {
    private lateinit var binding: FragmentEditBinding
    val db = Firebase.firestore
    private lateinit var mContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditBinding.inflate(inflater, container, false)
        return (binding.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mContext = requireContext()
        FirebaseApp.initializeApp(mContext)

        binding.btnComplete.setOnClickListener {

            val title = binding.title.toString()
            val value = binding.value.toString()
            val category = binding.category.toString()
            val city = binding.city.toString()
            val during = binding.during.toString()
            val image = binding.camera.toString()
            val mainText = binding.mainText.toString()

            val edit = hashMapOf(
                "title" to title,
                "value" to value,
                "category" to category,
                "city" to city,
                "during" to during,
                "image" to image,
                "mainText" to mainText
            )
            Toast.makeText(context, "send text", Toast.LENGTH_SHORT).show()

            db.collection("Post")
                .add(edit)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                }

        }
    }
}