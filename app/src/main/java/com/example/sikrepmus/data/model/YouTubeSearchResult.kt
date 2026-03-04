package com.example.sikrepmus.data.model

data class YouTubeSearchResult(
    val id: String,
    val title: String,
    val artist: String,
    val thumbnailUrl: String?,
    val duration: Long, // en segundos
    val viewCount: Long = 0
)