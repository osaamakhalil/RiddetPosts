package com.example.redditpost.presentation.viewstate

import com.example.redditpost.remote.model.DataX

sealed class HomeViewState {

    object Loading : HomeViewState()
    object NoInternet : HomeViewState()
    data class Success(val posts: List<DataX>) : HomeViewState()
    data class Error(val message: String? = null) : HomeViewState()
}