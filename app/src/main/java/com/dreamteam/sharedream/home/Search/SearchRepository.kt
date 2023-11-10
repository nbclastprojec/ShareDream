package com.dreamteam.sharedream.home.Search


import android.net.Uri
import com.dreamteam.sharedream.model.Post
import com.dreamteam.sharedream.model.PostRcv
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class SearchRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val strage = Firebase.storage

    fun searchTitle(title: String, callback: (List<PostRcv>) -> Unit) {
        val postCollection = firestore.collection("Posts")

        // query 객체 만들고 get으로 가져오기
        postCollection.whereGreaterThanOrEqualTo("title", title)
            .whereLessThanOrEqualTo("title", title + "\uf8ff")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val searchList = mutableListOf<PostRcv>()

                // for문으로 data를 searchList에 넣어주고 adapter로 전달하기
                for (i in querySnapshot.documents) {
                    val data = i.toObject(Post::class.java)
                    data?.let {
                        convertPostToPostRcv(it, querySnapshot, searchList, callback)
                    }
                }
                searchList.sortByDescending { it.price }
                callback(searchList)
            }
    }

    fun sortLikeUsers(title: String, callback: (List<PostRcv>) -> Unit) {
        val postCollection = firestore.collection("Posts")

        // query 객체 만들고 get으로 가져오기
        val query = postCollection.whereGreaterThanOrEqualTo("title", title)
            .whereLessThanOrEqualTo("title", title + "\uf8ff")

        query.get().addOnSuccessListener { querySnapshot ->
            val searchList = mutableListOf<PostRcv>()

            for (i in querySnapshot) {
                val data = i.toObject(Post::class.java)
                data?.let {
                    convertPostToPostRcv(it, querySnapshot, searchList, callback)
                }
            }

            // 검색 결과를 가격(price)으로 정렬
            searchList.sortBy { it.price }
            callback(searchList)

        }

    }


    fun sortLikeUsersHigh(title: String,callback: (List<PostRcv>) -> Unit) {
        val postCollection = firestore.collection("Posts")

        // query 객체 만들고 get으로 가져오기
        val query = postCollection.whereGreaterThanOrEqualTo("title", title)
            .whereLessThanOrEqualTo("title", title + "\uf8ff")

        query.get().addOnSuccessListener { querySnapshot ->
            val searchList = mutableListOf<PostRcv>()

            for (i in querySnapshot) {
                val data = i.toObject(Post::class.java)
                data?.let {
                    convertPostToPostRcv(it, querySnapshot, searchList, callback)
                }
            }

            // 검색 결과를 가격(price)으로 정렬
            searchList.sortByDescending { it.price }
            callback(searchList)

        }
    }
    private fun convertPostToPostRcv(
        post: Post,
        querySnapshot: QuerySnapshot,
        postRcvList: MutableList<PostRcv>,
        callback: (List<PostRcv>) -> Unit
    ) {
        val postImgUris: List<String> = post.imgs
        val postImgList: MutableList<Uri> = mutableListOf()

        val downloadTasks = mutableListOf<Task<Uri>>()
        for (uri in postImgUris) {
            val downloadTask = strage.reference.child("post").child(uri).downloadUrl
            downloadTasks.add(downloadTask)
        }

        Tasks.whenAllSuccess<Uri>(downloadTasks)
            .addOnSuccessListener { uriList ->
                postImgList.addAll(uriList)

                if (postImgUris.size == postImgList.size) {
                    var postRcv = PostRcv(
                        uid = post.uid,
                        title = post.title,
                        price = post.price.toString().replace(",", "").toLong(),
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
                        documentId = post.documentId,
                        locationLatLng = post.locationLatLng,
                        locationKeyword = post.locationKeyword,
                        endDate = post.endTime
                    )

                    var inserted = false
                    for (index in postRcvList.indices) {
                        if (postRcv.timestamp > postRcvList[index].timestamp) {
                            postRcvList.add(index, postRcv)
                            inserted = true
                            break
                        }
                    }
                    if (!inserted) {
                        postRcvList.add(postRcv)
                    }
                    if (postRcvList.size == querySnapshot.size()) {
                        callback(postRcvList) // 변환한 목록을 콜백으로 반환
                    }
                }
            }
    }
}