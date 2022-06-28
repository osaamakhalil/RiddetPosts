package com.example.redditpost.presentation.viewmodel


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.redditpost.remote.model.DataX
import com.example.redditpost.remote.model.Post
import com.example.redditpost.domain.usecases.AddPostToFavoriteUseCase
import com.example.redditpost.domain.usecases.DeletePostUseCase
import com.example.redditpost.domain.usecases.GetPostSearchUseCase
import com.example.redditpost.presentation.viewstate.SearchViewEvent
import com.example.redditpost.presentation.viewstate.SearchViewState
import com.example.redditpost.utils.Event
import com.example.redditpost.utils.NetworkUtil
import io.reactivex.rxjava3.disposables.CompositeDisposable
import javax.inject.Inject


class SearchViewModel @Inject constructor(
    private val getPostSearchUseCase: GetPostSearchUseCase,
    private val addPostToFavoriteUseCase: AddPostToFavoriteUseCase,
    private val deletePostUseCase: DeletePostUseCase,
    private val networkUtil: NetworkUtil
) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    private val _searchViewState = MutableLiveData<SearchViewState>()
    val searchViewState: LiveData<SearchViewState>
        get() = _searchViewState

    private val _searchViewEvent = MutableLiveData<Event<SearchViewEvent>>()
    val searchViewEvent: LiveData<Event<SearchViewEvent>>
        get() = _searchViewEvent

    /*
    *for paging handle
    *add a new results to this list and use it to send to the view to present all results
    * */
    private var searchResponse: MutableList<DataX>? = null

    // for paging handle like since, send as a query pram to get the next group of post
    private var after: String? = ""

    init {
        if (!networkUtil.hasInternetConnection()) {
            _searchViewState.postValue(SearchViewState.NoInternet)
        }
    }


    fun getPostSearch(q: String) {
        //check if in the last page
        if (after != null) {
            if (networkUtil.hasInternetConnection()) {
                if (searchResponse == null) _searchViewState.postValue(SearchViewState.Loading)
                val params = GetPostSearchUseCase.Params(q = q, limit = 25, after = after!!)
                compositeDisposable.add(
                    getPostSearchUseCase.execute(params = params)
                        .subscribe({ response -> onGetSearchPostSuccess(response) },
                            { error -> onGetSearchPostFailure(error) })
                )

            } else {
                //if no list to show, then show some icon on center of screen
                if (searchResponse == null) {
                    _searchViewState.postValue(SearchViewState.NoInternet)
                } else {
                    //if list not empty, used that to show no internet on bottom of page
                    _searchViewState.postValue(SearchViewState.Success(requireNotNull(searchResponse)))
                }
            }
        } else {
            Log.e("SearchViewModel", "these is the last page")
        }
    }

    //this to fun to handle our getPostSearch response if success or failure
    private fun onGetSearchPostSuccess(searchList: Post) {
        val dataXList = searchList.data.children.map { it.dataX }
        if (dataXList.isNotEmpty()) {
            if (searchResponse == null) {
                searchResponse = dataXList.toMutableList()
            } else {
                searchResponse?.addAll(dataXList)
            }
            _searchViewState.postValue(SearchViewState.Success(requireNotNull(searchResponse)))
            after = searchList.data.after
            //if the no more page then server send after = null
            if (after == null) {
                networkUtil.isLastPage(true)
            }
        }
    }

    private fun onGetSearchPostFailure(error: Throwable) {
        _searchViewState.postValue(SearchViewState.Error(error.message))
    }

    fun clearSearch() {
        after = ""
        searchResponse?.clear()
    }

    fun addPostToFavorite(post: DataX) {
        val params = AddPostToFavoriteUseCase.Params(post = post)
        compositeDisposable.add(
            addPostToFavoriteUseCase.execute(params)
                .subscribe({
                    _searchViewEvent.postValue(Event(SearchViewEvent.ShowAddPostSnackBar(post)))
                },
                    { error ->
                        _searchViewEvent.postValue(Event(SearchViewEvent.Error))
                        Log.e("SearchViewModel", "error in add post $error")
                    })
        )
    }

    fun deletePostFromFavorite(post: DataX) {
        val params = DeletePostUseCase.Params(post)
        compositeDisposable.add(
            deletePostUseCase.execute(params)
                .subscribe({
                    _searchViewEvent.postValue(Event(SearchViewEvent.ShowDeletePostSnackBar(post)))
                }, { error ->
                    _searchViewEvent.postValue(Event(SearchViewEvent.Error))
                    Log.e("SearchViewModel", "error in delete post $error")
                }
                )
        )
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }


}