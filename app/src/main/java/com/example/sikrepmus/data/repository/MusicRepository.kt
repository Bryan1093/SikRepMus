package com.example.sikrepmus.data.repository

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.example.sikrepmus.data.local.dao.SongDao
import com.example.sikrepmus.data.local.dao.FolderDao
import com.example.sikrepmus.data.local.entities.SongEntity
import com.example.sikrepmus.data.local.entities.FolderEntity
import com.example.sikrepmus.data.model.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class MusicRepository(
    private val context: Context,
    private val songDao: SongDao,
    private val folderDao: FolderDao
) {

    fun getSongsFlow(): Flow<List<Song>> {
        return songDao.getAllSongs().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun addFolder(uri: Uri) {
        folderDao.insertFolder(FolderEntity(uri.toString()))
        refreshSongs()
    }

    suspend fun refreshSongs() = withContext(Dispatchers.IO) {
        val selectedFolders = folderDao.getAllFolders().first()
        val songsFromDevice = mutableListOf<SongEntity>()
        
        val collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DATE_ADDED
        )

        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"

        context.contentResolver.query(
            collection,
            projection,
            selection,
            null,
            null
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)

            while (cursor.moveToNext()) {
                val path = cursor.getString(dataColumn)
                
                if (selectedFolders.isNotEmpty()) {
                    val isInsideSelectedFolder = selectedFolders.any { folder ->
                        path.contains(folder.uri.split(":").last(), ignoreCase = true)
                    }
                    if (!isInsideSelectedFolder) continue
                }

                val id = cursor.getLong(idColumn)
                val title = cursor.getString(titleColumn) ?: "Desconocido"
                val artist = cursor.getString(artistColumn) ?: "Artista Desconocido"
                val album = cursor.getString(albumColumn) ?: "Álbum Desconocido"
                val duration = cursor.getLong(durationColumn)
                val albumId = cursor.getLong(albumIdColumn)
                val dateAdded = cursor.getLong(dateAddedColumn)

                val contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id).toString()
                val albumArtUri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), albumId).toString()

                songsFromDevice.add(
                    SongEntity(
                        id = id,
                        title = title,
                        artist = artist,
                        album = album,
                        duration = duration,
                        contentUri = contentUri,
                        albumArtUri = albumArtUri,
                        dateAdded = dateAdded,
                        path = path
                    )
                )
            }
        }

        if (songsFromDevice.isNotEmpty()) {
            songDao.deleteAllSongs()
            songDao.insertSongs(songsFromDevice)
        }
    }

    private fun SongEntity.toDomain(): Song {
        return Song(
            id = id,
            title = title,
            artist = artist,
            album = album,
            duration = duration,
            contentUri = Uri.parse(contentUri),
            albumArtUri = albumArtUri?.let { Uri.parse(it) },
            genre = genre,
            dateAdded = dateAdded,
            path = path
        )
    }
}
