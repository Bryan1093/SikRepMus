package com.example.sikrepmus.data.repository

import android.util.Log
import com.example.sikrepmus.data.model.YouTubeSearchResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.schabi.newpipe.extractor.InfoItem
import org.schabi.newpipe.extractor.ServiceList
import org.schabi.newpipe.extractor.stream.StreamInfo
import org.schabi.newpipe.extractor.stream.StreamInfoItem

class YouTubeRepository {

    suspend fun search(query: String): List<YouTubeSearchResult> = withContext(Dispatchers.IO) {
        try {
            val service = ServiceList.YouTube
            val extractor = service.getSearchExtractor(query)
            extractor.fetchPage()

            extractor.initialPage.items
                .filterIsInstance<StreamInfoItem>()
                .take(20) // Limitar a 20 resultados
                .map { item ->
                    YouTubeSearchResult(
                        id = item.url,
                        title = item.name,
                        artist = item.uploaderName ?: "Unknown",
                        thumbnailUrl = item.thumbnails.firstOrNull()?.url,
                        duration = item.duration,
                        viewCount = item.viewCount
                    )
                }
        } catch (e: Exception) {
            Log.e("YouTubeRepository", "Error searching: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun getAudioStreamUrl(videoUrl: String): String? = withContext(Dispatchers.IO) {
        try {
            val streamInfo = StreamInfo.getInfo(ServiceList.YouTube, videoUrl)

            // Buscar mejor stream de audio
            val audioStream = streamInfo.audioStreams
                .maxByOrNull { it.averageBitrate }

            audioStream?.url
        } catch (e: Exception) {
            Log.e("YouTubeRepository", "Error getting stream: ${e.message}", e)
            null
        }
    }
}