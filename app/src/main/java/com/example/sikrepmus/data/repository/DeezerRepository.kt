package com.example.sikrepmus.data.repository

import com.example.sikrepmus.data.model.SearchResult
import com.example.sikrepmus.data.remote.api.DeezerApi
import com.example.sikrepmus.data.remote.dto.DeezerTrack

class DeezerRepository(private val api: DeezerApi) {

    suspend fun searchMusic(query: String, limit: Int = 25): Result<List<SearchResult>> {
        return try {
            val response = api.searchTracks(query = query, limit = limit)
            val results = response.data.map { it.toSearchResult() }
            Result.success(results)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun DeezerTrack.toSearchResult() = SearchResult(
        id = id,
        title = title,
        artist = artist.name,
        album = album.title,
        duration = duration,
        coverUrl = album.coverMedium,
        previewUrl = preview
    )
}
