package com.example.sikrepmus.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "songs")
data class SongEntity(
    @PrimaryKey val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val contentUri: String,
    val albumArtUri: String?,
    val isFavorite: Boolean = false,
    val lastPlayed: Long = 0,
    val genre: String? = null,
    val dateAdded: Long = 0,
    val path: String = "",
    val playCount: Int = 0,
    val trackNumber: Int = 0,
    val discNumber: Int = 0,
    val year: Int = 0,
    val mimeType: String = ""
)
