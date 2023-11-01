package com.dreamteam.sharedream.home.Search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dreamteam.sharedream.model.Post

class SearchViewModel : ViewModel() {
    private val searchRepository = SearchRepository()

    private val _searchResults = MutableLiveData<List<Post>>()
    val searchResults: LiveData<List<Post>> = _searchResults

    private val _selectedPost = MutableLiveData<Post?>()
    val selectedPost: LiveData<Post?> = _selectedPost

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
    fun onPostClicked(post: Post) {
        _selectedPost.value = post
    }

    fun resetSelectedPost() {
        _selectedPost.value = null
    }
}