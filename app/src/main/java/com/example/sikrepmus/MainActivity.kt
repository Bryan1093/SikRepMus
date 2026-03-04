package com.example.sikrepmus

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sikrepmus.data.local.AppDatabase
import com.example.sikrepmus.data.repository.MusicRepository
import com.example.sikrepmus.ui.theme.SikRepMusTheme
import com.example.sikrepmus.ui.viewmodel.MusicViewModel
import com.example.sikrepmus.ui.screens.*
import com.example.sikrepmus.presentation.ui.youtube.YouTubeScreen
import com.example.sikrepmus.presentation.viewmodel.YouTubeViewModel
import androidx.compose.material.icons.filled.VideoLibrary
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val database = AppDatabase.getDatabase(this)
        val repository = MusicRepository(this, database.songDao(), database.folderDao())
        val viewModelFactory = MusicViewModel.Factory(repository, database.folderDao())

        enableEdgeToEdge()
        setContent {
            SikRepMusTheme {
                val viewModel: MusicViewModel = viewModel(factory = viewModelFactory)
                val songs by viewModel.songs.collectAsState()
                val currentSong by viewModel.currentSong
                val isPlaying by viewModel.isPlaying
                val currentPosition by viewModel.currentPosition
                val totalDuration by viewModel.totalDuration
                val isShuffleEnabled by viewModel.isShuffleModeEnabled
                val repeatMode by viewModel.repeatMode

                var selectedTab by remember { mutableIntStateOf(0) }
                var isNowPlayingVisible by remember { mutableStateOf(false) }

                // Estados para navegación profunda desde el reproductor
                var libSubScreen by remember { mutableStateOf("Main") }
                var libFilterType by remember { mutableStateOf<String?>(null) }
                var libFilterValue by remember { mutableStateOf<String?>(null) }

                // Launcher para seleccionar carpeta (SAF)
                val folderLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.OpenDocumentTree()
                ) { uri ->
                    uri?.let {
                        contentResolver.takePersistableUriPermission(
                            it,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                        viewModel.addFolder(it)
                    }
                }

                val permissionToRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    Manifest.permission.READ_MEDIA_AUDIO
                } else {
                    Manifest.permission.READ_EXTERNAL_STORAGE
                }

                val permissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) { isGranted ->
                    if (isGranted) viewModel.refreshSongs()
                }

                LaunchedEffect(Unit) {
                    viewModel.initController(this@MainActivity)
                    if (ContextCompat.checkSelfPermission(this@MainActivity, permissionToRequest) == PackageManager.PERMISSION_GRANTED) {
                        viewModel.refreshSongs()
                    } else {
                        permissionLauncher.launch(permissionToRequest)
                    }
                }

                if (isNowPlayingVisible && currentSong != null) {
                    BackHandler { isNowPlayingVisible = false }
                    NowPlayingScreen(
                        song = currentSong!!,
                        isPlaying = isPlaying,
                        currentPosition = currentPosition,
                        totalDuration = totalDuration,
                        isShuffleEnabled = isShuffleEnabled,
                        repeatMode = repeatMode,
                        onPlayPauseClick = { viewModel.togglePlayPause() },
                        onNextClick = { viewModel.playNext() },
                        onPreviousClick = { viewModel.playPrevious() },
                        onShuffleClick = { viewModel.toggleShuffle() },
                        onRepeatClick = { viewModel.toggleRepeatMode() },
                        onSeek = { viewModel.seekTo(it) },
                        onClose = { isNowPlayingVisible = false },
                        onOptionClick = { option ->
                            when(option) {
                                "Artist" -> {
                                    libSubScreen = "FilteredList"
                                    libFilterType = "Artist"
                                    libFilterValue = currentSong?.artist
                                    selectedTab = 0
                                    isNowPlayingVisible = false
                                }
                                "Album" -> {
                                    libSubScreen = "FilteredList"
                                    libFilterType = "Album"
                                    libFilterValue = currentSong?.album
                                    selectedTab = 0
                                    isNowPlayingVisible = false
                                }
                                "Folder" -> {
                                    libSubScreen = "FilteredList"
                                    libFilterType = "Folder"
                                    libFilterValue = java.io.File(currentSong?.path ?: "").parentFile?.name
                                    selectedTab = 0
                                    isNowPlayingVisible = false
                                }
                                "Genre" -> {
                                    libSubScreen = "FilteredList"
                                    libFilterType = "Genre"
                                    libFilterValue = currentSong?.genre
                                    selectedTab = 0
                                    isNowPlayingVisible = false
                                }
                                "Delete" -> {
                                    // Lógica para eliminar próximamente
                                    Toast.makeText(this@MainActivity, "Función eliminar próximamente", Toast.LENGTH_SHORT).show()
                                }
                                "Info" -> {
                                    Toast.makeText(this@MainActivity, "Ruta: ${currentSong?.path}", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    )
                } else {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        bottomBar = {
                            NavigationBar(containerColor = Color.Black) {
                                NavigationBarItem(
                                    selected = selectedTab == 0,
                                    onClick = { 
                                        selectedTab = 0
                                        libSubScreen = "Main" // Reset al volver manualmente
                                    },
                                    icon = { Icon(Icons.Default.LibraryMusic, contentDescription = null) },
                                    label = { Text("Biblioteca") }
                                )
                                NavigationBarItem(
                                    selected = selectedTab == 1,
                                    onClick = { selectedTab = 1 },
                                    icon = { Icon(Icons.Default.Equalizer, contentDescription = null) },
                                    label = { Text("Ecu") }
                                )
                                NavigationBarItem(
                                    selected = selectedTab == 2,
                                    onClick = { selectedTab = 2 },
                                    icon = { Icon(Icons.Default.Search, contentDescription = null) },
                                    label = { Text("Buscar") }
                                )
                                NavigationBarItem(
                                    selected = selectedTab == 3,
                                    onClick = { selectedTab = 3 },
                                    icon = { Icon(Icons.Default.VideoLibrary, contentDescription = null) },
                                    label = { Text("YouTube") }
                                )
                                NavigationBarItem(
                                    selected = selectedTab == 4,
                                    onClick = { selectedTab = 4 },
                                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                                    label = { Text("Ajustes") }
                                )
                            }
                        }
                    ) { innerPadding ->
                        Box(modifier = Modifier.padding(innerPadding)) {
                            when (selectedTab) {
                                0 -> LibraryScreen(
                                    songs = songs,
                                    currentSong = currentSong,
                                    isPlaying = isPlaying,
                                    onSongClick = { viewModel.playSong(it) },
                                    onMiniPlayerClick = { isNowPlayingVisible = true },
                                    onPlayPauseClick = { viewModel.togglePlayPause() },
                                    initialSubScreen = libSubScreen,
                                    initialFilterType = libFilterType,
                                    initialFilterValue = libFilterValue
                                )
                                1 -> CenterText("Ecualizador Próximamente")
                                2 -> SearchScreen()
                                3 -> {
                                    val youtubeViewModel: YouTubeViewModel = viewModel()
                                    val scope = rememberCoroutineScope()

                                    YouTubeScreen(
                                        viewModel = youtubeViewModel,
                                        onPlayTrack = { result ->
                                            scope.launch {
                                                // Mostrar loading
                                                Toast.makeText(
                                                    this@MainActivity,
                                                    "Obteniendo audio...",
                                                    Toast.LENGTH_SHORT
                                                ).show()

                                                // Obtener URL de audio
                                                val streamUrl = youtubeViewModel.getStreamUrl(result.id)

                                                if (streamUrl != null) {
                                                    // Reproducir con tu ExoPlayer existente
                                                    viewModel.playFromUrl(
                                                        url = streamUrl,
                                                        title = result.title,
                                                        artist = result.artist
                                                    )

                                                    Toast.makeText(
                                                        this@MainActivity,
                                                        "Reproduciendo: ${result.title}",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                } else {
                                                    Toast.makeText(
                                                        this@MainActivity,
                                                        "Error al obtener audio",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }
                                        }
                                    )
                                }
                                4 -> SettingsScreen(
                                    viewModel = viewModel,
                                    onAddFolderClick = { folderLauncher.launch(null) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CenterText(text: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text, color = Color.Gray)
    }
}
