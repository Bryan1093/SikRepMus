package com.example.sikrepmus.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sikrepmus.data.model.Song

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistsScreen(
    songs: List<Song>,
    onArtistClick: (String) -> Unit,
    onBack: () -> Unit
) {
    val artists = songs.groupBy { it.artist }.keys.sorted()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Artistas", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        },
        containerColor = Color(0xFF0F0F0F)
    ) { padding ->
        if (artists.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No se encontraron artistas.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize()
            ) {
                items(artists) { artist ->
                    ListItem(
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        headlineContent = { Text(artist, color = Color.White) },
                        leadingContent = { Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF1DB954)) },
                        modifier = Modifier.clickable { onArtistClick(artist) }
                    )
                }
            }
        }
    }
}
