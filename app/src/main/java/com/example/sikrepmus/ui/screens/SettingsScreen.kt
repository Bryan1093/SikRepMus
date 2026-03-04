package com.example.sikrepmus.ui.screens

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sikrepmus.data.local.entities.FolderEntity
import com.example.sikrepmus.ui.viewmodel.MusicViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: MusicViewModel,
    onAddFolderClick: () -> Unit
) {
    // Necesitaremos exponer las carpetas en el ViewModel. Por ahora, asumamos que las tenemos.
    // Voy a actualizar el ViewModel para exponer las carpetas.
    val folders by viewModel.folders.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ajustes de Biblioteca", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        },
        containerColor = Color(0xFF0F0F0F),
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddFolderClick,
                containerColor = Color(0xFF1DB954),
                contentColor = Color.Black
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Carpeta")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                "Carpetas de música",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                "Solo se escaneará la música dentro de estas carpetas.",
                color = Color.Gray,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (folders.isEmpty()) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text("No has seleccionado ninguna carpeta.", color = Color.Gray)
                }
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(folders) { folder ->
                        FolderItem(
                            folder = folder,
                            onDelete = { viewModel.removeFolder(folder) }
                        )
                    }
                }
            }

            Button(
                onClick = { viewModel.refreshSongs() },
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1DB954))
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Escanear ahora", color = Color.Black)
            }
        }
    }
}

@Composable
fun FolderItem(folder: FolderEntity, onDelete: () -> Unit) {
    val folderName = folder.uri.split("%3A").lastOrNull()?.replace("%2F", "/") ?: folder.uri
    
    ListItem(
        colors = ListItemDefaults.colors(containerColor = Color.White.copy(alpha = 0.05f)),
        headlineContent = { Text(folderName, color = Color.White, maxLines = 1) },
        leadingContent = { Icon(Icons.Default.Folder, contentDescription = null, tint = Color(0xFF1DB954)) },
        trailingContent = {
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red.copy(alpha = 0.7f))
            }
        },
        modifier = Modifier.padding(vertical = 4.dp)
    )
}
