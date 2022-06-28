package com.example.redditpost.di.modules

import com.example.redditpost.data.datasource.PostRemoteDataSource
import com.example.redditpost.remote.api.RedditApiService
import com.example.redditpost.remote.datasource.PostRemoteDataSourceImp
import dagger.Binds
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
abstract class RemoteModule {

    companion object {
        @Provides
        @Singleton
         fun provideRedditService(retrofit: Retrofit): RedditApiService {
            return retrofit.create(RedditApiService::class.java)
        }
    }

    @Binds
    abstract fun bindPostRemoteDataSource(dataSource: PostRemoteDataSourceImp): PostRemoteDataSource
}