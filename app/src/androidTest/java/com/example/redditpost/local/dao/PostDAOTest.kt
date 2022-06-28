package com.example.redditpost.local.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.redditpost.local.PostDatabase
import com.example.redditpost.remote.model.DataX
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class PostDAOTest {

    @get:Rule
    //it's just for Junit to execute tasks synchronously
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: PostDatabase
    private lateinit var dao: PostDAO

    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            PostDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.getPostDao()
    }

    @After
    fun clodDb() {
        database.close()
    }


    @Test
    fun insertPost() {
        val post = DataX(
            author = "osama",
            id = "1",
            isVideo = true,
            thumbnail = "url",
            title = "post",
            commentCounts = 140,
            totalAwardsReceived = 14
        )
        dao.insert(post).blockingAwait()
        dao.getAllPosts().test()
            .assertValue{list-> list.isNotEmpty()
        }
    }

    @Test
    fun deletePost() {
        val post = DataX(
            author = "osama",
            id = "1",
            isVideo = true,
            thumbnail = "url",
            title = "post",
            commentCounts = 140,
            totalAwardsReceived = 14
        )
        dao.insert(post).blockingAwait()
        dao.deletePost(post).blockingAwait()

        dao.getAllPosts().test()
            .assertValue{list -> list.isEmpty()}
    }

    @Test
    fun deleteAllPost() {
        val post = DataX(
            author = "osama",
            id = "1",
            isVideo = true,
            thumbnail = "url",
            title = "post",
            commentCounts = 140,
            totalAwardsReceived = 14
        )
        val post1 = DataX(
            author = "khalil",
            id = "2",
            isVideo = true,
            thumbnail = "url",
            title = "post",
            commentCounts = 140,
            totalAwardsReceived = 14
        )
        dao.insert(post).blockingAwait()
        dao.insert(post1).blockingAwait()

        dao.deleteAllPosts().blockingAwait()
        dao.getAllPosts().test()
            .assertValue{list -> list.isEmpty()}

    }

}