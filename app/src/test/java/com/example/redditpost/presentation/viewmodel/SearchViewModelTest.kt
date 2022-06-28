package com.example.redditpost.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.redditpost.domain.usecases.AddPostToFavoriteUseCase
import com.example.redditpost.domain.usecases.DeletePostUseCase
import com.example.redditpost.domain.usecases.GetPostSearchUseCase
import com.example.redditpost.factory.PostFactory
import com.example.redditpost.presentation.viewstate.SearchViewEvent
import com.example.redditpost.presentation.viewstate.SearchViewState
import com.example.redditpost.remote.model.Post
import com.example.redditpost.utils.NetworkUtil
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class SearchViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val getPostSearchUseCase = mock<GetPostSearchUseCase>()
    private val addPostToFavoriteUseCase = mock<AddPostToFavoriteUseCase>()
    private val deletePostUseCase = mock<DeletePostUseCase>()
    private val networkUtil = mock<NetworkUtil>()
    private val viewModel: SearchViewModel by lazy {
        SearchViewModel(
            getPostSearchUseCase = getPostSearchUseCase,
            addPostToFavoriteUseCase = addPostToFavoriteUseCase,
            deletePostUseCase = deletePostUseCase,
            networkUtil = networkUtil
        )
    }

    @Test
    fun `init view model will emit no internet state when internet isn't connected`() {
        stubNetworkChecker(false)
        verifyZeroInteractions(getPostSearchUseCase)
        assertEquals(SearchViewState.NoInternet, viewModel.searchViewState.value)
    }

    @Test
    fun `when get search post success view model will emit success view state`() {
        stubNetworkChecker(true)
        val post = PostFactory.makePost()
        val dataX = post.data.children.map { it.dataX }
        stubGetSearchPost(Single.just(post))
        viewModel.getPostSearch("w")
        assertEquals(SearchViewState.Success(dataX), viewModel.searchViewState.value)
    }

    @Test
    fun `when get search post error view model will emit error view state`() {
        stubNetworkChecker(true)
        stubGetSearchPost(Single.error(Throwable()))
        viewModel.getPostSearch("w")
        assertEquals(SearchViewState.Error(), viewModel.searchViewState.value)
    }

    @Test
    fun `view model will calls get search post use case with the correct params`() {
        stubNetworkChecker(true)
        val post = PostFactory.makePost()
        stubGetSearchPost(Single.just(post))
        val params = GetPostSearchUseCase.Params(q = "w", limit = 25, after = "")
        viewModel.getPostSearch("w")
        verify(getPostSearchUseCase).execute(params)
        verifyNoMoreInteractions(addPostToFavoriteUseCase)
    }

    @Test
    fun `when add post to favorite success view model emit success view event`() {
        val dataX = PostFactory.makeDataX()
        stubAddPostToFavorite(Completable.complete())

        viewModel.addPostToFavorite(dataX)
        assertEquals(
            SearchViewEvent.ShowAddPostSnackBar(dataX),
            viewModel.searchViewEvent.value?.peekContent()
        )
    }

    @Test
    fun `when add post to favorite error view model will emit error view event`() {
        val dataX = PostFactory.makeDataX()
        stubAddPostToFavorite(Completable.error(Throwable()))
        viewModel.addPostToFavorite(dataX)
        assertEquals(SearchViewEvent.Error, viewModel.searchViewEvent.value?.peekContent())

    }

    @Test
    fun `view model calls add post to favorite use case with the correct params`() {
        val dataX = PostFactory.makeDataX()
        val params = AddPostToFavoriteUseCase.Params(dataX)
        stubAddPostToFavorite(Completable.complete())
        viewModel.addPostToFavorite(dataX)
        verify(addPostToFavoriteUseCase).execute(params)
        verifyNoMoreInteractions(addPostToFavoriteUseCase)
    }

    @Test
    fun `when delete post success view model will emit success view event`() {
        val dataX = PostFactory.makeDataX()
        stubDeletePostFromFavorite(Completable.complete())
        viewModel.deletePostFromFavorite(dataX)
        assertEquals(
            SearchViewEvent.ShowDeletePostSnackBar(dataX),
            viewModel.searchViewEvent.value?.peekContent()
        )
    }

    @Test
    fun `when delete post error view model will emit error view event`() {
        val dataX = PostFactory.makeDataX()
        stubDeletePostFromFavorite(Completable.error(Throwable()))
        viewModel.deletePostFromFavorite(dataX)
        assertEquals(SearchViewEvent.Error, viewModel.searchViewEvent.value?.peekContent())
    }

    @Test
    fun `view model calls delete post use case with the correct params`(){
        val dataX = PostFactory.makeDataX()
        val params = DeletePostUseCase.Params(dataX)
        stubDeletePostFromFavorite(Completable.complete())
        viewModel.deletePostFromFavorite(dataX)

        verify(deletePostUseCase).execute(params)
        verifyNoMoreInteractions(deletePostUseCase)
    }


    private fun stubNetworkChecker(isConnected: Boolean) {
        whenever(networkUtil.hasInternetConnection())
            .thenReturn(isConnected)
    }

    private fun stubGetSearchPost(single: Single<Post>) {
        whenever(getPostSearchUseCase.execute(any()))
            .thenReturn(single)
    }

    private fun stubAddPostToFavorite(completable: Completable) {
        whenever(addPostToFavoriteUseCase.execute(any()))
            .thenReturn(completable)
    }

    private fun stubDeletePostFromFavorite(completable: Completable) {
        whenever(deletePostUseCase.execute(any()))
            .thenReturn(completable)
    }
}