package com.example.redditpost.local.dao

import androidx.room.*
import com.example.redditpost.remote.model.DataX
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable


@Dao
interface PostDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(post: DataX): Completable

    @Query("SELECT * FROM posts")
    fun getAllPosts(): Flowable<List<DataX>>

    @Delete
    fun deletePost(post: DataX): Completable

    @Query("DELETE FROM posts")
    fun deleteAllPosts(): Completable

}