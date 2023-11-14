package com.dreamteam.sharedream.home.Search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dreamteam.sharedream.model.PostRcv

class SearchViewModel : ViewModel() {
    private val searchRepository = SearchRepository()

    private val _searchResults = MutableLiveData<List<PostRcv>>()
    val searchResults: LiveData<List<PostRcv>> = _searchResults

    private val _selectedPost = MutableLiveData<PostRcv?>()
    val selectedPost: LiveData<PostRcv?> = _selectedPost
    fun performSearch(title: String) {
        searchRepository.searchTitle(title) { results ->
            _searchResults.value = results
        }
    }
    fun sortSearchLowPrice(title: String) {
        searchRepository.sortLikeUsers(title) { results ->
            _searchResults.value = results
        }
    }
    fun sortSearchHighPrice(title: String) {
        searchRepository.sortLikeUsersHigh(title) { results ->
            _searchResults.value = results
        }
    }
    fun onPostClicked(post: PostRcv) {
        _selectedPost.value = post
    }
    fun resetSelectedPost() {
        _selectedPost.value = null
    }
}