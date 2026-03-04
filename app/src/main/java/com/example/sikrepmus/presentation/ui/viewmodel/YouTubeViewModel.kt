package com.example.sikrepmus.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sikrepmus.data.model.YouTubeSearchResult
import com.example.sikrepmus.data.repository.YouTubeRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class YouTubeViewModel : ViewModel() {
    private val repository = YouTubeRepository()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<YouTubeSearchResult>>(emptyList())
    val searchResults: StateFlow<List<YouTubeSearchResult>> = _searchResults.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        viewModelScope.launch {
            _searchQuery
                .debounce(400)
                .filter { it.isNotBlank() }
                .distinctUntilChanged()
                .collect { query ->
                    performSearch(query)
                }
        }
    }

    fun updateQuery(query: String) {
        _searchQuery.value = query
        if (query.isBlank()) {
            _searchResults.value = emptyList()
        }
    }

    private fun performSearch(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val results = repository.search(query)
                _searchResults.value = results

                if (results.isEmpty()) {
                    _errorMessage.value = "No se encontraron resultados"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
                _searchResults.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    suspend fun getStreamUrl(videoUrl: String): String? {
        return repository.getAudioStreamUrl(videoUrl)
    }
}