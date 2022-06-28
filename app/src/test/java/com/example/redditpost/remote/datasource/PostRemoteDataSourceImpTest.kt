package com.example.redditpost.remote.datasource

import com.example.redditpost.factory.PostFactory
import com.example.redditpost.remote.api.RedditApiService
import com.example.redditpost.remote.model.Post
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.rxjava3.core.Single
import org.junit.Test


class PostRemoteDataSourceImpTest {
    private val api = mock<RedditApiService>()
    private val postRemoteDataSourceImp = PostRemoteDataSourceImp(redditApiService = api)


    @Test
    fun `get post calls api with correct params`() {
        stubGetPost(Single.never())

        postRemoteDataSourceImp.getPost(t = "t", limit = 25, after = "")
        verify(api).getPosts(t = "t", limit = 25, after = "")
    }

    @Test
    fun `get post return data and completes`() {
        val post = PostFactory.makePost()

        stubGetPost(Single.just(post))

        val testObserver = postRemoteDataSourceImp.getPost(t = "t", limit = 2, after = "").test()
        testObserver.assertValue(post)
        testObserver.assertComplete()
    }

    @Test
    fun `get search post calls api with correct params`() {
        stubGetSearchPost(Single.never())

        postRemoteDataSourceImp.getPostSearch(q = "q", limit = 25, after = "")
        verify(api).getPostSearch(q = "q", limit = 25, after = "")
    }

    @Test
    fun `get search post return data and completes`() {
        val post = PostFactory.makePost()

        stubGetSearchPost(Single.just(post))

        val testObserver =
            postRemoteDataSourceImp.getPostSearch(q = "t", limit = 2, after = "").test()
        testObserver.assertValue(post)
        testObserver.assertComplete()
    }

    private fun stubGetPost(single: Single<Post>) {
        whenever(api.getPosts(any(), any(), any()))
            .thenReturn(single)
    }

    private fun stubGetSearchPost(single: Single<Post>) {
        whenever(api.getPostSearch(any(), any(), any()))
            .thenReturn(single)
    }

}