package com.dreamteam.sharedream.home.Edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dreamteam.sharedream.model.PostData

class HomeViewModel : ViewModel() {

    private val _refreshData = MutableLiveData<Boolean>()

    val refreshData: MutableLiveData<Boolean>
        get() = _refreshData

    fun onRefreshComplete() {
        _refreshData.value = false
    }
}