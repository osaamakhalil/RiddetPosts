package com.example.redditpost.domain.usecases

import com.example.redditpost.domain.repository.PostRepository
import com.example.redditpost.utils.scheduler.TestSchedulerProvider
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.rxjava3.core.Completable
import org.junit.Test

class DeleteAllPostsUseCaseTest {
    private val repository = mock<PostRepository>()
    private val schedulerProvider = TestSchedulerProvider()
    private val useCase = DeleteAllPostsUseCase(
        repository = repository, schedulerProvider = schedulerProvider
    )

    @Test
    fun `build use case completable calls repository`() {
        stubDeleteAllPost()

        useCase.buildUseCaseCompletable().test()
        verify(repository).deleteAllPosts()
    }

    @Test
    fun `build use case completable completes`() {
        stubDeleteAllPost()

        useCase.buildUseCaseCompletable().test()
            .assertComplete()

    }

    private fun stubDeleteAllPost() {
        whenever(repository.deleteAllPosts())
            .thenReturn(Completable.complete())
    }

}