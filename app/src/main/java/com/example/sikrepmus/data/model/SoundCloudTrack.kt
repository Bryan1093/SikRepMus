package com.example.sikrepmus.data.model

data class SoundCloudTrack(
    val id: Long,
    val title: String,
    val user: SoundCloudUser,
    val duration: Int,
    val artwork_url: String?,
    val stream_url: String?,
    val permalink_url: String
)

data class SoundCloudUser(
    val username: String
)

data class SoundCloudSearchResponse(
    val collection: List<SoundCloudTrack>
)
