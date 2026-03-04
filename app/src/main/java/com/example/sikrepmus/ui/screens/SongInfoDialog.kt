package com.example.sikrepmus.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.sikrepmus.data.model.Song
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SongInfoDialog(
    song: Song,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.85f)
                .clip(RoundedCornerShape(28.dp)),
            color = Color(0xFF1A1A1A)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Info/Etiquetas",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Folder, contentDescription = null, tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // File Path
                Text(song.path, color = Color(0xFF1DB954), fontSize = 14.sp)
                
                Spacer(modifier = Modifier.height(16.dp))

                // Technical Info (Mockup for now as we'd need a MediaMetadataRetriever)
                TechnicalInfoRow(song)

                Spacer(modifier = Modifier.height(24.dp))

                // Stats
                Row(modifier = Modifier.fillMaxWidth()) {
                    StatItem("Veces reproducida", song.playCount.toString(), Modifier.weight(1f))
                    StatItem("Última reproducción", formatDate(song.lastPlayed), Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Metadata Fields
                InfoField("Título", song.title)
                Row(modifier = Modifier.fillMaxWidth()) {
                    InfoField("Pista", song.trackNumber.toString(), Modifier.weight(1f))
                    InfoField("Disco", song.discNumber.toString(), Modifier.weight(1f))
                    InfoField("Año", song.year.toString(), Modifier.weight(1f))
                }
                InfoField("Género", song.genre ?: "Desconocido")
                InfoField("Artista", song.artist)
                InfoField("Álbum", song.album)

                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.height(24.dp))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { /* Buscar en web logic */ }) {
                        Text("Buscar", color = Color.White)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { /* Editar logic */ },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.1f))
                    ) {
                        Text("Editar etiquetas", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun TechnicalInfoRow(song: Song) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            color = Color.White.copy(alpha = 0.1f),
            shape = RoundedCornerShape(4.dp)
        ) {
            Text(
                text = song.path.substringAfterLast(".").uppercase(),
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            "44100 Hz, 16 bit, Estéreo", // Esto se sacará dinámicamente luego
            color = Color.Gray,
            fontSize = 12.sp
        )
    }
}

@Composable
fun StatItem(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(label, color = Color.Gray, fontSize = 12.sp)
        Text(value, color = Color.White, fontSize = 14.sp)
    }
}

@Composable
fun InfoField(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(bottom = 16.dp)) {
        Text(label, color = Color.Gray, fontSize = 12.sp)
        Text(value, color = Color.White, fontSize = 16.sp)
        Divider(modifier = Modifier.padding(top = 4.dp), color = Color.Gray.copy(alpha = 0.2f))
    }
}

private fun formatDate(timestamp: Long): String {
    if (timestamp == 0L) return "-"
    val sdf = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale("es", "ES"))
    return sdf.format(Date(timestamp))
}
