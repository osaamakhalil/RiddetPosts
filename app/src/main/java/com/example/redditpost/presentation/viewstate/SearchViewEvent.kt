package com.example.redditpost.presentation.viewstate

import com.example.redditpost.remote.model.DataX
import com.example.redditpost.utils.ViewEvent

sealed class SearchViewEvent : ViewEvent {
    data class ShowAddPostSnackBar(val post: DataX) : SearchViewEvent()
    data class ShowDeletePostSnackBar(val post: DataX) : SearchViewEvent()
    object Error : SearchViewEvent()
}