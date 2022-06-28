package com.example.redditpost.domain.usecases

import com.example.redditpost.domain.repository.PostRepository
import com.example.redditpost.remote.model.Post
import com.example.redditpost.utils.ParamMissingException
import com.example.redditpost.utils.scheduler.SchedulerProvider
import com.example.redditpost.utils.usecase.SingleUseCase
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class GetPostSearchUseCase @Inject constructor(
    private val repository: PostRepository,
    schedulerProvider: SchedulerProvider
    ) :
    SingleUseCase<Post,GetPostSearchUseCase.Params>(
        threadExecutor = schedulerProvider.io(),
        postExecutionThread = schedulerProvider.ui()
    ) {



    override fun buildUseCaseSingle(params: Params?):Single<Post> {
        if (params == null){
            throw ParamMissingException()
        }
        return repository.getPostSearch(q = params.q, limit = params.limit, after = params.after)
    }
    data class Params(val q: String, val limit: Int, val after: String)

}