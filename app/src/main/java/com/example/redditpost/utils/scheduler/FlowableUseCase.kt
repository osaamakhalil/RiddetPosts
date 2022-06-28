package com.example.redditpost.utils.scheduler

import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.subscribers.DisposableSubscriber

/**
 * Abstract class for a Use Case (Interactor in terms of Clean Architecture).
 * This interface represents a execution unit for different use cases (this means any use case
 * in the application should implement this contract).
 *
 * By convention each UseCase implementation will return the result using a [DisposableSubscriber]
 * that will execute its job in a background thread and will post the result in the UI thread.
 *
 * This use case is to be used when we expect multiple values to be emitted via an [Flowable].
 */
abstract class FlowableUseCase<Results, in Params>(
    private val threadExecutor: Scheduler,
    private val postExecutionThread: Scheduler
) {

    /**
     * Builds an [Flowable] which will be used when executing the current [FlowableUseCase].
     */
    abstract fun buildUseCaseFlowable(params: Params? = null): Flowable<Results>

    /**
     * Executes the current use case.
     *
     * @param params   Parameters (Optional) used to build/execute this use case.
     */
    open fun execute(params: Params? = null): Flowable<Results> {
        return buildUseCaseFlowable(params)
            .subscribeOn(threadExecutor)
            .observeOn(postExecutionThread)
    }
}