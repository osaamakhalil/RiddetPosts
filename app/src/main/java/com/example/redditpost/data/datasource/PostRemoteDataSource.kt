package com.example.redditpost.data.datasource

import com.example.redditpost.remote.model.Post
import io.reactivex.rxjava3.core.Single

interface PostRemoteDataSource {
    fun getPost(t: String, limit: Int, after: String): Single<Post>

    fun getPostSearch(q: String, limit: Int, after: String?): Single<Post>
}