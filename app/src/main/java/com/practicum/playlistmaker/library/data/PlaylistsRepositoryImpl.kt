package com.practicum.playlistmaker.library.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Log
import com.practicum.playlistmaker.core.db.AppDatabase
import com.practicum.playlistmaker.core.db.entity.PlaylistEntity
import com.practicum.playlistmaker.library.domain.api.PlaylistsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import androidx.core.net.toUri
import com.practicum.playlistmaker.core.db.converters.PlaylistDbConverter
import com.practicum.playlistmaker.core.db.converters.TrackDbConverter
import com.practicum.playlistmaker.library.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.FileOutputStream

class PlaylistsRepositoryImpl(private val db: AppDatabase, private val context: Context, private val playlistDbConverter: PlaylistDbConverter, private val appScope: CoroutineScope, private val trackDbConverter: TrackDbConverter) :
    PlaylistsRepository {

    override fun addPlaylist(
        name: String,
        description: String,
        imageUriString: String?
    ) {
        appScope.launch { Log.d("PlaylistDB", "In repository")
            val uri = imageUriString?.toUri()
            val path = withContext(Dispatchers.IO) {
                if (uri != null) {
                    val filePath =
                        File(
                            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                            "PlaylistsAlbum"
                        )
                    if (!filePath.exists()) {
                        filePath.mkdirs()
                    }

                    val file = File(filePath, "image_${System.currentTimeMillis()}.jpg")
                    val inputStream = context.contentResolver.openInputStream(uri)
                    val outputStream = FileOutputStream(file)
                    BitmapFactory
                        .decodeStream(inputStream)
                        .compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
                    Log.d("PlaylistDB", "In filePath")
                    file.absolutePath
                } else {
                    ""
                }
            }
            Log.d("PlaylistDB", "before dao")
            db.playlistsDao().insertPlaylist(
                PlaylistEntity(
                    playlistName = name, playlistDescription = description,
                    imagePath = path,
                    tracks = "",
                    count = 0,
                )
            )
            Log.d("PlaylistDB", "after dao") }

    }

    override fun getPlaylists(): Flow<List<Playlist>> = db.playlistsDao().getPlaylists().map {playlists ->
        playlists.map {
            playlistDbConverter.map(it)
        }
    }

    override suspend fun addTrackToPlaylist(
        track: Track,
        playlist: Playlist
    ) {
        db.tracksInPlaylistsDao().insertTrackInPlaylist(trackDbConverter.mapToTrackInPlaylist(track))
        db.playlistsDao().updatePlaylist(playlistDbConverter.map(playlist.copy(tracks = playlist.tracks + track.trackId, count = playlist.count + 1)))
    }
}