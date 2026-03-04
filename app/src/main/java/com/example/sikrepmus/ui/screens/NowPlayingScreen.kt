package com.example.sikrepmus.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.Player
import coil.compose.AsyncImage
import com.example.sikrepmus.data.model.Song

@Composable
fun NowPlayingScreen(
    song: Song,
    isPlaying: Boolean,
    currentPosition: Long,
    totalDuration: Long,
    isShuffleEnabled: Boolean,
    repeatMode: Int,
    onPlayPauseClick: () -> Unit,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onShuffleClick: () -> Unit,
    onRepeatClick: () -> Unit,
    onSeek: (Long) -> Unit,
    onClose: () -> Unit,
    onOptionClick: (String) -> Unit
) {
    var isDragging by remember { mutableStateOf(false) }
    var dragPosition by remember { mutableLongStateOf(0L) }
    val displayPosition = if (isDragging) dragPosition else currentPosition
    
    var showMenu by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF1DB954).copy(alpha = 0.3f), Color(0xFF0F0F0F))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Color.White)
                }
                Text(
                    "REPRODUCIENDO",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = null, tint = Color.White)
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        modifier = Modifier.background(Color(0xFF1A1A1A))
                    ) {
                        DropdownMenuItem(
                            text = { Text("Información", color = Color.White) },
                            onClick = { showMenu = false; onOptionClick("Info") }
                        )
                        DropdownMenuItem(
                            text = { Text("Letra", color = Color.White) },
                            onClick = { showMenu = false; onOptionClick("Lyrics") }
                        )
                        Divider(color = Color.Gray.copy(alpha = 0.2f))
                        DropdownMenuItem(
                            text = { Text("Ir a Artista", color = Color.White) },
                            onClick = { showMenu = false; onOptionClick("Artist") }
                        )
                        DropdownMenuItem(
                            text = { Text("Ir a Álbum", color = Color.White) },
                            onClick = { showMenu = false; onOptionClick("Album") }
                        )
                        DropdownMenuItem(
                            text = { Text("Ir a Carpeta", color = Color.White) },
                            onClick = { showMenu = false; onOptionClick("Folder") }
                        )
                        DropdownMenuItem(
                            text = { Text("Ir a Género", color = Color.White) },
                            onClick = { showMenu = false; onOptionClick("Genre") }
                        )
                        Divider(color = Color.Gray.copy(alpha = 0.2f))
                        DropdownMenuItem(
                            text = { Text("Eliminar", color = Color.Red) },
                            onClick = { showMenu = false; onOptionClick("Delete") }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Album Art
            Surface(
                modifier = Modifier
                    .aspectRatio(1f)
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.linearGradient(listOf(Color(0xFF1DB954), Color(0xFF19E68C)))
                ),
                tonalElevation = 8.dp
            ) {
                AsyncImage(
                    model = song.albumArtUri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Song Info
            Text(
                text = song.title,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
            Text(
                text = song.artist,
                color = Color(0xFF1DB954),
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                maxLines = 1
            )

            Spacer(modifier = Modifier.weight(1f))

            // Shuffle and Repeat Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onShuffleClick) {
                    Icon(
                        imageVector = Icons.Default.Shuffle,
                        contentDescription = "Shuffle",
                        tint = if (isShuffleEnabled) Color(0xFF1DB954) else Color.White.copy(alpha = 0.5f)
                    )
                }

                IconButton(onClick = onRepeatClick) {
                    val icon = when (repeatMode) {
                        Player.REPEAT_MODE_ONE -> Icons.Default.RepeatOne
                        Player.REPEAT_MODE_ALL -> Icons.Default.Repeat
                        else -> Icons.Default.Repeat
                    }
                    val tint = if (repeatMode != Player.REPEAT_MODE_OFF) Color(0xFF1DB954) else Color.White.copy(alpha = 0.5f)
                    Icon(imageVector = icon, contentDescription = "Repeat", tint = tint)
                }
            }

            // Progress Bar
            Slider(
                value = if (totalDuration > 0) displayPosition.toFloat() / totalDuration else 0f,
                onValueChange = { 
                    isDragging = true
                    dragPosition = (it * totalDuration).toLong()
                },
                onValueChangeFinished = {
                    onSeek(dragPosition)
                    isDragging = false
                },
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFF1DB954),
                    activeTrackColor = Color(0xFF1DB954),
                    inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                )
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(formatTime(displayPosition), color = Color.Gray, fontSize = 12.sp)
                Text(formatTime(totalDuration), color = Color.Gray, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onPreviousClick, modifier = Modifier.size(48.dp)) {
                    Icon(Icons.Default.SkipPrevious, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
                }
                
                Surface(
                    onClick = onPlayPauseClick,
                    modifier = Modifier.size(72.dp),
                    shape = CircleShape,
                    color = Color.White
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }

                IconButton(onClick = onNextClick, modifier = Modifier.size(48.dp)) {
                    Icon(Icons.Default.SkipNext, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

private fun formatTime(ms: Long): String {
    if (ms < 0) return "00:00"
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}
