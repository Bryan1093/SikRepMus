package com.example.sikrepmus.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.sikrepmus.data.model.SearchResult
import com.example.sikrepmus.data.model.Song
import com.example.sikrepmus.presentation.ui.search.SearchViewModel
import com.example.sikrepmus.ui.viewmodel.MusicViewModel
import com.example.sikrepmus.util.toFormattedDuration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: MusicViewModel,
    onSongClick: (Song) -> Unit,
    onMiniPlayerClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    searchViewModel: SearchViewModel = viewModel(factory = SearchViewModel.Factory())
) {
    val localQuery by viewModel.searchQuery.collectAsState()
    val songs by viewModel.songs.collectAsState()
    val currentSong by viewModel.currentSong
    val isPlaying by viewModel.isPlaying

    val deezerQuery by searchViewModel.searchQuery.collectAsState()
    val deezerResults by searchViewModel.searchResults.collectAsState()
    val isLoading by searchViewModel.isLoading.collectAsState()
    val error by searchViewModel.error.collectAsState()

    var isDeezerMode by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F0F))
            .padding(16.dp)
    ) {
        // Mode toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = !isDeezerMode,
                onClick = { isDeezerMode = false },
                label = { Text("Local") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF1DB954),
                    selectedLabelColor = Color.Black,
                    containerColor = Color.White.copy(alpha = 0.05f),
                    labelColor = Color.Gray
                )
            )
            FilterChip(
                selected = isDeezerMode,
                onClick = { isDeezerMode = true },
                label = { Text("Deezer") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF1DB954),
                    selectedLabelColor = Color.Black,
                    containerColor = Color.White.copy(alpha = 0.05f),
                    labelColor = Color.Gray
                )
            )
        }

        if (isDeezerMode) {
            // Deezer remote search
            TextField(
                value = deezerQuery,
                onValueChange = { searchViewModel.onQueryChange(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                placeholder = { Text("Busca en Deezer...", color = Color.Gray) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF1DB954)) },
                trailingIcon = {
                    if (deezerQuery.isNotEmpty()) {
                        IconButton(onClick = { searchViewModel.clearSearch() }) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = Color.Gray)
                        }
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White.copy(alpha = 0.05f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
                    disabledContainerColor = Color.White.copy(alpha = 0.05f),
                    cursorColor = Color(0xFF1DB954),
                    focusedIndicatorColor = Color(0xFF1DB954),
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Box(modifier = Modifier.weight(1f)) {
                when {
                    deezerQuery.isEmpty() -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Busca canciones en Deezer", color = Color.Gray)
                        }
                    }
                    isLoading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Color(0xFF1DB954))
                        }
                    }
                    error != null -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "Error: $error",
                                    color = Color.Red,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                Button(
                                    onClick = { searchViewModel.searchMusic(deezerQuery) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1DB954))
                                ) {
                                    Text("Reintentar", color = Color.Black)
                                }
                            }
                        }
                    }
                    deezerResults.isEmpty() -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                "No se encontraron resultados para \"$deezerQuery\"",
                                color = Color.Gray
                            )
                        }
                    }
                    else -> {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(deezerResults) { result ->
                                SearchResultCard(result = result)
                            }
                        }
                    }
                }

                if (currentSong != null) {
                    MiniPlayer(
                        song = currentSong!!,
                        isPlaying = isPlaying,
                        onPlayPauseClick = onPlayPauseClick,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 8.dp)
                            .clickable { onMiniPlayerClick() }
                    )
                }
            }
        } else {
            // Local search (existing behavior)
            TextField(
                value = localQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                placeholder = { Text("Busca canciones, artistas o álbumes...", color = Color.Gray) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF1DB954)) },
                trailingIcon = {
                    if (localQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = Color.Gray)
                        }
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White.copy(alpha = 0.05f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
                    disabledContainerColor = Color.White.copy(alpha = 0.05f),
                    cursorColor = Color(0xFF1DB954),
                    focusedIndicatorColor = Color(0xFF1DB954),
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Box(modifier = Modifier.weight(1f)) {
                if (songs.isEmpty() && localQuery.isNotEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No se encontraron resultados para \"$localQuery\"", color = Color.Gray)
                    }
                } else if (localQuery.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Empieza a escribir para buscar", color = Color.Gray)
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(songs) { song ->
                            SongItem(
                                song = song,
                                isSelected = song.id == currentSong?.id,
                                onClick = { onSongClick(song) }
                            )
                        }
                    }
                }

                if (currentSong != null) {
                    MiniPlayer(
                        song = currentSong!!,
                        isPlaying = isPlaying,
                        onPlayPauseClick = onPlayPauseClick,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 8.dp)
                            .clickable { onMiniPlayerClick() }
                    )
                }
            }
        }
    }
}

@Composable
fun SearchResultCard(result: SearchResult) {
    ListItem(
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        headlineContent = {
            Text(result.title, maxLines = 1, color = Color.White, fontWeight = FontWeight.Bold)
        },
        supportingContent = {
            Text(
                "${result.artist} • ${result.album}",
                maxLines = 1,
                color = Color.Gray,
                fontSize = 12.sp
            )
        },
        trailingContent = {
            Text(
                result.duration.toFormattedDuration(),
                color = Color.Gray,
                fontSize = 12.sp
            )
        },
        leadingContent = {
            AsyncImage(
                model = result.coverUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        }
    )
    HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
}
