package com.example.sikrepmus.data.model

import android.net.Uri

data class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val contentUri: Uri,
    val albumArtUri: Uri? = null,
    val genre: String? = null,
    val dateAdded: Long = 0,
    val path: String = "",
    val playCount: Int = 0,
    val lastPlayed: Long = 0,
    val trackNumber: Int = 0,
    val discNumber: Int = 0,
    val year: Int = 0,
    val mimeType: String = ""
)
