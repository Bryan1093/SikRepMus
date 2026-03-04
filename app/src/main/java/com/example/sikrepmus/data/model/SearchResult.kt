package com.example.sikrepmus.data.model

data class SearchResult(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Int,
    val coverUrl: String?,
    val previewUrl: String?
)
