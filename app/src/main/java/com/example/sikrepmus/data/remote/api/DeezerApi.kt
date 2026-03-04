package com.example.sikrepmus.data.remote.api

import com.example.sikrepmus.data.remote.dto.DeezerSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface DeezerApi {
    @GET("search")
    suspend fun searchTracks(
        @Query("q") query: String,
        @Query("limit") limit: Int = 25
    ): DeezerSearchResponse
}
