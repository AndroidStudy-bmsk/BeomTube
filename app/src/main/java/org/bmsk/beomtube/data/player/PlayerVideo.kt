package org.bmsk.beomtube.data.player

import org.bmsk.beomtube.data.VideoEntity

data class PlayerVideo(
    override val id: String,
    val title: String,
    val sources: List<String>,
    val channelName: String,
    val videoThumb: String,
    val channelThumb: String,
    val viewCount: Long,
    val date: String,
): PlayerVideoModel

fun VideoEntity.transform(): PlayerVideo {
    return PlayerVideo(
        id = id,
        title = title,
        sources = sources,
        channelName = channelName,
        videoThumb = videoThumb,
        channelThumb = channelThumb,
        viewCount = viewCount,
        date = date
    )
}