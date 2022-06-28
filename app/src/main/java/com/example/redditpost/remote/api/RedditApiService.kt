package com.example.redditpost.remote.api

import com.example.redditpost.remote.model.Post
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface RedditApiService {

    @GET("/r/aww/top.json")
    fun getPosts(
        @Query("t") t: String,
        @Query("limit") limit: Int,
        @Query("after") after: String
    ): Single<Post>

    @GET("/r/aww/search.json")
    fun getPostSearch(
        @Query("q") q: String,
        @Query("limit") limit: Int,
        @Query("after") after: String?,
        ): Single<Post>
}