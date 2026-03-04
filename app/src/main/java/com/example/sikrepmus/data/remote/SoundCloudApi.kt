package com.example.sikrepmus.data.remote

import com.example.sikrepmus.data.model.SoundCloudSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface SoundCloudApi {
    @GET("search/tracks")
    suspend fun searchTracks(
        @Query("q") query: String,
        @Query("client_id") clientId: String,
        @Query("limit") limit: Int = 25
    ): SoundCloudSearchResponse
}
