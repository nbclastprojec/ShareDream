//package com.dreamteam.sharedream.home.Edit
//
//import android.content.ContentValues.TAG
//import android.content.Context
//import android.os.Bundle
//import android.util.Log
//import androidx.fragment.app.Fragment
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Toast
//import androidx.activity.OnBackPressedCallback
//import androidx.fragment.app.viewModels
//import com.dreamteam.sharedream.databinding.FragmentEditBinding
//import com.google.firebase.FirebaseApp
//import com.google.firebase.firestore.ktx.firestore
//import com.google.firebase.ktx.Firebase
//
//@Suppress("DEPRECATION")
//class EditFragment : Fragment() {
//    private lateinit var binding: FragmentEditBinding
//    val db = Firebase.firestore
//    private lateinit var mContext: Context
//    private val homeViewModel: HomeViewModel by viewModels()
//
//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        mContext = context
//    }
//
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        binding = FragmentEditBinding.inflate(inflater, container, false)
//        return (binding.root)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        mContext = requireContext()
//        FirebaseApp.initializeApp(mContext)
//
//        binding.btnComplete.setOnClickListener {
//
//            val title = binding.title.text.toString()
//            val value = binding.value.text.toString().toInt()
//            val category = binding.category.text.toString()
//            val city = binding.city.text.toString()
//            val during = binding.during.text.toString()
//            val mainText = binding.mainText.text.toString()
//
//            val edit = hashMapOf(
//                "title" to title,
//                "value" to value,
//                "category" to category,
//                "city" to city,
//                "during" to during,
//                "mainText" to mainText
//            )
//            Toast.makeText(context, "send text", Toast.LENGTH_SHORT).show()
//
//            db.collection("Post")
//                .add(edit)
//                .addOnSuccessListener { documentReference ->
//                    Log.d(TAG, "nyh DocumentSnapshot added with ID: ${documentReference.id}")
//                }
//                .addOnFailureListener { e ->
//                    Log.w(TAG, "nyh Error adding document", e)
//                }
//        }
//
//        val callback = object : OnBackPressedCallback(true) {
//            override fun handleOnBackPressed() {
//                Log.d("edit onBack","nyh BackPressd In Fragment")
//                parentFragmentManager.beginTransaction()
//                    .remove(this@EditFragment)
//                    .commit()
//            }
//        }
//        requireActivity().onBackPressedDispatcher.addCallback(
//            viewLifecycleOwner, callback
//        )
//
//
//        binding.btnBack.setOnClickListener {
//            parentFragmentManager.beginTransaction()
//                .remove(this)
//                .commit()
//            homeViewModel.refreshRecyclerView()
//        }
//    }
//}