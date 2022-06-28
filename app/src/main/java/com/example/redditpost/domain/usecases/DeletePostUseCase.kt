package com.example.redditpost.domain.usecases

import com.example.redditpost.domain.repository.PostRepository
import com.example.redditpost.remote.model.DataX
import com.example.redditpost.utils.ParamMissingException
import com.example.redditpost.utils.scheduler.SchedulerProvider
import com.example.redditpost.utils.usecase.CompletableUseCase
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

class DeletePostUseCase @Inject constructor(
    private val repository: PostRepository,
    schedulerProvider: SchedulerProvider
) : CompletableUseCase<DeletePostUseCase.Params>(
    threadExecutor = schedulerProvider.io(),
    postExecutionThread = schedulerProvider.ui()
) {

    override fun buildUseCaseCompletable(params: Params?): Completable {
        if (params == null){
             throw ParamMissingException()
        }
        return repository.deletePost(params.Post)
    }

    data class Params(val Post: DataX)

}