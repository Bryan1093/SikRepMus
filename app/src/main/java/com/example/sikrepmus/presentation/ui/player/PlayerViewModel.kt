package com.example.sikrepmus.presentation.ui.player

import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PlayerViewModel(private val exoPlayer: ExoPlayer) : ViewModel() {

    private val _currentSong = MutableStateFlow<Any?>(null)
    val currentSong: StateFlow<Any?> = _currentSong.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration.asStateFlow()

    init {
        exoPlayer.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                _isPlaying.value = playing
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    _duration.value = exoPlayer.duration
                }
            }
        })
    }

    fun playPreview(url: String) {
        exoPlayer.apply {
            setMediaItem(MediaItem.fromUri(url))
            prepare()
            play()
        }
    }

    fun togglePlayPause() {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
        } else {
            exoPlayer.play()
        }
    }

    fun stop() {
        exoPlayer.stop()
        exoPlayer.clearMediaItems()
        _currentPosition.value = 0L
    }

    fun seekTo(position: Long) {
        exoPlayer.seekTo(position)
    }

    override fun onCleared() {
        super.onCleared()
        exoPlayer.release()
    }
}