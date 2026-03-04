package com.example.sikrepmus.ui.viewmodel

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.sikrepmus.data.local.dao.FolderDao
import com.example.sikrepmus.data.local.entities.FolderEntity
import com.example.sikrepmus.data.model.Song
import com.example.sikrepmus.data.repository.MusicRepository
import com.example.sikrepmus.playback.PlaybackService
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MusicViewModel(
    private val repository: MusicRepository,
    private val folderDao: FolderDao
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val songs: StateFlow<List<Song>> = repository.getSongsFlow()
        .combine(_searchQuery) { songs, query ->
            if (query.isBlank()) songs
            else songs.filter { 
                it.title.contains(query, ignoreCase = true) || 
                it.artist.contains(query, ignoreCase = true) ||
                it.album.contains(query, ignoreCase = true)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val folders: StateFlow<List<FolderEntity>> = folderDao.getAllFolders()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var mediaController: MediaController? = null
    
    var currentSong = mutableStateOf<Song?>(null)
    var isPlaying = mutableStateOf(false)
    var currentPosition = mutableLongStateOf(0L)
    var totalDuration = mutableLongStateOf(0L)
    
    // Shuffle and Repeat states
    var isShuffleModeEnabled = mutableStateOf(false)
    var repeatMode = mutableIntStateOf(Player.REPEAT_MODE_OFF)

    fun initController(context: Context) {
        val sessionToken = SessionToken(context, ComponentName(context, PlaybackService::class.java))
        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture?.addListener({
            mediaController = controllerFuture?.get()
            mediaController?.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlayingNow: Boolean) {
                    isPlaying.value = isPlayingNow
                    if (isPlayingNow) startProgressTracker()
                }
                
                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_READY) {
                        updateDuration()
                    }
                }

                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    val songId = mediaItem?.mediaId?.toLongOrNull()
                    val songFromList = songs.value.find { it.id == songId }
                    
                    if (songFromList != null) {
                        currentSong.value = songFromList
                    } else if (mediaItem != null) {
                        // For streams/YouTube items that aren't in the database
                        val metadata = mediaItem.mediaMetadata
                        currentSong.value = Song(
                            id = 0,
                            title = metadata.title?.toString() ?: "Unknown",
                            artist = metadata.artist?.toString() ?: "Unknown",
                            album = "Streaming",
                            duration = 0,
                            contentUri = mediaItem.localConfiguration?.uri ?: Uri.EMPTY,
                            albumArtUri = metadata.artworkUri
                        )
                    }
                    updateDuration()
                }
                
                override fun onPositionDiscontinuity(
                    oldPosition: Player.PositionInfo,
                    newPosition: Player.PositionInfo,
                    reason: Int
                ) {
                    updateDuration()
                }

                override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                    isShuffleModeEnabled.value = shuffleModeEnabled
                }

                override fun onRepeatModeChanged(newRepeatMode: Int) {
                    repeatMode.intValue = newRepeatMode
                }
            })
            // Sync initial state
            isShuffleModeEnabled.value = mediaController?.shuffleModeEnabled ?: false
            repeatMode.intValue = mediaController?.repeatMode ?: Player.REPEAT_MODE_OFF
        }, MoreExecutors.directExecutor())
    }

    private fun updateDuration() {
        val duration = mediaController?.duration ?: 0L
        if (duration > 0) {
            totalDuration.longValue = duration
        } else {
            currentSong.value?.let {
                totalDuration.longValue = it.duration
            }
        }
    }

    private fun startProgressTracker() {
        viewModelScope.launch {
            while (isPlaying.value) {
                currentPosition.longValue = mediaController?.currentPosition?.coerceAtLeast(0L) ?: 0L
                delay(1000)
            }
        }
    }

    fun toggleShuffle() {
        val controller = mediaController ?: return
        controller.shuffleModeEnabled = !controller.shuffleModeEnabled
    }

    fun toggleRepeatMode() {
        val controller = mediaController ?: return
        val nextMode = when (controller.repeatMode) {
            Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
            Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
            Player.REPEAT_MODE_ONE -> Player.REPEAT_MODE_OFF
            else -> Player.REPEAT_MODE_OFF
        }
        controller.repeatMode = nextMode
    }

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun playSong(song: Song) {
        val controller = mediaController ?: return
        
        val mediaItems = songs.value.map { 
            MediaItem.Builder()
                .setMediaId(it.id.toString())
                .setUri(it.contentUri)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(it.title)
                        .setArtist(it.artist)
                        .setAlbumTitle(it.album)
                        .setArtworkUri(it.albumArtUri)
                        .build()
                )
                .build()
        }
        
        controller.setMediaItems(mediaItems)
        val index = songs.value.indexOf(song)
        if (index != -1) {
            controller.seekTo(index, 0)
        }
        
        controller.prepare()
        controller.play()
        currentSong.value = song
        totalDuration.longValue = song.duration
    }

    fun togglePlayPause() {
        val controller = mediaController ?: return
        if (controller.isPlaying) controller.pause() else controller.play()
    }

    fun seekTo(position: Long) {
        mediaController?.seekTo(position)
    }

    fun playNext() {
        mediaController?.seekToNext()
    }

    fun playPrevious() {
        mediaController?.seekToPrevious()
    }

    fun addFolder(uri: Uri) {
        viewModelScope.launch {
            repository.addFolder(uri)
        }
    }

    fun removeFolder(folder: FolderEntity) {
        viewModelScope.launch {
            folderDao.deleteFolder(folder)
            repository.refreshSongs()
        }
    }

    fun refreshSongs() {
        viewModelScope.launch {
            repository.refreshSongs()
        }
    }

    override fun onCleared() {
        super.onCleared()
        controllerFuture?.let { MediaController.releaseFuture(it) }
    }

    class Factory(
        private val repository: MusicRepository,
        private val folderDao: FolderDao
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MusicViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MusicViewModel(repository, folderDao) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    fun playFromUrl(url: String, title: String, artist: String, albumArtUrl: String? = null) {
        viewModelScope.launch {
            try {
                mediaController?.stop()

                val artworkUri = albumArtUrl?.let { Uri.parse(it) }
                
                val mediaItem = MediaItem.Builder()
                    .setUri(url)
                    .setMediaMetadata(
                        MediaMetadata.Builder()
                            .setTitle(title)
                            .setArtist(artist)
                            .setArtworkUri(artworkUri)
                            .build()
                    )
                    .build()

                mediaController?.apply {
                    setMediaItem(mediaItem)
                    prepare()
                    play()
                }

                currentSong.value = Song(
                    id = 0,
                    title = title,
                    artist = artist,
                    album = "YouTube",
                    path = url,
                    duration = 0,
                    contentUri = Uri.parse(url),
                    albumArtUri = artworkUri
                )
                isPlaying.value = true

            } catch (e: Exception) {
                Log.e("MusicViewModel", "Error playing from URL: ${e.message}", e)
            }
        }
    }
}
