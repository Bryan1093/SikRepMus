package com.example.sikrepmus.data.repository

import com.example.sikrepmus.data.model.SearchResult
import com.example.sikrepmus.data.model.SoundCloudTrack
import com.example.sikrepmus.data.remote.SoundCloudApi
import com.example.sikrepmus.util.Constants

class SoundCloudRepository(private val api: SoundCloudApi) {

    suspend fun searchMusic(query: String, limit: Int = 25): Result<List<SearchResult>> {
        return try {
            val response = api.searchTracks(
                query = query,
                clientId = Constants.SOUNDCLOUD_CLIENT_ID,
                limit = limit
            )
            val results = response.collection.map { it.toSearchResult() }
            Result.success(results)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun SoundCloudTrack.toSearchResult() = SearchResult(
        id = id,
        title = title,
        artist = user.username,
        album = "",
        duration = duration / 1000,
        coverUrl = artwork_url,
        previewUrl = stream_url?.let { "$it?client_id=${Constants.SOUNDCLOUD_CLIENT_ID}" }
    )
}
