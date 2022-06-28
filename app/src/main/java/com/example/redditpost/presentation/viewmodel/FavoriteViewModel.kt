package com.example.redditpost.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.redditpost.remote.model.DataX
import com.example.redditpost.domain.usecases.AddPostToFavoriteUseCase
import com.example.redditpost.domain.usecases.DeleteAllPostsUseCase
import com.example.redditpost.domain.usecases.DeletePostUseCase
import com.example.redditpost.domain.usecases.GetAllPostsUseCase
import com.example.redditpost.presentation.viewstate.FavoriteViewEvent
import com.example.redditpost.presentation.viewstate.FavoriteViewState
import com.example.redditpost.utils.Event
import io.reactivex.rxjava3.disposables.CompositeDisposable
import javax.inject.Inject


class FavoriteViewModel @Inject constructor(
    private val addPostToFavoriteUseCase: AddPostToFavoriteUseCase,
    private val deletePostUseCase: DeletePostUseCase,
    private val deleteAllPostsUseCase: DeleteAllPostsUseCase,
    private val getAllPostsUseCase: GetAllPostsUseCase,
) : ViewModel() {


    private val _favoriteViewState = MutableLiveData<FavoriteViewState>()
    val favoriteViewState: LiveData<FavoriteViewState>
        get() = _favoriteViewState

    private val _favoriteViewEvent = MutableLiveData<Event<FavoriteViewEvent>>()
    val favoriteViewEvent: LiveData<Event<FavoriteViewEvent>>
        get() = _favoriteViewEvent

    private val compositeDisposable = CompositeDisposable()

    init {
        FavoriteViewState.Loading.also(_favoriteViewState::setValue)
        getAllPosts()
    }

    fun getAllPosts() {
        getAllPostsUseCase.execute()
            .subscribe({ list -> onGetFavoritePostList(list) },
                { error -> onGetFavoritePostListError(error) })
            .also(compositeDisposable::add)
    }

    private fun onGetFavoritePostList(list: List<DataX>) {
        _favoriteViewState.postValue(FavoriteViewState.Success(list))
    }

    private fun onGetFavoritePostListError(throwable: Throwable) {
        _favoriteViewState.postValue(FavoriteViewState.Error(throwable.message))
    }

    fun deletePostFromFavorite(post: DataX) {
        val params = DeletePostUseCase.Params(post)
        compositeDisposable.add(
            deletePostUseCase.execute(params)
                .subscribe({
                    _favoriteViewEvent.postValue(Event(FavoriteViewEvent.ShowDeleteSnackBar(post)))
                }, { error ->
                    _favoriteViewEvent.postValue(Event(FavoriteViewEvent.Error))
                    Log.e("FavoriteViewModel", "error in delete post $error")
                }
                )
        )
    }

    fun addPostToFavorite(post: DataX) {
        val params = AddPostToFavoriteUseCase.Params(post = post)
        compositeDisposable.add(
            addPostToFavoriteUseCase.execute(params)
                .subscribe({}, { error ->
                    _favoriteViewEvent.postValue(Event(FavoriteViewEvent.Error))
                    Log.e("FavoriteViewModel", "error in add post $error")
                }
                )
        )
    }

    fun showDeleteAllDialog() {
        _favoriteViewEvent.postValue(Event(FavoriteViewEvent.ShowDeleteAllDialog))
    }

    fun clearAllPosts() {
        compositeDisposable.add(
            deleteAllPostsUseCase
                .execute()
                .subscribe({}, { error ->
                    _favoriteViewEvent.postValue(Event(FavoriteViewEvent.Error))
                    Log.e("FavoriteViewModel", "error in add post $error")
                }
                )
        )
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }

}