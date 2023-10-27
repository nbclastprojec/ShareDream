package com.dreamteam.sharedream.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dreamteam.sharedream.Util.Constants
import com.dreamteam.sharedream.model.Post
import com.dreamteam.sharedream.model.PostRcv
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
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
    private val _postFeedResult = MutableLiveData<MutableList<PostRcv>>()
    val postFeedResult: LiveData<MutableList<PostRcv>> get() = _postFeedResult

    // 게시글 목록 Rcv 클릭한 아이템 정보 받아오기
    var currentPost = MutableLiveData<PostRcv>()

    // 관심 목록 ( 좋아요 ) 추가 시 카운트 변동
    private val _likeUsersCount = MutableLiveData<MutableList<String>>()
    val likeUsersCount : LiveData<MutableList<String>> get() = _likeUsersCount

    // 게시글 디테일 페이지에서 수정 페이지로 이동 시 필요한 데이터
//    var currentPostEditDefault = MutableList<>

    // 게시글 목록 Rcv 클릭한 아이템 작성자 프로필 이미지 가져오기 / 마이 페이지 프로필 이미지와 같은 단일 프로필 이미지 로딩
    private val _currentProfileImg = MutableLiveData<Uri>()
    val currentProfileImg : LiveData<Uri> get() = _currentProfileImg

    // Home Frag 게시글 정보 LiveData
    private val _postResult = MutableLiveData<MutableList<PostRcv>>()
    val postResult: LiveData<MutableList<PostRcv>> get() = _postResult

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
                db.collection("Posts")
                    .orderBy("deadline",Query.Direction.ASCENDING)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        if (!querySnapshot.isEmpty) {
                            val postRcvList = mutableListOf<PostRcv>()
                            for ( document in querySnapshot.documents){
                                document.toObject<Post>()?.let { post ->
                                    PostRcvConvertUri(post,querySnapshot, postRcvList,_postResult)
                                }
                            }
                        }
                    }

            }catch (e: Exception){

            }
        }
    }

    private fun PostRcvConvertUri (post: Post, querySnapshot: QuerySnapshot, postRcvList:MutableList<PostRcv>,
                                   resultLiveData: MutableLiveData<MutableList<PostRcv>>) {
        val postImgUris: List<String> = post.imgs
        val postImgList: MutableList<Uri> = mutableListOf()

        val downloadTasks = mutableListOf<Task<Uri>>()
        for (uri in postImgUris) {
            val downloadTask = storage.reference.child("post").child(uri).downloadUrl
            downloadTasks.add(downloadTask)
        }

        Tasks.whenAllSuccess<Uri>(downloadTasks)
            .addOnSuccessListener { uriList ->
                postImgList.addAll(uriList)

                if (postImgUris.size == postImgList.size) {
                    val postRcv = PostRcv(
                        uid = post.uid,
                        title = post.title,
                        price = post.price,
                        category = post.category,
                        address = post.address,
                        deadline = post.deadline,
                        desc = post.desc,
                        imgs = postImgList,
                        nickname = post.nickname,
                        likeUsers = post.likeUsers,
                        token = post.token,
                        timestamp = post.timestamp,
                        state = post.state
                    )

                    postRcvList.add(postRcv)

                    if (postRcvList.size == querySnapshot.size()) {
                        resultLiveData.postValue(postRcvList)
                        Log.d("xxxx", " 설마 되겠어?")
                        Log.d(
                            "xxxx",
                            " 설마 있겠어 1: ${postRcvList[0].imgs}, 2: ${postRcvList[1].imgs}"
                        )
                    }
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
                            val postRcvList : MutableList<PostRcv> = mutableListOf()
                            for (document in documentSnapshot) {
                                document.toObject<Post>()?.let {
                                    PostRcvConvertUri(it,querySnapshot,postRcvList,_postFeedResult)
                                }
                            }
                            Log.d("xxxx", "postFeedDownload: $postRcvList")
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

    // 관심 목록 추가 or 제거
    fun addOrSubFavoritePost(uid: String, postPath: Timestamp){
        db.collection("Posts").whereEqualTo("timestamp", postPath)
            .get()
            .addOnSuccessListener {querySnapshot ->
                if (!querySnapshot.isEmpty) {

                    val documentSnap = querySnapshot.documents[0]
                    val likeList : MutableList<String> = mutableListOf()

                    likeList.addAll(documentSnap.data?.get("likeUsers") as List<String>)
                    Log.d("xxxx", " Before control LikeUsers List : $likeList")

                    if (likeList.contains(uid)){
                        //todo 이미 좋아요한 유저 이벤트 처리, 본인 게시물 좋아요 버튼 이벤트 처리.
                        Log.d("xxxx", "addFavoritePost: 이미 좋아요한 UID")
                        likeList.remove(uid)
                        documentSnap.reference.update("likeUsers",likeList)
                            .addOnSuccessListener {
                                Log.d("xxxx", " 관심 목록에서 제거 O ")
                            _likeUsersCount.postValue(likeList)
                            }
                            .addOnFailureListener {
                                Log.d("xxxx", "관심목록에서 제외하기 Failure $it")
                            }
                    } else {
                        likeList.add(uid)
                        documentSnap.reference.update("likeUsers",likeList).addOnSuccessListener {
                            Log.d("xxxx", " 좋아요 버튼 클릭 Successful, 좋아요 List에 UID 추가 " )
                            _likeUsersCount.postValue(likeList)
                            }
                            .addOnFailureListener {
                                Log.d("xxxx", " 좋아요 버튼 클릭 Failure 좋아요 List에 추가 X = $it ")
                            }
                        }
                    }
                }
    }
}