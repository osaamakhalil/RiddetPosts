package com.example.redditpost.factory

import com.example.redditpost.remote.model.Children
import com.example.redditpost.remote.model.Data
import com.example.redditpost.remote.model.DataX
import com.example.redditpost.remote.model.Post
import com.example.redditpost.utils.DataFactory.randomBoolean
import com.example.redditpost.utils.DataFactory.randomInt
import com.example.redditpost.utils.DataFactory.randomString

object PostFactory {


    fun makeDataX(): DataX {
        return DataX(
            author = randomString(),
            id = randomString(),
            isVideo = randomBoolean(),
            thumbnail = randomString(),
            title = randomString(),
            commentCounts = randomInt(),
            totalAwardsReceived = randomInt()
        )
    }

    fun makeChildren(): List<Children> {
        return listOf(Children(dataX = makeDataX()))
    }

    fun makeData(): Data {
        return Data(
            after = randomString(),
            before = randomBoolean(),
            children = makeChildren(),
            dist = randomInt()
        )
    }

    fun makePost(): Post {
        return Post(data = makeData(), kind = randomString())
    }


}