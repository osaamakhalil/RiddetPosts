package com.example.redditpost.presentation.viewstate

import com.example.redditpost.remote.model.DataX
import com.example.redditpost.utils.ViewEvent

sealed class FavoriteViewEvent: ViewEvent {
    data class ShowDeleteSnackBar(val post: DataX) : FavoriteViewEvent()
    object ShowDeleteAllDialog : FavoriteViewEvent()
    object Error : FavoriteViewEvent()
}