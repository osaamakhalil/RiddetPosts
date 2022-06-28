package com.example.redditpost.presentation.viewstate

import com.example.redditpost.remote.model.DataX
import com.example.redditpost.utils.ViewEvent

sealed class HomeViewEvent : ViewEvent {

    data class ShowAddSnackBar(val post: DataX) : HomeViewEvent()
    data class ShowDeleteSnackBar(val post: DataX) : HomeViewEvent()
    object Error : HomeViewEvent()
}