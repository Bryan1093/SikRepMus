package com.example.sikrepmus.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Label
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
fun GenresScreen(
    songs: List<Song>,
    onGenreClick: (String) -> Unit,
    onBack: () -> Unit
) {
    val genres = songs.groupBy { it.genre ?: "Desconocido" }.keys.sorted()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Géneros", color = Color.White) },
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
        if (genres.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No se encontraron géneros.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize()
            ) {
                items(genres) { genre ->
                    ListItem(
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        headlineContent = { Text(genre, color = Color.White) },
                        leadingContent = { Icon(Icons.Default.Label, contentDescription = null, tint = Color(0xFF1DB954)) },
                        modifier = Modifier.clickable { onGenreClick(genre) }
                    )
                }
            }
        }
    }
}
