package com.example.redditpost.utils.scheduler
import io.reactivex.rxjava3.core.Scheduler

/**
 * you can read this article to understand SchedulerProvider job its for simplicity
 * https://medium.com/@PaulinaSadowska/writing-unit-tests-on-asynchronous-events-with-rxjava-and-rxkotlin-1616a27f69aa
 */

interface SchedulerProvider {

    fun io(): Scheduler

    fun ui(): Scheduler

    fun computation(): Scheduler
}