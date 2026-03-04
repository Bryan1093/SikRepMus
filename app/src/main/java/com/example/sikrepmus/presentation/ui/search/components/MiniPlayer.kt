package com.example.sikrepmus.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.sikrepmus.presentation.ui.search.player.PlayerViewModel

@Composable
fun MiniPlayer(
    viewModel: PlayerViewModel,
    modifier: Modifier = Modifier
) {
    val currentSong by viewModel.currentSong.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val currentPosition by viewModel.currentPosition.collectAsState()
    val duration by viewModel.duration.collectAsState()

    currentSong?.let { song ->
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                // Song info row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Cover
                    AsyncImage(
                        model = song.coverUrl,
                        contentDescription = "Cover",
                        modifier = Modifier.size(56.dp)
                    )

                    Spacer(Modifier.width(12.dp))

                    // Info
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = song.title,
                            style = MaterialTheme.typography.titleSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = song.artist,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Preview 30s • ${song.source}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Play/Pause button
                    IconButton(onClick = { viewModel.togglePlayPause() }) {
                        Icon(
                            imageVector = if (isPlaying)
                                Icons.Default.Pause
                            else
                                Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Stop button
                    IconButton(onClick = { viewModel.stop() }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Stop"
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Progress bar
                Column {
                    LinearProgressIndicator(
                        progress = if (duration > 0)
                            (currentPosition.toFloat() / duration.toFloat())
                        else 0f,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(4.dp))

                    // Time labels
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = formatTime(currentPosition),
                            style = MaterialTheme.typography.labelSmall
                        )
                        Text(
                            text = formatTime(duration),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}

private fun formatTime(millis: Long): String {
    val seconds = (millis / 1000) % 60
    val minutes = (millis / 1000) / 60
    return String.format("%d:%02d", minutes, seconds)
}