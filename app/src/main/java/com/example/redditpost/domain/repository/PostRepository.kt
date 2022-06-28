package com.example.redditpost.domain.repository

import com.example.redditpost.remote.model.DataX
import com.example.redditpost.remote.model.Post
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single


interface PostRepository {
    fun getPost(t: String, limit: Int, after: String): Single<Post>

    fun getPostSearch(q: String, limit: Int, after: String?): Single<Post>

    fun insertPost(post: DataX): Completable

    fun getAllPosts(): Flowable<List<DataX>>

    fun deletePost(post: DataX): Completable

    fun deleteAllPosts(): Completable
}