package com.dreamteam.sharedream.home

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.dreamteam.sharedream.model.Post
import com.dreamteam.sharedream.model.PostRcv
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.storage

class HomeRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val storege = Firebase.storage

    fun categorySort(category: String, callback: (List<PostRcv>) -> Unit) {
        val postCollection = firestore.collection("Posts")
        Log.d("nyh", "categorySort: Repository category = $category")

        val query = if (category.isNullOrEmpty() || category == "전체"){
            postCollection
        }else {
            postCollection.whereEqualTo("category",category)
        }
        query.get()
            .addOnSuccessListener { querySnapshot ->
                val categoryList = mutableListOf<PostRcv>()

                Log.d("nyh", "home Repository categorySort:  $categoryList")
                for (i in querySnapshot.documents) {
                    val data = i.toObject(Post::class.java)
                    data?.let {
                        // 데이터를 PostRcv로 변환
                        convertPostToPostRcv(it, querySnapshot, categoryList, callback)
                    }
                }
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
            val downloadTask = storege.reference.child("post").child(uri).downloadUrl
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