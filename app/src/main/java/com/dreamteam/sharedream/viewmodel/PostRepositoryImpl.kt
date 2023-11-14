package com.dreamteam.sharedream.viewmodel

import com.dreamteam.sharedream.model.Post
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class PostRepositoryImpl : PostRepository {

    private val db = Firebase.firestore

    override suspend fun downloadPosts(collection: String): Task<QuerySnapshot> {

        return db.collection(collection).orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
    }

    override suspend fun uploadPost(collection: String, post: Post): String {
        db.collection(collection).add(post)

        return post.documentId
    }

    override suspend fun getUploadPost(
        collection: String,
        documentId: String
    ): Task<DocumentSnapshot> {

        return db.collection(collection).document(documentId).get()
    }

    override suspend fun updatePostStateOrLikeUser(
        collection: String,
        documentId: String,
        changeValue: String,
        editPost: Any
    ): Task<Void> {

        return db.collection(collection).document(documentId).update(changeValue, editPost)
    }

    override suspend fun deletePost(collection: String, documentId: String): Task<Void> {

        return db.collection(collection).document(documentId).delete()
    }

    override suspend fun uploadReplacePost(querySnapshot: QuerySnapshot, post: Post): Task<Void> {

        return querySnapshot.documents[0].reference.set(post)
    }
}