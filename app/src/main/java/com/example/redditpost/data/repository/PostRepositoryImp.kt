package com.example.redditpost.data.repository


import com.example.redditpost.data.datasource.PostLocalDataSource
import com.example.redditpost.data.datasource.PostRemoteDataSource
import com.example.redditpost.remote.model.DataX
import com.example.redditpost.remote.model.Post
import com.example.redditpost.domain.repository.PostRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class PostRepositoryImp @Inject constructor(
    private val postLocalDataSource: PostLocalDataSource,
    private val postRemoteDataSource: PostRemoteDataSource)
    : PostRepository {

    override fun getPost(t: String, limit: Int, after: String): Single<Post> {
        return postRemoteDataSource.getPost(t = t, limit = limit, after = after)
    }

    override fun getPostSearch(q: String, limit: Int, after: String?): Single<Post> {
        return postRemoteDataSource.getPostSearch(q = q, limit = limit, after = after)
    }

    override fun insertPost(post: DataX): Completable {
        return postLocalDataSource.insertPost(post = post)
    }

    override fun getAllPosts(): Flowable<List<DataX>> {
        return postLocalDataSource.getAllPosts()
    }

    override fun deletePost(post: DataX): Completable {
        return postLocalDataSource.deletePost(post = post)
    }

    override fun deleteAllPosts(): Completable {
        return postLocalDataSource.deleteAllPosts()
    }

}