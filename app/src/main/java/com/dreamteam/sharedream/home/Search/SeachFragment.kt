package com.dreamteam.sharedream.home.Search

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.dreamteam.sharedream.databinding.FragmentSeachBinding
import com.dreamteam.sharedream.model.PostData
import com.google.firebase.firestore.FirebaseFirestore


class SeachFragment : Fragment() {

    private lateinit var searchadapter: SeachAdapter
    private lateinit var binding: FragmentSeachBinding
    private lateinit var mContext: Context

    private val firestore = FirebaseFirestore.getInstance()


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSeachBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //어댑터 초기화!해야됨!

        searchadapter = SeachAdapter(mContext)
        val searchEdit = binding.editTextText

        binding.searchRecycler.layoutManager =
            LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
        binding.searchRecycler.adapter = searchadapter

        // 검색어 필수 메서드
        searchEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                //검색어 입력 후 searchTitle 실행
                val query = p0.toString()
                if (query.isNotEmpty()) {
                    searchTitle(query)
                    Log.d("nyh", "afterTextChanged: $p0 // $query ")
                } else {
                    Log.d("nyh", "afterTextChanged else: $p0 // $query")
                }
            }
        })
    }

    private fun searchTitle(title: String) {
        val postCollection = firestore.collection("Post")
        //query 객체 만들고 get으로 가져오기
        postCollection.whereEqualTo("title", title)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val searchList = mutableListOf<PostData>()

                //for문으로 data를 searchList에 넣어주고 adapter로 전달하기

                for (i in querySnapshot.documents) {
                    val data = i.toObject(PostData::class.java)
                    data?.let {
                        searchList.add(it)
                        Log.d("nyh", "searchTitle: $searchList")
                    }
                }
                searchadapter.setData(searchList)
                val startPosition = searchList.size
                searchadapter.notifyItemRangeInserted(startPosition, searchList.size)
            }.addOnFailureListener { exception ->
                Log.d("nyh", "searchTitle fail : $exception")
            }
    }

}