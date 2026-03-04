package com.example.sikrepmus.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.sikrepmus.data.model.Song
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicListScreen(
    songs: List<Song>,
    currentSong: Song?,
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    onSongClick: (Song) -> Unit = {},
    onMiniPlayerClick: () -> Unit = {},
    onPlayPauseClick: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ#".toList()
    
    var activeLetter by remember { mutableStateOf<Char?>(null) }
    var columnHeight by remember { mutableStateOf(0f) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Todas las canciones", color = Color.White) },
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
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            Row(modifier = Modifier.fillMaxSize()) {
                // Barra lateral alfabética con efecto de lupa (Poweramp Style)
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(32.dp)
                        .background(Color.Black.copy(alpha = 0.5f))
                        .onGloballyPositioned { columnHeight = it.size.height.toFloat() }
                        .pointerInput(songs) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    val letter = getLetterFromOffset(offset.y, columnHeight, alphabet)
                                    activeLetter = letter
                                    scrollToLetter(letter, songs, listState, scope)
                                },
                                onDrag = { change, _ ->
                                    val letter = getLetterFromOffset(change.position.y, columnHeight, alphabet)
                                    if (letter != activeLetter) {
                                        activeLetter = letter
                                        scrollToLetter(letter, songs, listState, scope)
                                    }
                                },
                                onDragEnd = { activeLetter = null },
                                onDragCancel = { activeLetter = null }
                            )
                        }
                        .pointerInput(songs) {
                            detectTapGestures { offset ->
                                val letter = getLetterFromOffset(offset.y, columnHeight, alphabet)
                                activeLetter = letter
                                scrollToLetter(letter, songs, listState, scope)
                                // Pequeño delay para el efecto visual antes de limpiar
                                scope.launch {
                                    kotlinx.coroutines.delay(200)
                                    activeLetter = null
                                }
                            }
                        }
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceEvenly,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        alphabet.forEach { char ->
                            val isSelected = activeLetter == char
                            val scale by animateFloatAsState(if (isSelected) 2f else 1f, label = "scale")
                            val color = if (isSelected) Color.White else Color(0xFF1DB954)
                            val weight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold

                            Text(
                                text = char.toString(),
                                color = color,
                                fontSize = 10.sp,
                                fontWeight = weight,
                                modifier = Modifier
                                    .scale(scale)
                                    .fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                if (songs.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay música en estas carpetas.", color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(bottom = 100.dp)
                    ) {
                        items(songs) { song ->
                            SongItem(
                                song = song, 
                                isSelected = song.id == currentSong?.id,
                                onClick = { onSongClick(song) }
                            )
                        }
                    }
                }
            }

            // Indicador flotante central (Lupa de letra)
            if (activeLetter != null) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .align(Alignment.Center)
                        .background(Color(0xFF1DB954).copy(alpha = 0.8f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = activeLetter.toString(),
                        color = Color.Black,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            if (currentSong != null) {
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
}

private fun getLetterFromOffset(y: Float, height: Float, alphabet: List<Char>): Char? {
    if (height <= 0) return null
    val proportion = (y / height).coerceIn(0f, 0.99f)
    val index = (proportion * alphabet.size).toInt()
    return alphabet.getOrNull(index)
}

private fun scrollToLetter(char: Char?, songs: List<Song>, listState: androidx.compose.foundation.lazy.LazyListState, scope: kotlinx.coroutines.CoroutineScope) {
    if (char == null) return
    val index = songs.indexOfFirst { song ->
        val title = song.title.trim()
        if (title.isEmpty()) return@indexOfFirst false
        val firstChar = title.first()
        if (char == '#') {
            firstChar.isDigit()
        } else {
            firstChar.equals(char, ignoreCase = true)
        }
    }
    if (index >= 0) {
        scope.launch {
            listState.scrollToItem(index)
        }
    }
}

@Composable
fun SongItem(song: Song, isSelected: Boolean, onClick: () -> Unit) {
    val backgroundColor = if (isSelected) Color(0xFF1DB954).copy(alpha = 0.1f) else Color.Transparent
    
    ListItem(
        colors = ListItemDefaults.colors(containerColor = backgroundColor),
        headlineContent = { 
            Text(
                song.title, 
                maxLines = 1, 
                color = if (isSelected) Color(0xFF1DB954) else Color.White,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            ) 
        },
        supportingContent = { Text("${song.artist} • ${song.album}", maxLines = 1, color = Color.Gray) },
        leadingContent = {
            AsyncImage(
                model = song.albumArtUri,
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        },
        modifier = Modifier.clickable { onClick() }
    )
}

@Composable
fun MiniPlayer(
    song: Song,
    isPlaying: Boolean,
    onPlayPauseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp),
        color = Color.Black.copy(alpha = 0.9f),
        shape = RoundedCornerShape(20.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(listOf(Color(0xFF1DB954), Color(0xFF19E68C)))
        )
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = song.albumArtUri,
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                Text(
                    song.title,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    song.artist,
                    color = Color.Gray,
                    fontSize = 12.sp,
                    maxLines = 1
                )
            }

            IconButton(
                onClick = onPlayPauseClick,
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        Brush.linearGradient(listOf(Color(0xFF1DB954), Color(0xFF19E68C))),
                        CircleShape
                    )
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.Black
                )
            }
        }
    }
}
