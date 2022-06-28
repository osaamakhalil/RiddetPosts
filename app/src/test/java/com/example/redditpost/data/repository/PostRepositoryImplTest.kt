package com.example.redditpost.data.repository


import com.example.redditpost.data.datasource.PostLocalDataSource
import com.example.redditpost.data.datasource.PostRemoteDataSource
import com.example.redditpost.factory.PostFactory
import com.example.redditpost.remote.model.DataX
import com.example.redditpost.remote.model.Post
import com.nhaarman.mockitokotlin2.*
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import org.junit.Test


class PostRepositoryImplTest {

    private val remoteDataSource = mock<PostRemoteDataSource>()
    private val localDataSource = mock<PostLocalDataSource>()
    private val repository = PostRepositoryImp(
        postRemoteDataSource = remoteDataSource,
        postLocalDataSource = localDataSource
    )

    @Test
    fun `get post calls remote data source with the correct params`() {
        stubGetPost(Single.never())

        repository.getPost(t = "t", limit = 25, after = "").test()
        verify(remoteDataSource).getPost(
            t = "t",
            limit = 25,
            after = ""
        )
    }

    @Test
    fun `get post completes`() {
        val post = PostFactory.makePost()
        stubGetPost(Single.just(post))

        val testObserver = repository.getPost(t = "t", limit = 25, after = "").test()
        testObserver.assertValue(post)
        testObserver.assertComplete()
    }


    @Test
    fun `get post search calls remote data source with the correct params`() {
        stubGetPostSearch(Single.never())

        repository.getPostSearch(q = "osama", limit = 25, after = "").test()
        verify(remoteDataSource).getPostSearch(
            q = "osama",
            limit = 25,
            after = ""
        )
    }

    @Test
    fun `get post search completes`() {
        val post = PostFactory.makePost()
        stubGetPostSearch(Single.just(post))

        val testObserver = repository.getPostSearch(q = "osama", limit = 25, after = "").test()
        testObserver.assertValue(post)
        testObserver.assertComplete()

    }

    @Test
    fun `add post calls local data source with the correct params`() {
        stubAddPost()

        val post = PostFactory.makeDataX()

        repository.insertPost(post).test()
        verify(localDataSource).insertPost(post)
    }

    @Test
    fun `add post completes`() {
        stubAddPost()

        val post = PostFactory.makeDataX()

        repository.insertPost(post).test()
            .assertComplete()
    }

    @Test
    fun `get all posts calls local data source`() {
        stubGetAllPost(Flowable.never())

        repository.getAllPosts().test()
        verify(localDataSource).getAllPosts()
        verifyNoMoreInteractions(localDataSource)
    }

    @Test
    fun `get all posts completes`() {
        val post = PostFactory.makeDataX()

        stubGetAllPost(Flowable.just(listOf(post)))

        repository.getAllPosts().test()
            .assertComplete()
    }


    @Test
    fun `delete post calls local data source with the correct params`() {
        stubDeletePost()

        val post = PostFactory.makeDataX()

        repository.deletePost(post).test()
        verify(localDataSource).deletePost(post)
    }

    @Test
    fun `delete post completes`() {
        stubDeletePost()

        val post = PostFactory.makeDataX()

        repository.deletePost(post).test()
            .assertComplete()
    }

    @Test
    fun `delete posts calls local data source with the correct params`() {
        stubDeletePosts()

        repository.deleteAllPosts().test()
        verify(localDataSource).deleteAllPosts()
    }

    @Test
    fun `delete posts completes`() {
        stubDeletePosts()

        repository.deleteAllPosts().test()
            .assertComplete()
    }


    private fun stubGetPost(single: Single<Post>) {
        whenever(remoteDataSource.getPost(any(), any(), any()))
            .thenReturn(single)
    }

    private fun stubGetPostSearch(single: Single<Post>) {
        whenever(remoteDataSource.getPostSearch(any(), any(), any()))
            .thenReturn(single)
    }

    private fun stubAddPost() {
        whenever(localDataSource.insertPost(any()))
            .thenReturn(Completable.complete())
    }

    private fun stubGetAllPost(flowable: Flowable<List<DataX>>) {
        whenever(localDataSource.getAllPosts())
            .thenReturn(flowable)
    }

    private fun stubDeletePost() {
        whenever(localDataSource.deletePost(any()))
            .thenReturn(Completable.complete())
    }

    private fun stubDeletePosts() {
        whenever(localDataSource.deleteAllPosts())
            .thenReturn(Completable.complete())
    }


}