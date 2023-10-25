package com.dreamteam.sharedream.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dreamteam.sharedream.Util.Constants
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

    // 게시글 목록 Rcv 클릭한 아이템 정보 받아오기
    var currentPost = MutableLiveData<Post>()
    // 게시글 목록 Rcv 클릭한 아이템 작성자 프로필 이미지 가져오기 / 마이 페이지 프로필 이미지와 같은 단일 프로필 이미지 로딩
    private val _currentProfileImg = MutableLiveData<Uri>()
    val currentProfileImg : LiveData<Uri> get() = _currentProfileImg

    // Home Frag 게시글 정보 LiveData
    private val _postResult = MutableLiveData<MutableList<Post>>()
    val postResult: LiveData<MutableList<Post>> get() = _postResult

    // todo Home Frag 게시글 대표 이미지 별도로 추가할 수 있도록 수정할 예정 - 이미지를 띄우는 속도가 너무 느림.
    private val _postUriResult = MutableLiveData<MutableList<Uri>>()
    val postUriResult : LiveData<MutableList<Uri>> get() = _postUriResult


    // 게시글 디테일 아이템 사진들 불러오기
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
                                //변경 실패 코드 todo Home Frag 게시글 대표 이미지 별도로 추가할 수 있도록 수정할 예정 2
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
//                                                    Log.d("xxxx", "postDownload RcvList: ${postRcvList}")
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

    // 내가 쓴 글 목록 받아오기 -  whereEqualTo, orderBy
    fun postFeedDownload() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                db.collection("Posts")
                    .whereEqualTo("uid", Constants.currentUserUid)
                    .orderBy("imgs",Query.Direction.DESCENDING)
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
                    .addOnFailureListener {
                        Log.d("xxxx", "postFeedDownload Failure : $it ")
                    }

            }catch (e: Exception){
                Log.d("xxxx", " postFeedDownload Failure = $e ")
            }
        }
    }

    // 단일 프로필 이미지 불러오기
    fun getCurrentProfileImg(uid : String){
        storage.reference.child("ProfileImg").child("$uid").downloadUrl
            .addOnSuccessListener {
                _currentProfileImg.value = it
            }
    }

    // 좋아요 버튼 클릭 이벤트
    fun addFavoritePost(uid: String,postPath: String){
        db.collection("Posts").whereArrayContains("imgs", postPath)
            .get()
            .addOnSuccessListener {querySnapshot ->
                Log.d("xxxx", "addFavoritePost: 첫번째는 성공 $querySnapshot")
                if (!querySnapshot.isEmpty) {

                    val documentSnap = querySnapshot.documents[0]
                    Log.d("xxxx", "addFavoritePost documentSanp : $documentSnap")
                    val likeList : MutableList<String> = mutableListOf()

                    likeList.addAll(documentSnap.data?.get("likeUsers") as List<String>)
                    Log.d("xxxx", " Before add LikeUsers List : $likeList")

                    if (likeList.contains(uid)){
                        //todo 이미 좋아요한 유저 이벤트 처리, 본인 게시물 좋아요 버튼 이벤트 처리.
                        Log.d("xxxx", "addFavoritePost: 이미 좋아요한 UID")
                    } else {
                        likeList.add(uid)
                        documentSnap.reference.update("likeUsers",likeList).addOnSuccessListener {
                                Log.d("xxxx", " 좋아요 버튼 클릭 Successful, 좋아요 List에 UID 추가 " )
                            }
                            .addOnFailureListener {
                                Log.d("xxxx", " 좋아요 버튼 클릭 Failure 좋아요 List에 추가 X = $it ")
                            }
                        }
                    }
                }

    }
}