package com.example.redditpost.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.redditpost.domain.usecases.AddPostToFavoriteUseCase
import com.example.redditpost.domain.usecases.DeletePostUseCase
import com.example.redditpost.domain.usecases.GetPostUseCase
import com.example.redditpost.factory.PostFactory
import com.example.redditpost.presentation.viewstate.HomeViewEvent
import com.example.redditpost.presentation.viewstate.HomeViewState
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

class HomeViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val getPostUseCase = mock<GetPostUseCase>()
    private val addPostToFavoriteUseCase = mock<AddPostToFavoriteUseCase>()
    private val deletePostUseCase = mock<DeletePostUseCase>()
    private val networkUtil = mock<NetworkUtil>()
    private val viewModel: HomeViewModel by lazy {
        HomeViewModel(
            getPostUseCase = getPostUseCase,
            addPostToFavoriteUseCase = addPostToFavoriteUseCase,
            deletePostUseCase = deletePostUseCase,
            networkUtil = networkUtil
        )
    }

    @Test
    fun `init view model will emit loading state when internet is connected`() {
        stubNetworkChecker(true)
        stubGetPostUseCase(Single.never())
        assertEquals(HomeViewState.Loading, viewModel.homeViewState.value)
    }

    @Test
    fun `init view model will emit no internet state when internet isn't connected`() {
        stubNetworkChecker(false)

        verifyZeroInteractions(getPostUseCase)
        assertEquals(HomeViewState.NoInternet, viewModel.homeViewState.value)
    }

    @Test
    fun `when get post success view model will emit success view state`() {
        stubNetworkChecker(true)
        val post = PostFactory.makePost()
        val dataX = post.data.children.map { it.dataX }
        stubGetPostUseCase(Single.just(post))

        assertEquals(HomeViewState.Success(dataX), viewModel.homeViewState.value)
    }

    @Test
    fun `when get post error view model will emit error view state`() {
        stubNetworkChecker(true)
        stubGetPostUseCase(Single.error(Throwable()))

        assertEquals(HomeViewState.Error(), viewModel.homeViewState.value)
    }

    @Test
    fun `view model calls addPostToFavoriteUseCase with the correct params`() {
        stubNetworkChecker(true)
        stubAddPost(Completable.complete())
        val post = PostFactory.makePost()
        val dataX = PostFactory.makeDataX()

        stubGetPostUseCase(Single.just(post))
        val params = AddPostToFavoriteUseCase.Params(post = dataX)

        viewModel.addPostToFavorite(dataX)
        verify(addPostToFavoriteUseCase).execute(params)
        verifyNoMoreInteractions(addPostToFavoriteUseCase)
    }

    @Test
    fun `view model calls deletePostToFavoriteUseCase with the correct params`() {
        stubNetworkChecker(true)
        stubDeletePost(Completable.complete())
        val post = PostFactory.makePost()
        val dataX = PostFactory.makeDataX()
        val params = DeletePostUseCase.Params(dataX)

        stubGetPostUseCase(Single.just(post))
        viewModel.deletePostFromFavorite(dataX)
        verify(deletePostUseCase).execute(params)
        verifyNoMoreInteractions(deletePostUseCase)
    }
    @Test
    fun `when add post to favorite success view model will emit success view event`(){
        stubNetworkChecker(true)
        val post = PostFactory.makePost()
        val dataX = PostFactory.makeDataX()

        stubGetPostUseCase(Single.just(post))
        stubAddPost(Completable.complete())

        viewModel.addPostToFavorite(dataX)
        assertEquals(HomeViewEvent.ShowAddSnackBar(dataX),viewModel.homeViewEvent.value?.peekContent())
    }

    @Test
    fun `when add post to favorite error view model will emit error view event`(){
        stubNetworkChecker(true)
        val post = PostFactory.makePost()
        val dataX = PostFactory.makeDataX()

        stubAddPost(Completable.error(Throwable()))
        stubGetPostUseCase(Single.just(post))
        viewModel.addPostToFavorite(dataX)
        assertEquals(HomeViewEvent.Error,viewModel.homeViewEvent.value?.peekContent())
    }

    @Test
    fun `when delete post from favorite success view model will emit success view event`(){
        stubNetworkChecker(true)
        val post = PostFactory.makePost()
        val dataX = PostFactory.makeDataX()

        stubGetPostUseCase(Single.just(post))
        stubDeletePost(Completable.complete())

        viewModel.deletePostFromFavorite(dataX)
        assertEquals(HomeViewEvent.ShowDeleteSnackBar(dataX),viewModel.homeViewEvent.value?.peekContent())
    }
    @Test
    fun `when delete post from favorite error view model will emit error view event`(){
        stubNetworkChecker(true)
        val post = PostFactory.makePost()
        val dataX = PostFactory.makeDataX()

        stubGetPostUseCase(Single.just(post))
        stubDeletePost(Completable.error(Throwable()))

        viewModel.deletePostFromFavorite(dataX)
        assertEquals(HomeViewEvent.Error,viewModel.homeViewEvent.value?.peekContent())
    }

    private fun stubAddPost(completable: Completable) {
        whenever(addPostToFavoriteUseCase.execute(any()))
            .thenReturn(completable)
    }

    private fun stubDeletePost(completable: Completable) {
        whenever(deletePostUseCase.execute(any()))
            .thenReturn(completable)
    }

    private fun stubNetworkChecker(isConnected: Boolean) {
        whenever(networkUtil.hasInternetConnection())
            .thenReturn(isConnected)
    }

    private fun stubGetPostUseCase(single: Single<Post>) {
        whenever(getPostUseCase.execute(any()))
            .thenReturn(single)
    }


}