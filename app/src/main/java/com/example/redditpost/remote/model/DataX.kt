package com.example.redditpost.remote.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "posts")
data class DataX(
    val author: String,
    @PrimaryKey
    val id: String,
    @SerializedName("is_video")
    val isVideo: Boolean,
    val thumbnail: String,
    val title: String,
    @SerializedName("num_comments")
    val commentCounts:Int,
    @SerializedName("total_awards_received")
    val totalAwardsReceived: Int,
)