package com.example.redditpost.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.redditpost.remote.model.DataX
import com.example.redditpost.remote.model.Post
import com.example.redditpost.domain.usecases.AddPostToFavoriteUseCase
import com.example.redditpost.domain.usecases.DeletePostUseCase
import com.example.redditpost.domain.usecases.GetPostUseCase
import com.example.redditpost.presentation.viewstate.HomeViewEvent
import com.example.redditpost.presentation.viewstate.HomeViewState
import com.example.redditpost.utils.Event
import com.example.redditpost.utils.NetworkUtil
import io.reactivex.rxjava3.disposables.CompositeDisposable
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val getPostUseCase: GetPostUseCase,
    private val addPostToFavoriteUseCase: AddPostToFavoriteUseCase,
    private val deletePostUseCase: DeletePostUseCase,
    private val networkUtil: NetworkUtil,
) : ViewModel() {


    private val compositeDisposable = CompositeDisposable()

    private val _homeViewState = MutableLiveData<HomeViewState>()
    val homeViewState: LiveData<HomeViewState>
        get() = _homeViewState

    private val _homeViewEvent = MutableLiveData<Event<HomeViewEvent>>()
    val homeViewEvent: LiveData<Event<HomeViewEvent>>
        get() = _homeViewEvent


    // for paging handle like since, send as a query pram to get the next group of  post
    private var after = ""

    /*
    *for paging handle
    *add a new results to this list and use it to send to the view to present all results
    * */
    private var postResponse: MutableList<DataX>? = null

    init {
        HomeViewState.Loading.also(_homeViewState::setValue)
        getPost()
    }

    fun getPost() {
        if (networkUtil.hasInternetConnection()) {
            if (postResponse == null) _homeViewState.postValue(HomeViewState.Loading)
            val params = GetPostUseCase.Params(t = "t", limit = 25, after = after)
            compositeDisposable.add(
                getPostUseCase.execute(params = params)
                    .subscribe({ response -> onGetPostSuccess(response) },
                        { error -> onGetPostFailure(error) })
            )
        } else {
            //if no list to show, then show some icon on center of screen
            if (postResponse == null) {
                _homeViewState.postValue(HomeViewState.NoInternet)
            } else {
                //if list not empty, used that to show no internet on bottom of page
                _homeViewState.postValue(HomeViewState.Success(requireNotNull(postResponse)))
            }
        }
    }

    //this to fun to handle our get post response if success or failure
    private fun onGetPostSuccess(response: Post) {
        val childrenList = response.data.children.map { it.dataX }
        if (childrenList.isNotEmpty()) {
            if (postResponse == null) {
                postResponse = childrenList.toMutableList()
            } else {
                postResponse?.addAll(childrenList)
            }
            _homeViewState.postValue(HomeViewState.Success(requireNotNull(postResponse)))
            after = response.data.after
        }
    }

    private fun onGetPostFailure(error: Throwable) {
        _homeViewState.postValue(HomeViewState.Error(error.message))
    }


    fun addPostToFavorite(post: DataX) {
        val params = AddPostToFavoriteUseCase.Params(post = post)
            addPostToFavoriteUseCase.execute(params)
                .subscribe({
                    _homeViewEvent.postValue(Event(HomeViewEvent.ShowAddSnackBar(post)))
                }, { error ->
                    _homeViewEvent.postValue(Event(HomeViewEvent.Error))
                    Log.e("HomeViewModel", "error in add post $error")
                }).also(compositeDisposable::add)
    }

    fun deletePostFromFavorite(post: DataX) {
        val params = DeletePostUseCase.Params(post)
        compositeDisposable.add(
            deletePostUseCase.execute(params)
                .subscribe({
                    _homeViewEvent.postValue(Event(HomeViewEvent.ShowDeleteSnackBar(post)))

                }, { error ->
                    _homeViewEvent.postValue(Event(HomeViewEvent.Error))
                    Log.e("HomeViewModel", "error in delete post $error")
                })
        )
    }

    /*
* use this fun when swipe screen to refresh and make call to get the first page
* with the after = " "
*  */
    fun swipeToRefresh() {
        after = ""
        postResponse = mutableListOf()
        getPost()
    }

    override fun onCleared() {
        // Using clear will clear all, but can accept new disposable
        compositeDisposable.clear()
        super.onCleared()

    }
}