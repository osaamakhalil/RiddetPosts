package com.example.redditpost.utils

import java.util.*
import java.util.concurrent.ThreadLocalRandom

/**
 * this object use in factory to make it readable and avoid boilerplate
 * exist on capiter app in "com.capiter.core.testing.factory"
 */

object DataFactory {
    fun randomString(): String {
        return UUID.randomUUID().toString()
    }

    fun randomInt(): Int {
        return ThreadLocalRandom.current().nextInt(0, 1000 + 1)
    }

    fun randomLong(): Long {
        return randomInt().toLong()
    }

    fun randomFloat(): Float {
        return randomInt().toFloat()
    }

    fun randomBoolean(): Boolean {
        return Math.random() < 0.5
    }

    fun randomDate(): Date {
        return Date()
    }

    fun randomDateInMilliSeconds(): Long {
        return randomDate().time
    }

}