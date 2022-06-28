package com.example.redditpost.remote.model

data class Data(
    val after: String,
    val before: Any,
    val children: List<Children>,
    val dist: Int,
)