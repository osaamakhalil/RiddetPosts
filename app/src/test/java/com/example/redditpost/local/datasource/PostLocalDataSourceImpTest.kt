package com.example.redditpost.local.datasource

import com.example.redditpost.factory.PostFactory
import com.example.redditpost.local.dao.PostDAO
import com.example.redditpost.remote.model.DataX
import com.nhaarman.mockitokotlin2.*
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import org.junit.Test


class PostLocalDataSourceImpTest {
    private val dao = mock<PostDAO>()
    private val postLocalDataSourceImp = PostLocalDataSourceImp(postDAO = dao)

    @Test
    fun `get All post calls dao`() {
        stubGetAllPost(Flowable.never())

        postLocalDataSourceImp.getAllPosts().test()
        verify(dao).getAllPosts()
    }

    @Test
    fun `get posts completes`() {
        val dataX = PostFactory.makeDataX()

        stubGetAllPost(Flowable.just(listOf(dataX)))

        postLocalDataSourceImp.getAllPosts().test()
            .assertComplete()
    }


    @Test
    fun `add post calls dao with the correct post`() {
        stubAddPost()
        val dataX = PostFactory.makeDataX()

        postLocalDataSourceImp.insertPost(post = dataX).test()
        verify(dao).insert(dataX)
    }

    @Test
    fun `add post completes`() {
        stubAddPost()
        val dataX = PostFactory.makeDataX()

        postLocalDataSourceImp.insertPost(post = dataX).test()
            .assertComplete()
    }

    @Test
    fun `delete post calls dao with the correct dataX`() {
        stubDeletePost()
        val dataX = PostFactory.makeDataX()

        postLocalDataSourceImp.deletePost(post = dataX).test()
        verify(dao).deletePost(dataX)
    }

    @Test
    fun `delete post completes`() {
        stubDeletePost()
        val dataX = PostFactory.makeDataX()

        postLocalDataSourceImp.deletePost(post = dataX).test()
            .assertComplete()
    }

    @Test
    fun `delete posts calls dao`() {
        stubDeletePosts()

        postLocalDataSourceImp.deleteAllPosts().test()
        verify(dao).deleteAllPosts()
        verifyNoMoreInteractions(dao)
    }

    @Test
    fun `delete posts completes`() {
        stubDeletePosts()

        postLocalDataSourceImp.deleteAllPosts().test()
            .assertComplete()
    }

    private fun stubGetAllPost(flowable: Flowable<List<DataX>>) {
        whenever(dao.getAllPosts())
            .thenReturn(flowable)
    }

    private fun stubAddPost() {
        whenever(dao.insert(any()))
            .thenReturn(Completable.complete())
    }

    private fun stubDeletePost() {
        whenever(dao.deletePost(any()))
            .thenReturn(Completable.complete())
    }

    private fun stubDeletePosts() {
        whenever(dao.deleteAllPosts())
            .thenReturn(Completable.complete())
    }

}