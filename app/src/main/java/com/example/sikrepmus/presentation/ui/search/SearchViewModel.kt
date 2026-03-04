package com.example.sikrepmus.presentation.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.sikrepmus.data.model.SearchResult
import com.example.sikrepmus.data.remote.RetrofitClient
import com.example.sikrepmus.data.repository.DeezerRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    private val repository: DeezerRepository = DeezerRepository(RetrofitClient.deezerApi)
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<SearchResult>>(emptyList())
    val searchResults: StateFlow<List<SearchResult>> = _searchResults.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // ✨ CAMBIO: Ahora se llama errorMessage
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private var searchJob: Job? = null

    fun onQueryChange(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            _errorMessage.value = null
            return
        }
        searchJob = viewModelScope.launch {
            delay(400)
            searchMusic(query)
        }
    }

    fun searchMusic(query: String) {
        if (query.isBlank()) return
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            repository.searchMusic(query).fold(
                onSuccess = { results ->
                    _searchResults.value = results
                    _isLoading.value = false
                },
                onFailure = { throwable ->
                    _errorMessage.value = throwable.message ?: "Error desconocido"
                    _isLoading.value = false
                }
            )
        }
    }

    fun clearSearch() {
        searchJob?.cancel()
        _searchQuery.value = ""
        _searchResults.value = emptyList()
        _errorMessage.value = null
    }

    class Factory : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SearchViewModel() as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}