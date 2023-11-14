package com.dreamteam.sharedream.viewmodel

import com.dreamteam.sharedream.model.Post
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot

interface PostRepository {

    // 게시물 목록 전체 받아오기
    suspend fun downloadPosts(collection : String): Task<QuerySnapshot>

    // 게시물 업로드 하기.
    suspend fun uploadPost(collection:String, post: Post): String

    // 단일 포스트 받아오기
    suspend fun getUploadPost(collection: String, documentId: String):Task<DocumentSnapshot>

    // 관심 목록 추가, 게시글 상태 수정
    suspend fun updatePostStateOrLikeUser(collection: String, documentId: String, changeValue: String,editPost: Any): Task<Void>

    // 게시글 삭제하기
    suspend fun deletePost(collection: String,documentId: String): Task<Void>

    // 게시글 수정
    suspend fun uploadReplacePost(querySnapshot: QuerySnapshot, post: Post): Task<Void>
}