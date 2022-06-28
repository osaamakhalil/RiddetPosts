package com.example.redditpost.remote.model

import com.google.gson.annotations.SerializedName

data class Children(
    @SerializedName("data")
    val dataX: DataX,
)