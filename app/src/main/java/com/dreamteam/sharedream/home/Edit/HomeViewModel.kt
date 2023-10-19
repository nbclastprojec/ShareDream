package com.dreamteam.sharedream.home.Edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dreamteam.sharedream.model.PostData

class HomeViewModel : ViewModel() {

    private val _refreshData = MutableLiveData<Boolean>()

    // 외부에서 접근 가능한 LiveData를 생성합니다.
    val refreshData: LiveData<Boolean>
        get() = _refreshData

    // 데이터를 갱신하는 함수를 제공합니다.
    fun refreshRecyclerView() {
        // MutableLiveData 값을 변경하여 RecyclerView 갱신을 알립니다.
        _refreshData.value = true
    }

    // RecyclerView가 갱신되었음을 알린 후에 값을 초기화합니다.
    fun onRefreshComplete() {
        _refreshData.value = false
    }
}