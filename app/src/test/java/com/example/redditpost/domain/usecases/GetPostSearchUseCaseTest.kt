package com.example.redditpost.domain.usecases

import com.example.redditpost.domain.repository.PostRepository
import com.example.redditpost.factory.PostFactory
import com.example.redditpost.remote.model.Post
import com.example.redditpost.utils.ParamMissingException
import com.example.redditpost.utils.scheduler.TestSchedulerProvider
import com.nhaarman.mockitokotlin2.*
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.channels.Channel
import org.junit.Test

class GetPostSearchUseCaseTest {

    private val repository = mock<PostRepository>()
    private val schedulerProvider = TestSchedulerProvider()
    private val useCase = GetPostSearchUseCase(
        repository = repository, schedulerProvider = schedulerProvider
    )

    @Test(expected = ParamMissingException::class)
    fun `build use case single without params throw exception`() {
        useCase.buildUseCaseSingle()
    }

    @Test
    fun `build use case single calls repository with correct params`() {
        val params = GetPostSearchUseCase.Params(q = "q", limit = 25, after = "")

        stubGetSearchPost(Single.never())

        useCase.buildUseCaseSingle(params).test()
        verify(repository).getPostSearch(q = "q", limit = 25, after = "")

    }


    @Test
    fun `get post search return data and complete`() {
        val post = PostFactory.makePost()
        val params = GetPostSearchUseCase.Params(q = "q", limit = 25, after = "")
        stubGetSearchPost(Single.just(post))

        val testObserver = useCase.buildUseCaseSingle(params).test()
        testObserver.assertValue(post)
            .assertComplete()
    }


    private fun stubGetSearchPost(single: Single<Post>) {
        whenever(repository.getPostSearch(any(), any(), any()))
            .thenReturn(single)
    }

}