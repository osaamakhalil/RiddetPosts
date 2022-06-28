package com.example.redditpost.domain.usecases

import com.example.redditpost.domain.repository.PostRepository
import com.example.redditpost.remote.model.DataX
import com.example.redditpost.utils.scheduler.FlowableUseCase
import com.example.redditpost.utils.scheduler.SchedulerProvider
import io.reactivex.rxjava3.core.Flowable
import javax.inject.Inject

class GetAllPostsUseCase @Inject constructor(
    private val repository: PostRepository,
    schedulerProvider: SchedulerProvider
) : FlowableUseCase<List<DataX>, Unit>(
    threadExecutor = schedulerProvider.io(),
    postExecutionThread = schedulerProvider.ui()
) {

    override fun buildUseCaseFlowable(params: Unit?): Flowable<List<DataX>> {
        return repository.getAllPosts()
    }
}