package com.dreamteam.sharedream.home.Edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dreamteam.sharedream.model.PostData

class HomeViewModel : ViewModel() {

    private val _refreshData = MutableLiveData<Boolean>()//새로고침

    val refreshData: LiveData<Boolean>
        get() = _refreshData

    fun refreshRecyclerView() {
        _refreshData.value = true//새로고침중
    }

    fun onRefreshComplete() {
        _refreshData.value = false//새로고침완료
    }
}