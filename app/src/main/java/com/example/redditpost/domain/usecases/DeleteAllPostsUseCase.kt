package com.example.redditpost.domain.usecases

import com.example.redditpost.domain.repository.PostRepository
import com.example.redditpost.utils.scheduler.SchedulerProvider
import com.example.redditpost.utils.usecase.CompletableUseCase
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

class DeleteAllPostsUseCase @Inject constructor(
    private val repository: PostRepository,
    schedulerProvider: SchedulerProvider
) : CompletableUseCase<DeleteAllPostsUseCase>(
    threadExecutor = schedulerProvider.io(),
    postExecutionThread = schedulerProvider.ui()
) {

    override fun buildUseCaseCompletable(params: DeleteAllPostsUseCase?): Completable {
        return repository.deleteAllPosts()
    }

}