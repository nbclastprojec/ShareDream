package com.dreamteam.sharedream.home.alarm

import AlarmPost
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dreamteam.sharedream.model.PostRcv

class AlarmViewModel : ViewModel() {

    val alarmRepo = AlarmRepo()

    private val _notiData: MutableLiveData<List<AlarmPost?>> = MutableLiveData()
    val notiData: LiveData<List<AlarmPost?>> = _notiData

    private val _detailData: MutableLiveData<List<PostRcv>> = MutableLiveData()

    val detailData: LiveData<List<PostRcv>> = _detailData




    fun getNotiList() {
        alarmRepo.getMyalarm() { result ->
            _notiData.value = result
            Log.d("nyh", "getNotiList: $result")

        }
    }

    fun getDetailList() {
        alarmRepo.getPostDetail { result ->
            _detailData.postValue(result)
        }
    }
}