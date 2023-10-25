package com.dreamteam.sharedream.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dreamteam.sharedream.model.Post
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyPostFeedViewModel : ViewModel() {
    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private val storage = Firebase.storage

    // 내가 쓴 글 목록 LiveData todo 추후에 ViewType 나누는 걸로 수정할 예정.
    private val _postFeedResult = MutableLiveData<MutableList<Post>>()
    val postFeedResult: LiveData<MutableList<Post>> get() = _postFeedResult

    // 내가 쓴 글 목록 Rcv 클릭한 아이템 정보 받아오기
    var currentPost = MutableLiveData<Post>()

    // 디테일 정보 LiveData
    private val _postDetailResult = MutableLiveData<MutableList<Post>>()
    val postDetail : LiveData<MutableList<Post>> get() = _postDetailResult

    // Home Frag 게시글 정보 LiveData
    private val _postResult = MutableLiveData<MutableList<Post>>()
    val postResult: LiveData<MutableList<Post>> get() = _postResult
    private val _postUriResult = MutableLiveData<MutableList<Uri>>()
    val postUriResult : LiveData<MutableList<Uri>> get() = _postUriResult


    fun testA () {
        val uris = mutableListOf<Uri>()
        // 빈 리스트로 초기화
        _postUriResult.value = uris
        Log.d("xxxx", " 초기화 ? : ${uris} ")
        currentPost.value?.let { post ->
           for ( index in post.imgs.indices){
                   storage.reference.child("post").child("${post.imgs[index]}").downloadUrl
                       .addOnSuccessListener {
                           uris.add(it)

                           _postUriResult.postValue(uris)
                           Log.d("xxxx", "testA Successful : ${_postUriResult.value} ")
                       }
                       .addOnFailureListener {
                           Log.d("xxxx", "testA Failure : $it ")
                       }
           }

            Log.d("xxxx", "postUriResult: ${_postUriResult.value} ")
        }
    }
    // Home Frag 게시글 정보 받아오기
    fun postDownload(){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val postRcvList : MutableList<Post> = mutableListOf()
                db.collection("Posts")
                    .orderBy("deadline",Query.Direction.ASCENDING)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        if (!querySnapshot.isEmpty) {
                            val postDocumentSnapshot = querySnapshot.documents

                            for ( document in postDocumentSnapshot){
                                //변경 실패 코드
//                                document.toObject<Post>()?.let {post->
//                                    val imgUris = mutableListOf<Uri>()
//                                    for (index in post.imgs.indices) {
//                                        storage.reference.child("post").child(post.imgs[index])
//                                            .downloadUrl.addOnSuccessListener { uri ->
//                                                imgUris.add(uri)
//                                                Log.d("xxxx", "postDownload imgUris$index : ${imgUris[index]} ")
//
//                                                if (imgUris.size == post.imgs.size) {
//                                                    val postRcv = PostRcv(
//                                                        uid = post.uid,
//                                                        title = post.title,
//                                                        price = post.price,
//                                                        category = post.category,
//                                                        address = post.address,
//                                                        deadline = post.deadline,
//                                                        desc = post.desc,
//                                                        imgs =  imgUris,
//                                                        nickname = post.nickname,
//                                                        likeUsers = post.likeUsers
//                                                    )
//                                                    postRcvList.add(postRcv)
////                                                    Log.d("xxxx", "postDownload RcvList: ${postRcvList}")
//                                                }
//                                            }
//                                    }
//                                }

                                // 기존 코드
                                document.toObject<Post>()?.let {post ->
                                    val postImgList : MutableList<Uri> = mutableListOf()
                                    for (i in post.imgs.indices){
                                    storage.reference.child("post").child("${post.imgs[i]}")
                                        .downloadUrl.addOnSuccessListener {
                                            postImgList.add(it)
                                        }.addOnFailureListener {
                                        }

                                    Log.d("xxxx", " postRcvList.add  ")
                                    }
                                    postRcvList.add(post)
                                }
                            }

                            // livedata
                            _postResult.postValue(postRcvList)
                            Log.d("xxxx", "postDetailDownload : $postRcvList")
                        }
                    }

            }catch (e: Exception){

            }
        }
    }

    // 내가 쓴 글 목록 받아오기 - CurrentUser.uid, whereEqualTo
    fun postFeedDownload() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                db.collection("Posts")
                    .whereEqualTo("uid", auth.currentUser!!.uid)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        if (!querySnapshot.isEmpty) {
                            val documentSnapshot = querySnapshot.documents
                            val rcvList : MutableList<Post> = mutableListOf()
                            for (document in documentSnapshot) {
                                document.toObject<Post>()?.let {
                                    rcvList.add(it)
                                }
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