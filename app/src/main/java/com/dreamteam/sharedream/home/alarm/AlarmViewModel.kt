package com.dreamteam.sharedream.home.alarm

import AlarmPost
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AlarmViewModel : ViewModel() {

    val alarmRepo = AlarmRepo()

    private val _notiData: MutableLiveData<List<AlarmPost?>> = MutableLiveData()
    val notiData: LiveData<List<AlarmPost?>> = _notiData




    fun getNotiList() {
        alarmRepo.getMyalarm() { result ->
            _notiData.value = result
            Log.d("nyh", "getNotiList: $result")

        }
    }

    fun deleteItem(collectionName: String, documentId: String) {
        alarmRepo.deleteItem(collectionName, documentId,
            onSuccess = {

            },
            onFailure = { e ->
                Log.e("nyh", "deleteItem failure: $e")
            }
        )
    }
}