package com.example.redditpost.di.modules

import com.example.redditpost.utils.scheduler.AppSchedulerProvider
import com.example.redditpost.utils.scheduler.SchedulerProvider
import dagger.Binds
import dagger.Module

@Module
abstract class SchedulersModule {
    @Binds
    abstract fun bindSchedulers(schedulers: AppSchedulerProvider): SchedulerProvider
}