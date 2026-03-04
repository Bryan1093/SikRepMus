package com.example.sikrepmus.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.sikrepmus.data.model.Song
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoldersScreen(
    songs: List<Song>,
    onFolderClick: (String) -> Unit,
    onBack: () -> Unit
) {
    val folders = songs.groupBy { 
        val file = File(it.path)
        file.parentFile?.name ?: "Raíz"
    }.keys.sorted()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Carpetas", color = Color.White) },
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
        if (folders.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No se encontraron carpetas.", color = Color.Gray)
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding).fillMaxSize()) {
                items(folders) { folderName ->
                    ListItem(
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        headlineContent = { Text(folderName, color = Color.White) },
                        leadingContent = { Icon(Icons.Default.Folder, contentDescription = null, tint = Color(0xFF1DB954)) },
                        modifier = Modifier.clickable { onFolderClick(folderName) }
                    )
                }
            }
        }
    }
}
