package com.example.sikrepmus.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sikrepmus.data.model.Song
import java.io.File

@Composable
fun LibraryScreen(
    songs: List<Song>,
    currentSong: Song?,
    isPlaying: Boolean,
    onSongClick: (Song) -> Unit,
    onMiniPlayerClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    initialSubScreen: String = "Main",
    initialFilterType: String? = null,
    initialFilterValue: String? = null
) {
    var currentSubScreen by remember(initialSubScreen) { mutableStateOf(initialSubScreen) }
    var filterType by remember(initialFilterType) { mutableStateOf(initialFilterType) }
    var filterValue by remember(initialFilterValue) { mutableStateOf(initialFilterValue) }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0F0F0F))) {
        when (currentSubScreen) {
            "Main" -> {
                LibraryMenu(onCategoryClick = { currentSubScreen = it })
            }
            "Todas las canciones" -> {
                MusicListScreen(
                    songs = songs,
                    currentSong = currentSong,
                    isPlaying = isPlaying,
                    onSongClick = onSongClick,
                    onMiniPlayerClick = onMiniPlayerClick,
                    onPlayPauseClick = onPlayPauseClick,
                    onBack = { currentSubScreen = "Main" }
                )
            }
            "Álbumes" -> {
                AlbumsScreen(
                    songs = songs,
                    onAlbumClick = { 
                        filterType = "Album"
                        filterValue = it
                        currentSubScreen = "FilteredList"
                    },
                    onBack = { currentSubScreen = "Main" }
                )
            }
            "Artistas" -> {
                ArtistsScreen(
                    songs = songs,
                    onArtistClick = { 
                        filterType = "Artist"
                        filterValue = it
                        currentSubScreen = "FilteredList"
                    },
                    onBack = { currentSubScreen = "Main" }
                )
            }
            "Carpetas" -> {
                FoldersScreen(
                    songs = songs,
                    onFolderClick = { 
                        filterType = "Folder"
                        filterValue = it
                        currentSubScreen = "FilteredList"
                    },
                    onBack = { currentSubScreen = "Main" }
                )
            }
            "Géneros" -> {
                GenresScreen(
                    songs = songs,
                    onGenreClick = { 
                        filterType = "Genre"
                        filterValue = it
                        currentSubScreen = "FilteredList"
                    },
                    onBack = { currentSubScreen = "Main" }
                )
            }
            "FilteredList" -> {
                val filteredSongs = when (filterType) {
                    "Album" -> songs.filter { it.album == filterValue }
                    "Artist" -> songs.filter { it.artist == filterValue }
                    "Folder" -> songs.filter { File(it.path).parentFile?.name == filterValue }
                    "Genre" -> songs.filter { it.genre == filterValue }
                    else -> songs
                }
                MusicListScreen(
                    songs = filteredSongs,
                    currentSong = currentSong,
                    isPlaying = isPlaying,
                    onSongClick = onSongClick,
                    onMiniPlayerClick = onMiniPlayerClick,
                    onPlayPauseClick = onPlayPauseClick,
                    onBack = { 
                        currentSubScreen = when(filterType) {
                            "Album" -> "Álbumes"
                            "Artist" -> "Artistas"
                            "Folder" -> "Carpetas"
                            "Genre" -> "Géneros"
                            else -> "Main"
                        }
                    }
                )
            }
            "Agregado recientemente" -> {
                val recentSongs = songs.sortedByDescending { it.dateAdded }
                MusicListScreen(
                    songs = recentSongs,
                    currentSong = currentSong,
                    isPlaying = isPlaying,
                    onSongClick = onSongClick,
                    onMiniPlayerClick = onMiniPlayerClick,
                    onPlayPauseClick = onPlayPauseClick,
                    onBack = { currentSubScreen = "Main" }
                )
            }
            else -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("$currentSubScreen Próximamente", color = Color.Gray)
                        Button(onClick = { currentSubScreen = "Main" }) {
                            Text("Volver")
                        }
                    }
                }
            }
        }
        
        if (currentSubScreen == "Main" && currentSong != null) {
            MiniPlayer(
                song = currentSong,
                isPlaying = isPlaying,
                onPlayPauseClick = onPlayPauseClick,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .clickable { onMiniPlayerClick() }
            )
        }
    }
}

@Composable
fun LibraryMenu(onCategoryClick: (String) -> Unit) {
    val categories = listOf(
        "Todas las canciones" to Icons.Default.MusicNote,
        "Carpetas" to Icons.Default.Folder,
        "Álbumes" to Icons.Default.Album,
        "Artistas" to Icons.Default.Person,
        "Géneros" to Icons.Default.Label,
        "Listas de reproducción" to Icons.Default.PlaylistPlay,
        "Cola" to Icons.Default.QueueMusic,
        "Agregado recientemente" to Icons.Default.History
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            Text(
                "Biblioteca",
                color = Color.White,
                fontSize = 32.sp,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }
        items(categories) { (name, icon) ->
            CategoryItem(name = name, icon = icon, onClick = { onCategoryClick(name) })
        }
    }
}

@Composable
fun CategoryItem(name: String, icon: ImageVector, onClick: () -> Unit) {
    ListItem(
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        headlineContent = { Text(name, color = Color.White) },
        leadingContent = { Icon(icon, contentDescription = null, tint = Color(0xFF1DB954)) },
        modifier = Modifier.clickable { onClick() }
    )
}
