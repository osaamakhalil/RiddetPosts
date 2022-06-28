package com.example.redditpost.domain.usecases

import com.example.redditpost.domain.repository.PostRepository
import com.example.redditpost.factory.PostFactory
import com.example.redditpost.utils.ParamMissingException
import com.example.redditpost.utils.scheduler.TestSchedulerProvider
import com.nhaarman.mockitokotlin2.*
import io.reactivex.rxjava3.core.Completable
import org.junit.Test

class AddPostToFavoriteUseCaseTest {
    private val repository = mock<PostRepository>()
    private val schedulerProvider = TestSchedulerProvider()
    private val useCase = AddPostToFavoriteUseCase(
        repository = repository, schedulerProvider = schedulerProvider
    )

    @Test(expected = ParamMissingException::class)
    fun `build use case completable without params throw exception`() {
        useCase.buildUseCaseCompletable()
    }

    @Test
    fun `build use case completable calls repository with the correct post`() {
        stubAddPostToFavorite()
        val post = PostFactory.makeDataX()
        val params = AddPostToFavoriteUseCase.Params(post = post)

        useCase.buildUseCaseCompletable(params).test()
        verify(repository).insertPost(post)
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `build use case completable completes`() {
        stubAddPostToFavorite()

        val post = PostFactory.makeDataX()
        val params = AddPostToFavoriteUseCase.Params(post = post)

        useCase.buildUseCaseCompletable(params).test()
            .assertComplete()
    }


    private fun stubAddPostToFavorite() {
        whenever(repository.insertPost(any()))
            .thenReturn(Completable.complete())
    }

}

