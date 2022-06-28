package com.example.redditpost.di.modules

import com.example.redditpost.data.datasource.PostLocalDataSource
import com.example.redditpost.local.dao.PostDAO
import com.example.redditpost.local.PostDatabase
import com.example.redditpost.local.datasource.PostLocalDataSourceImp
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
 abstract class LocalModule {


    companion object {
        @Provides
        @Singleton
        fun getPostDao(postDatabase: PostDatabase): PostDAO {
            return postDatabase.getPostDao()
        }
    }

    @Binds
    abstract fun bindPostLocalDataSource(dataSource: PostLocalDataSourceImp): PostLocalDataSource
}