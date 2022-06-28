package com.example.redditpost.remote.datasource

import com.example.redditpost.remote.api.RedditApiService
import com.example.redditpost.data.datasource.PostRemoteDataSource
import com.example.redditpost.remote.model.Post
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject


class PostRemoteDataSourceImp @Inject constructor(private val redditApiService: RedditApiService) : PostRemoteDataSource {

    override fun getPost(t: String, limit: Int, after: String): Single<Post> {
        return redditApiService.getPosts(t = t, limit = limit, after = after)
    }

    override fun getPostSearch(q: String, limit: Int, after: String?): Single<Post> {
        return redditApiService.getPostSearch(q = q, limit = limit, after = after)
    }

}