package com.dreamteam.sharedream.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CategoryViewModel : ViewModel() {

    private val _selectedCategory = MutableLiveData<String>()
    val selectedCategory: MutableLiveData<String> get() = _selectedCategory

    private val _seletedPrice = MutableLiveData<Pair<Long,Long>>()
    val seletedPrice:MutableLiveData<Pair<Long,Long>> get() = _seletedPrice

    fun onCategorySelected(category: String) {
            _selectedCategory.postValue(category)
        Log.d("nyh", "category viewModel onCategorySelected: $category")
    }
    fun onPriceSelected(minPrice:Long,maxPrice:Long) {
        _seletedPrice.postValue(Pair(minPrice,maxPrice))

    }
}