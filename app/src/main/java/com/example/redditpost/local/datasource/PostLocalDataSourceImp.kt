package com.example.redditpost.local.datasource

import com.example.redditpost.data.datasource.PostLocalDataSource
import com.example.redditpost.local.dao.PostDAO
import com.example.redditpost.remote.model.DataX
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import javax.inject.Inject



class PostLocalDataSourceImp @Inject constructor(private val postDAO: PostDAO):
    PostLocalDataSource {

    override fun insertPost(post: DataX): Completable {
        return postDAO.insert(post = post)
    }

    override fun getAllPosts(): Flowable<List<DataX>> {
        return postDAO.getAllPosts()
    }

    override fun deletePost(post: DataX): Completable {
        return postDAO.deletePost(post = post)
    }

    override fun deleteAllPosts(): Completable {
        return postDAO.deleteAllPosts()
    }

}