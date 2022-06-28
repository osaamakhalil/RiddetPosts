package com.example.redditpost.utils.usecase

import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.observers.DisposableSingleObserver


/**
 * Abstract class for a Use Case (Interactor in terms of Clean Architecture).
 * This interface represents a execution unit for different use cases (this means any use case
 * in the application should implement this contract).
 *
 * By convention each UseCase implementation will return the result using a [DisposableSingleObserver]
 * that will execute its job in a background thread and will post the result in the UI thread.
 *
 * This use case is to be used when we expect a single value to be emitted via a [Single].
 */
abstract class SingleUseCase<Results, in Params>(
    private val threadExecutor: Scheduler,
    private val postExecutionThread: Scheduler
) {

    /**
     * Builds a [Single] which will be used when executing the current [SingleUseCase].
     */
    abstract fun buildUseCaseSingle(params: Params? = null): Single<Results>

    /**
     * Executes the current use case.
     *
     * @param params   Parameters (Optional) used to build/execute this use case.
     */
    fun execute(params: Params? = null): Single<Results> {
        return buildUseCaseSingle(params)
            .subscribeOn(threadExecutor)
            .observeOn(postExecutionThread)
    }
}