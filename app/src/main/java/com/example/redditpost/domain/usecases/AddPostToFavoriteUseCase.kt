package com.example.redditpost.domain.usecases

import com.example.redditpost.domain.repository.PostRepository
import com.example.redditpost.remote.model.DataX
import com.example.redditpost.utils.ParamMissingException
import com.example.redditpost.utils.scheduler.SchedulerProvider
import com.example.redditpost.utils.usecase.CompletableUseCase
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

class AddPostToFavoriteUseCase @Inject constructor(
    private val repository: PostRepository,
    schedulerProvider: SchedulerProvider
) :
    CompletableUseCase<AddPostToFavoriteUseCase.Params>(
        threadExecutor = schedulerProvider.io(),
        postExecutionThread = schedulerProvider.ui()
    ) {

    override fun buildUseCaseCompletable(params: Params?):Completable {
        if (params == null) {
            throw ParamMissingException()
        }
        return repository.insertPost(post = params.post)
    }

    data class Params(val post: DataX)
}