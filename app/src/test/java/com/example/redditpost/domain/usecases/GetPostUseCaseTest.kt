package com.example.redditpost.domain.usecases

import com.example.redditpost.domain.repository.PostRepository
import com.example.redditpost.factory.PostFactory
import com.example.redditpost.remote.model.Post
import com.example.redditpost.utils.ParamMissingException
import com.example.redditpost.utils.scheduler.TestSchedulerProvider
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.rxjava3.core.Single
import org.junit.Test

class GetPostUseCaseTest {
    private val repository = mock<PostRepository>()
    private val schedulerProvider = TestSchedulerProvider()
    private val useCase = GetPostUseCase(
        repository = repository, schedulerProvider = schedulerProvider
    )

    @Test(expected = ParamMissingException::class)
    fun `build use case single without params throw exception`() {
        useCase.buildUseCaseSingle()
    }

    @Test
    fun `build use case completable calls repository with the correct params`() {
        val params = GetPostUseCase.Params(t = "t", limit = 25, after = "")
        stubGetPost(Single.never())

        useCase.buildUseCaseSingle(params).test()
        verify(repository).getPost(t = "t", limit = 25, after = "")
    }

    @Test
    fun `get post return data and completes`() {
        val post = PostFactory.makePost()

        stubGetPost(Single.just(post))

        val params = GetPostUseCase.Params(t = "t", limit = 25, after = "")
        val testObserver = useCase.buildUseCaseSingle(params).test()

        testObserver.assertValue(post)
        testObserver.assertComplete()
    }

    private fun stubGetPost(single: Single<Post>) {
        whenever(repository.getPost(any(), any(), any()))
            .thenReturn(single)
    }

}