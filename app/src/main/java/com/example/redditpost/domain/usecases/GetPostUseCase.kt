package com.example.redditpost.domain.usecases

import com.example.redditpost.domain.repository.PostRepository
import com.example.redditpost.remote.model.Post
import com.example.redditpost.utils.ParamMissingException
import com.example.redditpost.utils.scheduler.SchedulerProvider
import com.example.redditpost.utils.usecase.SingleUseCase
import io.reactivex.rxjava3.core.Single

import javax.inject.Inject


class GetPostUseCase @Inject constructor(
    private val repository: PostRepository,
    schedulerProvider: SchedulerProvider
) :
    SingleUseCase<Post, GetPostUseCase.Params>(
        threadExecutor = schedulerProvider.io(),
        postExecutionThread = schedulerProvider.ui()
    ) {


    data class Params(val t: String, val limit: Int, val after: String)

    override fun buildUseCaseSingle(params: Params?): Single<Post> {
        if (params == null) {
            throw ParamMissingException()
        }
        return repository.getPost(t = params.t, limit = params.limit, after = params.after)
    }
}