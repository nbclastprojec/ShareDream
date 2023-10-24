package com.dreamteam.sharedream.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dreamteam.sharedream.model.PostData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyPostFeedViewModel : ViewModel() {
    private val db = Firebase.firestore
    private val auth = Firebase.auth

    private val _postFeedResult = MutableLiveData<MutableList<PostData>>()
    val postFeedResult: LiveData<MutableList<PostData>> get() = _postFeedResult

    var currentPost = MutableLiveData<PostData>()
    fun postFeedDownload() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                db.collection("Post")
                    .whereEqualTo("uid", auth.currentUser!!.uid)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        if (!querySnapshot.isEmpty) {
                            val documentSnapshot = querySnapshot.documents
                            val rcvList : MutableList<PostData> = mutableListOf()
                            for (document in documentSnapshot) {
                                rcvList.add(
                                    PostData(
                                        document.data?.get("uid") as String,
                                        document.data?.get("title") as String,
                                        (document.data?.get("value") as Long).toInt(),
                                        document.data?.get("category") as String,
                                        " ",
                                        document.data?.get("mainText") as String,
                                        // todo img 리스트로 변경하여 여러 이미지 저장
                                        document.data?.get("image") as String,
                                        " ",
                                        listOf()
                                    )
                                )
                            }
                            _postFeedResult.postValue(rcvList)
                            Log.d("xxxx", "postFeedDownload: $rcvList")
                        }
                    }

            }catch (e: Exception){
                Log.d("xxxx", " postFeedDownload Failure = $e ")
            }
        }
    }

}