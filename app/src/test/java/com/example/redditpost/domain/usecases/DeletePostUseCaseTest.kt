package com.example.redditpost.domain.usecases

import com.example.redditpost.domain.repository.PostRepository
import com.example.redditpost.factory.PostFactory
import com.example.redditpost.utils.scheduler.TestSchedulerProvider
import com.nhaarman.mockitokotlin2.*
import io.reactivex.rxjava3.core.Completable
import org.junit.Test


class DeletePostUseCaseTest() {

    private val repository = mock<PostRepository>()
    private val schedulerProvider = TestSchedulerProvider()
    private val useCase = DeletePostUseCase(
        repository = repository,
        schedulerProvider = schedulerProvider
    )

    @Test
    fun `build use case completable calls repository with the correct post`() {
        stubDeletePost()

        val post = PostFactory.makeDataX()
        val params = DeletePostUseCase.Params(post)

        useCase.buildUseCaseCompletable(params).test()
        verify(repository).deletePost(post)
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `build use case completable completes`() {
        stubDeletePost()

        val post = PostFactory.makeDataX()
        val params = DeletePostUseCase.Params(post)

        useCase.buildUseCaseCompletable(params).test()
            .assertComplete()
    }

    private fun stubDeletePost() {
        whenever(repository.deletePost(any()))
            .thenReturn(Completable.complete())
    }
}