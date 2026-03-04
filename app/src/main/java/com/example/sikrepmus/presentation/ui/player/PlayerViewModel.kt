package com.example.sikrepmus.presentation.ui.player

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.compose.runtime.currentCompositionLocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sikrepmus.data.model.SearchResult
import com.example.sikrepmus.service.MusicPlayerService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlayerViewModel : ViewModel() {

    private var musicService: MusicPlayerService? = null
    private var bound = false

    private val _currentSong = MutableStateFlow<SearchResult?>(null)
    val currentSong: StateFlow<SearchResult?> = _currentSong.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(30000L) // 30 segundos preview
    val duration: StateFlow<Long> = _duration.asStateFlow()

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicPlayerService.MusicBinder
            musicService = binder.getService()
            bound = true
            startPositionUpdater()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            musicService = null
            bound = false
        }
    }

    fun bindService(context: Context) {
        Intent(context, MusicPlayerService::class.java).also { intent ->
            context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    fun unbindService(context: Context) {
        if (bound) {
            context.unbindService(connection)
            bound = false
        }
    }

    fun playPreview(song: SearchResult) {
        _currentSong.value = song
        song.previewUrl?.let { url ->
            musicService?.playPreview(url)
            _isPlaying.value= true
        }
    }

    fun togglePlayPause() {
        if (_isPlaying.value) {
            musicService?.pause()
            _isPlaying.value = false
        } else {
            musicService?.resume()
            _isPlaying.value = true
        }
    }

    fun stop() {
        musicService?.stop()
        _isPlaying.value = false
        _currentSong.value = null
        _currentPosition.value = 0L
    }

    fun seekTo(position: Long) {
        musicService?.seekTo(position)
        _currentPosition.value = position
    }

    private fun startPositionUpdater() {
        viewModelScope.launch {
            while (true) {
                if (_isPlaying.value) {
                    val position = musicService?.getCurrentPosition() ?: 0L
                    _currentPosition.value = position

                    val dur = musicService?.getDuration() ?: 0L
                    if (dur > 0) {
                        _duration.value = dur
                    }
                }
                delay(100) // Update every 100ms
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        musicService?.stop()
    }
}