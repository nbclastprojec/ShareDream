package com.dreamteam.sharedream.home.Edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dreamteam.sharedream.model.PostData

class HomeViewModel : ViewModel() {

    private val _refreshData = MutableLiveData<Boolean>()

    val refreshData: LiveData<Boolean>
        get() = _refreshData

    fun refreshRecyclerView() {
        _refreshData.value = true
    }

    fun onRefreshComplete() {
        _refreshData.value = false
    }
}