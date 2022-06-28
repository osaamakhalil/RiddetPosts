package com.example.redditpost.data.datasource

import com.example.redditpost.remote.model.DataX
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

interface PostLocalDataSource {
    fun insertPost(post: DataX): Completable

    fun getAllPosts(): Flowable<List<DataX>>

    fun deletePost(post: DataX): Completable

    fun deleteAllPosts(): Completable
}