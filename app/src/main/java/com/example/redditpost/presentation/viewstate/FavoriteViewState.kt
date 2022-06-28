package com.example.redditpost.presentation.viewstate

import com.example.redditpost.remote.model.DataX

sealed class FavoriteViewState {

    object Loading : FavoriteViewState()
    data class Success(val posts: List<DataX>) : FavoriteViewState()
    data class Error(val error: String? = null) : FavoriteViewState()
}