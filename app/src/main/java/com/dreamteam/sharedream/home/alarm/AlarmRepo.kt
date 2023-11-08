package com.dreamteam.sharedream.home.alarm

import AlarmPost
import android.util.Log
import com.dreamteam.sharedream.Util.Constants
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class AlarmRepo {

    val db = Firebase.firestore
    private val storage = Firebase.storage

    fun getMyalarm(callback: (List<AlarmPost>) -> Unit) {
        val postCollection = db.collection("notifyList")
        //query 객체 만들고 get으로 가져오기
        postCollection.whereEqualTo("uid", Constants.currentUserUid)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val alarmList = mutableListOf<AlarmPost>()

                //for문으로 data를 searchList에 넣어주고 adapter로 전달하기

                for (i in querySnapshot.documents) {
                    val data = i.toObject(AlarmPost::class.java)
                    data?.let {
                        alarmList.add(it)
                        Log.d("nyh", "getMyalarm: $alarmList")
                    }
                }
                callback(alarmList)
            }.addOnFailureListener { exception ->
                Log.d("nyh", "getMyalarm fail : $exception")
            }
    }

    fun deleteItem(collectionName: String, documentId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val collectionReference = db.collection(collectionName)

        collectionReference.document(documentId)
            .delete()
            .addOnSuccessListener {
                // 삭제 성공 시 처리
                onSuccess()
            }
            .addOnFailureListener { e ->
                // 삭제 실패 시 처리
                onFailure(e)
            }
    }

}