package com.example.redditpost.presentation.viewstate

import com.example.redditpost.remote.model.DataX

sealed class SearchViewState {
    object Loading : SearchViewState()
    object NoInternet : SearchViewState()
    data class Success(val posts: List<DataX>) : SearchViewState()
    data class Error(val error: String? = null) : SearchViewState()
}