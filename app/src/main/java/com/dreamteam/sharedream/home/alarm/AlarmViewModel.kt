package com.dreamteam.sharedream.home.alarm

import AlarmPost
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dreamteam.sharedream.chat.Chatting
import com.dreamteam.sharedream.model.AlarmChatData
import com.dreamteam.sharedream.model.PostRcv

class AlarmViewModel : ViewModel() {

    val alarmRepo = AlarmRepo()

    private val _notiData: MutableLiveData<List<AlarmPost?>> = MutableLiveData()
    val notiData: LiveData<List<AlarmPost?>> = _notiData

    private val _selectedPost = MutableLiveData<AlarmPost?>()
    val selectedPost: LiveData<AlarmPost?> = _selectedPost

    private val _notiChatData: MutableLiveData<List<AlarmChatData?>> = MutableLiveData()
    val notiChatData: LiveData<List<AlarmChatData?>> = _notiChatData


    fun getNotiList() {
        alarmRepo.getMyalarm() { result ->
            _notiData.value = result
            Log.d("nyh", "getNotiList: $result")
        }
    }
    fun getChatNotiList(){
        alarmRepo.getMyChatalarm { result ->
            _notiChatData.value = result
            Log.d("nyh", "getChatNotiList: $result")
        }
    }

    fun onPostClicked(post: AlarmPost) {
        _selectedPost.value = post
    }
}