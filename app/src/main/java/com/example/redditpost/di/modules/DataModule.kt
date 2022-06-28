package com.example.redditpost.di.modules

import com.example.redditpost.data.repository.PostRepositoryImp
import com.example.redditpost.domain.repository.PostRepository
import dagger.Binds
import dagger.Module

@Module
abstract class DataModule {
    @Binds
    abstract fun bindPostRepository(repository: PostRepositoryImp): PostRepository
}