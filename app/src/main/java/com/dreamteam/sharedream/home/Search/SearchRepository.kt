package com.dreamteam.sharedream.home.Search

import com.dreamteam.sharedream.model.Post
import com.google.firebase.firestore.FirebaseFirestore


class SearchRepository {
    private val firestore = FirebaseFirestore.getInstance()

    fun searchTitle(title: String, callback: (List<Post>) -> Unit) {
        val postCollection = firestore.collection("Posts")

        // query 객체 만들고 get으로 가져오기
        postCollection.whereGreaterThanOrEqualTo("title", title)
            .whereLessThanOrEqualTo("title", title + "\uf8ff")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val searchList = mutableListOf<Post>()

                // for문으로 data를 searchList에 넣어주고 adapter로 전달하기
                for (i in querySnapshot.documents) {
                    val data = i.toObject(Post::class.java)
                    data?.let {
                        searchList.add(it)
                    }
                }
                searchList.sortByDescending { it.price }
                callback(searchList)
            }
    }


    fun sortLikeUsers(title: String, callback: (List<Post>) -> Unit) {
        val postCollection = firestore.collection("Posts")

        // query 객체 만들고 get으로 가져오기
        val query = postCollection.whereGreaterThanOrEqualTo("title", title)
            .whereLessThanOrEqualTo("title", title + "\uf8ff")

        query.get().addOnSuccessListener { querySnapshot ->
            val searchList = mutableListOf<Post>()

            for (i in querySnapshot) {
                val data = i.toObject(Post::class.java)
                data?.let {
                    searchList.add(it)
                }
            }

            // 검색 결과를 가격(price)으로 정렬
            searchList.sortBy { it.price }
            callback(searchList)

        }

    }


    fun sortLikeUsersHigh(title: String,callback: (List<Post>) -> Unit) {
        val postCollection = firestore.collection("Posts")

        // query 객체 만들고 get으로 가져오기
        val query = postCollection.whereGreaterThanOrEqualTo("title", title)
            .whereLessThanOrEqualTo("title", title + "\uf8ff")

        query.get().addOnSuccessListener { querySnapshot ->
            val searchList = mutableListOf<Post>()

            for (i in querySnapshot) {
                val data = i.toObject(Post::class.java)
                data?.let {
                    searchList.add(it)
                }
            }

            // 검색 결과를 가격(price)으로 정렬
            searchList.sortByDescending { it.price }
            callback(searchList)

        }
    }
}