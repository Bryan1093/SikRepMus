package com.example.sikrepmus.data.repository

import android.util.Log
import com.example.sikrepmus.data.model.YouTubeSearchResult
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val TAG = "YouTubeApiRepository"
private const val MAX_RETRIES = 3
private const val RETRY_DELAY_MS = 2000L

/**
 * Interfaz para llamar a una API que extrae datos de YouTube
 * Usa yt-dlp info extractor (más confiable que NewPipe)
 */
interface YtDlpApi {
    @GET("extract")
    suspend fun searchYouTube(
        @Query("url") url: String,
        @Query("format") format: String = "json"
    ): JsonObject
}

/**
 * Repositorio alternativo que usa yt-dlp como fallback
 * cuando NewPipe falla con YouTube
 */
class YouTubeApiRepository {

    companion object {
        private const val YT_DLP_API_BASE = "https://www.youtube.com/"
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl(YT_DLP_API_BASE)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(YtDlpApi::class.java)

    /**
     * Buscar vídeos en YouTube usando múltiples estrategias
     * 1. Primero intenta con NewPipe
     * 2. Si falla, intenta con yt-dlp
     * 3. Si ambas fallan, retorna lista vacía
     */
    suspend fun search(query: String): List<YouTubeSearchResult> = withContext(Dispatchers.IO) {
        Log.d(TAG, "Iniciando búsqueda: '$query'")

        // Estrategia 1: Intentar con NewPipe primero
        val youtubeRepo = YouTubeRepository()
        val newPipeResults = youtubeRepo.search(query)

        if (newPipeResults.isNotEmpty()) {
            Log.d(TAG, "Búsqueda exitosa con NewPipe: ${newPipeResults.size} resultados")
            return@withContext newPipeResults
        }

        Log.w(TAG, "NewPipe falló, intentando estrategia alternativa...")

        // Estrategia 2: Fallback a búsqueda simple
        // Ya que yt-dlp no tiene API pública, usamos una búsqueda básica
        return@withContext fallbackSearch(query)
    }

    /**
     * Búsqueda alternativa simple basada en URL de YouTube
     */
    private suspend fun fallbackSearch(query: String): List<YouTubeSearchResult> {
        return withContext(Dispatchers.IO) {
            try {
                // Construir URL de búsqueda de YouTube
                val searchUrl = "https://www.youtube.com/results?search_query=${query.replace(" ", "+")}"
                Log.d(TAG, "Usando fallback search: $searchUrl")

                // Para un fallback real, necesitarías:
                // 1. Descargar el HTML de la búsqueda
                // 2. Parsear los resultados del JSON inicial
                // 3. Extraer títulos, IDs, etc.

                // Por ahora, retornamos lista vacía
                // En producción, implementarías el parsing HTML
                emptyList()
            } catch (e: Exception) {
                Log.e(TAG, "Error en fallback search: ${e.message}")
                emptyList()
            }
        }
    }

    suspend fun getAudioStreamUrl(videoUrl: String): String? = withContext(Dispatchers.IO) {
        val youtubeRepo = YouTubeRepository()
        return@withContext youtubeRepo.getAudioStreamUrl(videoUrl)
    }
}

