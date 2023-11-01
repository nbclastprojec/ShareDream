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
import com.dreamteam.sharedream.model.UserData
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MyPostFeedViewModel : ViewModel() {
    private val db = Firebase.firestore
    private val storage = Firebase.storage

    companion object {
        const val USER_DATA = "UserData"
        const val POST_BARTER_DATA = "Posts_Barter"
        const val POST_RENTAL_DATA = "Posts_Rental"
        const val USER_PROFILE_IMAGE = "ProfileImg"
        const val POST_IMAGES = "post"
    }

    // 내가 쓴 글 목록 LiveData todo 추후에 ViewType 나누는 걸로 수정할 예정.
    private val _postFeedResult = MutableLiveData<MutableList<PostRcv>>()
    val postFeedResult: LiveData<MutableList<PostRcv>> get() = _postFeedResult

    // 관심 목록 ( 좋아요 ) 추가 시 카운트 변동
    private val _likeUsersCount = MutableLiveData<MutableList<String>>()
    val likeUsersCount : LiveData<MutableList<String>> get() = _likeUsersCount

    // 게시글 목록 Rcv 클릭한 아이템 작성자 프로필 이미지 가져오기 / 마이 페이지 프로필 이미지와 같은 단일 프로필 이미지 로딩
    private val _currentProfileImg = MutableLiveData<Uri>()
    val currentProfileImg : LiveData<Uri> get() = _currentProfileImg

    // Home Frag 게시글 정보 LiveData
    private val _postResult = MutableLiveData<MutableList<PostRcv>>()
    val postResult: LiveData<MutableList<PostRcv>> get() = _postResult

    // myPage 사용자 정보
    private val _myPageResult = MutableLiveData<UserData>()
    val myPageResult : MutableLiveData<UserData> get() = _myPageResult

    // userPage 다른 유저 사용자 정보
    private val _userPageResult = MutableLiveData<UserData>()
    val userPageResult : MutableLiveData<UserData> get() = _userPageResult

    // 게시글 목록 Rcv 클릭한 아이템 정보 받아오기
    private val _currentPost = MutableLiveData<PostRcv>()
    val currentPost : MutableLiveData<PostRcv> get() = _currentPost

    // 수정 페이지 이동 시 기존에 작성 되어 있던 정보 불러오기
    var currentPostToEditPage = MutableLiveData<PostRcv>()

    // 포스트 수정된 DB 업로드
    // 선택한 이미지 Storage에 업로드
    fun uploadEditPost(uris : MutableList<Any>, post : Post) {
        viewModelScope.launch(Dispatchers.IO) {
        val imgs : MutableList<String> = mutableListOf()
        fun getTime(): String {
            val currentDateTime = Calendar.getInstance().time
            val dateFormat =
                SimpleDateFormat("yyyy.MM.dd HH:mm:ss.SSS", Locale.KOREA).format(currentDateTime)

            return dateFormat
        }
        val time = getTime()
        uris?.let { uriAndUrl ->
            Log.d("xxxx", "imageUpload 1. uris : $uris")
                try {
                    var count : Int = 0
                    for (item in uriAndUrl){ // index in uriAndUrl로 하면 작동이 안됨.
                        count++
                        val fileName = "${time}_$count"
                        imgs.add(fileName)
                        when (item) {
                            is Uri -> {
                                storage.reference.child("post").child("${time}_$count").putFile(item)
                                    .addOnSuccessListener {
                                        Log.d("xxxx", " Uri Succesful : $it")
                                    }
                                    .addOnFailureListener {
                                        Log.d("xxxx", " Uri Failure : $it")
                                    }
                            }
                            is URL -> {
                                val connection = item.openConnection() as HttpURLConnection
                                connection.connect()
                                val inputStream = connection.inputStream
                                storage.reference.child("post").child("${time}_$count").putStream(inputStream)
                                    .addOnSuccessListener{
                                        Log.d("xxxx", " URL Successful : $it ")
                                    }
                                    .addOnFailureListener{
                                        Log.d("xxxx", " URL Failure : $it ")
                                    }
                            }
                            else -> {}
                        }
                    }
                }catch (exception : StorageException){
                    val errorCode = exception.errorCode
                    Log.d("xxxx", " 파일 업로드 실패 errorCode: ${errorCode}")
                }

            // timestamp 기준으로 수정하는 게시글 data를 가져와 변경된 게시글로 수정
            post.imgs = imgs
            db.collection("Posts").whereEqualTo("timestamp",post.timestamp).get()
                .addOnSuccessListener {querySnapshot ->
                    Log.d("xxxx", "uploadEditPost: 해당 게시글 $querySnapshot")
                    if (!querySnapshot.isEmpty){
                        querySnapshot.documents[0].reference.set(post)
                            .addOnSuccessListener {
                                Log.d("xxxx", "imageUpload: 게시글 수정 완료 ")
                            }
                            .addOnFailureListener {
                                Log.d("xxxx", "uploadEditPost: 게시글 수정 실패 $it")
                            }
                    }
                }
                .addOnFailureListener {
                    Log.d("xxxx", "uploadEditPost: 초장부터 실패 $it")
                }
        }
        }
    }

    // 게시물 디테일 정보 받아오기
    fun setCurrentPost(postRcv : PostRcv){
        _currentPost.value = postRcv
    }

    // Home Frag 게시글 정보 받아오기
    fun downloadHomePostRcv(){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                db.collection("Posts")
                    .orderBy("timestamp",Query.Direction.DESCENDING)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        Log.d("please","$querySnapshot")
                        if (!querySnapshot.isEmpty) {
                            val postRcvList = mutableListOf<PostRcv>()
                            for ( document in querySnapshot.documents){
                                document.toObject<Post>()?.let { post ->
                                    convertPostToPostRcv(post,querySnapshot, postRcvList,_postResult)
                                    Log.d("postpost","$post")
                                }
                            }
                        }
                    }.addOnFailureListener {
                        Log.d("pleaseFail","$it")
                    }

            }catch (e: Exception){

            }
        }
    }

    // Model 형식 Post  -> PostRcv 형식으로 변환
    private fun convertPostToPostRcv (post: Post, querySnapshot: QuerySnapshot, postRcvList:MutableList<PostRcv>,
                                      resultLiveData: MutableLiveData<MutableList<PostRcv>>) {
        val postImgUris: List<String> = post.imgs
        val postImgList: MutableList<Uri> = mutableListOf()

        val downloadTasks = mutableListOf<Task<Uri>>()
        for (uri in postImgUris) {
            val downloadTask = storage.reference.child("post").child(uri).downloadUrl
            downloadTasks.add(downloadTask)

        }

        // 이미지를 모두 받아온 뒤 한번에 PostRcv의 List<Uri> 에 담아준다 ->
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
                        state = post.state,
                        documentId=post.documentId,
                        endDate = post.endTime
                    )

                    var inserted = false
                    for ( index in postRcvList.indices){
                        if (postRcv.timestamp > postRcvList[index].timestamp){
                            postRcvList.add(index, postRcv)
                            inserted = true
                            break
                        }
                    }
                    if (!inserted){
                        postRcvList.add(postRcv)
                    }


                    if (postRcvList.size == querySnapshot.size()) {
                        resultLiveData.postValue(postRcvList)
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
                    .orderBy("timestamp",Query.Direction.DESCENDING)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        if (!querySnapshot.isEmpty) {
                            val documentSnapshot = querySnapshot.documents
                            val postRcvList : MutableList<PostRcv> = mutableListOf()
                            for (document in documentSnapshot) {
                                document.toObject<Post>()?.let {
                                    convertPostToPostRcv(it,querySnapshot,postRcvList,_postFeedResult)
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
    fun downloadCurrentProfileImg(uid : String){
        storage.reference.child("ProfileImg").child("$uid").downloadUrl
            .addOnSuccessListener {
                _currentProfileImg.value = it
            }
    }

    // 유저 데이터 가져오기
    fun downloadUserInfo(uid : String,resultLiveData : MutableLiveData<UserData>){
        db.collection("UserData").document(uid).get()
            .addOnSuccessListener {
                resultLiveData.postValue(it.toObject<UserData>())
            }
    }

    // 유저 데이터 수정하기
    fun uploadEditUserInfo(nickname : String,intro : String){
        db.collection(USER_DATA).document(Constants.currentUserUid!!)
            .update(
                "nickname",nickname,
                "intro",intro
            ).addOnSuccessListener {
                downloadUserInfo(Constants.currentUserUid!!,_myPageResult)
                Constants.currentUserInfo!!.nickname = nickname
                Constants.currentUserInfo!!.intro = intro
                Log.d("xxxx", "uploadEditUserInfo: Successful $it")
            }.addOnFailureListener {
                Log.d("xxxx", "uploadEditUserInfo: Failure $it")
            }
    }

    // 유저 프로필 이미지 수정하기
    fun uploadUserProfileImg(photoUri : Uri) {

        val uploadTask = storage.reference.child("ProfileImg").child("${Constants.currentUserUid}")
            .putFile(photoUri!!)
        uploadTask.addOnSuccessListener {
            // 파일 저장 성공 시 이벤트
            downloadCurrentProfileImg(Constants.currentUserUid!!)
            Log.d("xxxx", " img upload successful $it")
        }.addOnFailureListener {
            // 파일 저장 실패 시 이벤트
            Log.d("xxxx", " img upload failure : $it ")
        }
    }

    // 관심 목록 추가 or 제거
    fun addOrSubFavoritePost(postPath: Timestamp){
        val uid = Constants.currentUserUid
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
                        likeList.add(uid!!)
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

    // 관심 목록 추가, 제거 분리하여 관리
    fun subFavoritePost(){}

}