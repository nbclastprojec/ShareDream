package com.dreamteam.sharedream.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dreamteam.sharedream.model.PostRcv

class HomeViewModel : ViewModel() {
    private val homeRepository = HomeRepository()


    private val _refreshData = MutableLiveData<Boolean>()
    val refreshData: MutableLiveData<Boolean>
        get() = _refreshData

    private val _sortCategory = MutableLiveData<List<PostRcv?>>()
    val sortCategory: LiveData<List<PostRcv?>> = _sortCategory

    fun onRefreshComplete() {
        _refreshData.value = false
        _refreshData.value = false
    }

    fun sortCategorys(category: String) {
        homeRepository.categorySort(category) { result ->
            _sortCategory.value = result
            Log.d("nyh", "HomeViewModel sortCategorys: $result")
        }
    }
//    fun uploadChat(messageDTO: MessageDTO){
//        homeRepository.uploadChat(messageDTO)
//    }
}