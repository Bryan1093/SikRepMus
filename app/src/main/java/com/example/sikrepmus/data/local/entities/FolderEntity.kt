package com.example.sikrepmus.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "music_folders")
data class FolderEntity(
    @PrimaryKey val uri: String,
    val path: String? = null
)
