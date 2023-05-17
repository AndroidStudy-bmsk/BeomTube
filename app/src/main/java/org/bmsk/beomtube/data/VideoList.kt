package org.bmsk.beomtube.data

import com.google.gson.annotations.SerializedName

data class VideoList(
    @SerializedName("videos")
    val videos: List<VideoItem>
)

data class VideoItem(
    @SerializedName("id")
    val id: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("sources")
    val sources: List<String>,
    @SerializedName("subtitle")
    val channelName: String,
    @SerializedName("thumb")
    val videoThumb: String,
    @SerializedName("channelThumb")
    val channelThumb: String,
    @SerializedName("viewCount")
    val viewCount: Long,
    @SerializedName("date")
    val date: String,
)
