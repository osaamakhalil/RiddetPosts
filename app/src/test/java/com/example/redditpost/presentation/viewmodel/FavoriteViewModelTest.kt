package com.example.redditpost.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.redditpost.domain.usecases.AddPostToFavoriteUseCase
import com.example.redditpost.domain.usecases.DeleteAllPostsUseCase
import com.example.redditpost.domain.usecases.DeletePostUseCase
import com.example.redditpost.domain.usecases.GetAllPostsUseCase
import com.example.redditpost.factory.PostFactory
import com.example.redditpost.presentation.viewstate.FavoriteViewEvent
import com.example.redditpost.presentation.viewstate.FavoriteViewState
import com.example.redditpost.remote.model.DataX
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class FavoriteViewModelTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val getAllPostsUseCase = mock<GetAllPostsUseCase>()
    private val addPostToFavoriteUseCase = mock<AddPostToFavoriteUseCase>()
    private val deletePostUseCase = mock<DeletePostUseCase>()
    private val deleteAllPostUseCase = mock<DeleteAllPostsUseCase>()
    private val viewModel: FavoriteViewModel by lazy {
        FavoriteViewModel(
            addPostToFavoriteUseCase = addPostToFavoriteUseCase,
            deletePostUseCase = deletePostUseCase,
            deleteAllPostsUseCase = deleteAllPostUseCase,
            getAllPostsUseCase = getAllPostsUseCase,
        )
    }

    @Test
    fun `init view model will emit loading state`() {
        stubGetAllPosts(Flowable.never())
        assertEquals(FavoriteViewState.Loading, viewModel.favoriteViewState.value)
    }

    @Test
    fun `when get all post success view model will emit success view state`() {
        val post = PostFactory.makeDataX()
        stubGetAllPosts(Flowable.just(listOf(post)))
        viewModel.getAllPosts()
        assertEquals(FavoriteViewState.Success(listOf(post)), viewModel.favoriteViewState.value)
    }

    @Test
    fun `when get all post error view model will emit error view state`() {
        stubGetAllPosts(Flowable.error(Throwable()))
        viewModel.getAllPosts()
        assertEquals(FavoriteViewState.Error(), viewModel.favoriteViewState.value)
    }

    @Test
    fun `view model calls get all post use case with the correct params`() {
        val post = PostFactory.makeDataX()
        stubGetAllPosts(Flowable.just(listOf(post)))
        viewModel.hashCode()
        verify(getAllPostsUseCase).execute()
        verifyNoMoreInteractions(getAllPostsUseCase)
    }

    @Test
    fun `view model calls add post to favorite use case with the correct params`() {
        stubAddPost(Completable.complete())
        val dataX = PostFactory.makeDataX()
        stubGetAllPosts(Flowable.just(listOf(dataX)))
        val params = AddPostToFavoriteUseCase.Params(post = dataX)

        viewModel.addPostToFavorite(dataX)
        verify(addPostToFavoriteUseCase).execute(params)
        verifyNoMoreInteractions(addPostToFavoriteUseCase)
    }

    @Test
    fun `view model calls delete post from favorite use case with the correct params`() {
        stubDeletePost(Completable.complete())
        val dataX = PostFactory.makeDataX()
        stubGetAllPosts(Flowable.just(listOf(dataX)))
        val params = DeletePostUseCase.Params(dataX)

        viewModel.deletePostFromFavorite(dataX)
        verify(deletePostUseCase).execute(params)
        verifyNoMoreInteractions(addPostToFavoriteUseCase)
    }

    @Test
    fun `when delete post to favorite success view model will emit success view event`() {
        val post = PostFactory.makeDataX()
        stubDeletePost(Completable.complete())
        stubGetAllPosts(Flowable.just(listOf(post)))

        viewModel.deletePostFromFavorite(post)
        assertEquals(
            FavoriteViewEvent.ShowDeleteSnackBar(post),
            viewModel.favoriteViewEvent.value?.peekContent()
        )
    }

    @Test
    fun `when delete post to favorite error view model will emit error view event`() {
        val post = PostFactory.makeDataX()
        stubDeletePost(Completable.error(Throwable()))
        stubGetAllPosts(Flowable.just(listOf(post)))


        viewModel.deletePostFromFavorite(post)
        assertEquals(
            FavoriteViewEvent.Error,
            viewModel.favoriteViewEvent.value?.peekContent()
        )
    }

    @Test
    fun `when click on clear all user view model will emit show delete dialog event`() {
        val post = PostFactory.makeDataX()
        stubGetAllPosts(Flowable.just(listOf(post)))

        viewModel.showDeleteAllDialog()
        assertEquals(
            FavoriteViewEvent.ShowDeleteAllDialog,
            viewModel.favoriteViewEvent.value?.peekContent()
        )
    }

    @Test
    fun `when delete all post from favorite error view model will emit error view event`() {
        val post = PostFactory.makeDataX()
        stubGetAllPosts(Flowable.just(listOf(post)))
        stubDeleteAllPost(Completable.error(Throwable()))

        viewModel.clearAllPosts()
        assertEquals(FavoriteViewEvent.Error, viewModel.favoriteViewEvent.value?.peekContent())
    }


    private fun stubGetAllPosts(flowable: Flowable<List<DataX>>) {
        whenever(getAllPostsUseCase.execute())
            .thenReturn(flowable)
    }

    private fun stubAddPost(completable: Completable) {
        whenever(addPostToFavoriteUseCase.execute(any()))
            .thenReturn(completable)
    }

    private fun stubDeletePost(completable: Completable) {
        whenever(deletePostUseCase.execute(any()))
            .thenReturn(completable)
    }

    private fun stubDeleteAllPost(completable: Completable) {
        whenever(deleteAllPostUseCase.execute())
            .thenReturn(completable)
    }

}