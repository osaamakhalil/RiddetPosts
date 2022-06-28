package com.example.redditpost.domain.usecases

import com.example.redditpost.domain.repository.PostRepository
import com.example.redditpost.factory.PostFactory
import com.example.redditpost.remote.model.DataX
import com.example.redditpost.utils.scheduler.TestSchedulerProvider
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.rxjava3.core.Flowable
import org.junit.Test

class GetAllPostsUseCaseTest{
    private val repository = mock<PostRepository>()
    private val schedulerProvider = TestSchedulerProvider()
    private val useCase = GetAllPostsUseCase(
        repository = repository, schedulerProvider = schedulerProvider
    )

    @Test
    fun`build use case completable calls repository`(){
        val post = PostFactory.makePost()
        val dataX = post.data.children.map { it.dataX }
        stubGetAllPost(Flowable.just(dataX))
        useCase.buildUseCaseFlowable().test()
        verify(repository).deleteAllPosts()
    }

    private fun stubGetAllPost(flowable: Flowable<List<DataX>>){
        whenever(repository.getAllPosts())
            .thenReturn(flowable)

    }

}