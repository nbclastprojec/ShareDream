package com.dreamteam.sharedream.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dreamteam.sharedream.model.Post
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel (): ViewModel() {
    private val db = Firebase.firestore
    private val _postResult = MutableLiveData<MutableList<Post>>()
    val postResult: LiveData<MutableList<Post>> get() = _postResult

    fun postDownload (){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                db.collection("Posts").orderBy("uploadDate",
                    Query.Direction.ASCENDING)
                    .get().addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val documentSnapshot = querySnapshot.documents
                        Log.d("xxxx", " testA[0].data : ${documentSnapshot[0].data} ")

                        val rcvList: MutableList<Post> = mutableListOf()

                        for (document in documentSnapshot) {
                            rcvList.add(
                                Post(
                                    document.data?.get("uid") as String,
                                    document.data?.get("title") as String,
                                    document.data?.get("price") as String,
                                    document.data?.get("category") as String,
                                    document.data?.get("address") as String,
                                    document.data?.get("deadline") as String,
                                    document.data?.get("desc") as String,
                                    document.data?.get("imgs") as List<String>,
                                    document.data?.get("uploadDate") as String,
                                )
                            )
                        }
                        Log.d("xxxx", " result : $rcvList")
                        _postResult.postValue(rcvList)
                    }
                }
            }catch (e:Exception){
            }
        }
    }
}