package org.bmsk.beomtube.data.player

data class PlayerHeader(
    override val id: String,
    val title: String,
    val channelName: String,
    val channelThumb: String,
    val viewCount: Long,
    val date: String,
): PlayerVideoModel