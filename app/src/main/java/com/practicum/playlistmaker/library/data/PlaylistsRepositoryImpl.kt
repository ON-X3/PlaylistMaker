package com.practicum.playlistmaker.library.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.net.toUri
import com.practicum.playlistmaker.core.db.AppDatabase
import com.practicum.playlistmaker.core.db.converters.PlaylistDbConverter
import com.practicum.playlistmaker.core.db.converters.TrackDbConverter
import com.practicum.playlistmaker.core.db.entity.PlaylistEntity
import com.practicum.playlistmaker.core.db.entity.PlaylistTrackInPlaylistEntity
import com.practicum.playlistmaker.library.domain.api.PlaylistsRepository
import com.practicum.playlistmaker.library.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class PlaylistsRepositoryImpl(
    private val db: AppDatabase,
    private val context: Context,
    private val playlistDbConverter: PlaylistDbConverter,
    private val appScope: CoroutineScope,
    private val trackDbConverter: TrackDbConverter
) :
    PlaylistsRepository {

    override fun addPlaylist(
        name: String,
        description: String,
        imageUriString: String?
    ) {
        appScope.launch {
            Log.d("PlaylistDB", "In repository")
            val uri = imageUriString?.toUri()
            val path = saveImage(uri)
            Log.d("PlaylistDB", "before dao")
            db.playlistsDao().insertPlaylist(
                PlaylistEntity(
                    playlistName = name, playlistDescription = description,
                    imagePath = path
                )
            )
            Log.d("PlaylistDB", "after dao")
        }

    }

    override fun getPlaylists(): Flow<List<Playlist>> =
        db.playlistsDao().getPlaylistsWithTracks().map { playlists ->
            playlists.map {
                playlistDbConverter.map(it)
            }
        }

    override fun getPlaylistWithTracks(playlistId: Int): Flow<Pair<Playlist, List<Track>>> =
        db.playlistsDao().getPlaylist(playlistId)
            .combine(
                db.tracksInPlaylistsDao().getTracksFromPlaylist(playlistId)
            ) { playlist, tracks ->
                Pair(
                    playlistDbConverter.map(playlist, tracks),
                    tracks.map { trackDbConverter.map(it) })
            }

    override suspend fun addTrackToPlaylist(
        track: Track,
        playlistId: Int
    ) {
        db.tracksInPlaylistsDao()
            .insertTrackInPlaylist(trackDbConverter.mapToTrackInPlaylist(track))
        db.playlistTrackInPlaylistDao()
            .insertPlaylistTrackInPlaylist(PlaylistTrackInPlaylistEntity(playlistId, track.trackId))
    }

    override suspend fun deleteTrackFromPlaylist(trackId: Long, playlistId: Int) {
        db.playlistTrackInPlaylistDao().deleteTrackFromPlaylist(trackId, playlistId)
        if (!db.playlistTrackInPlaylistDao().isTrackInAnyPlaylist(trackId)) {
            db.tracksInPlaylistsDao().deleteTrack(trackId)
        }
    }

    override fun deletePlaylist(playlistId: Int) {
        appScope.launch {
            val trackIdsFromDeletedPlaylist =
                db.playlistTrackInPlaylistDao().getTrackIdsFromPlaylist(playlistId)
            db.playlistsDao().deletePlaylist(playlistId)
            trackIdsFromDeletedPlaylist.forEach {
                if (!db.playlistTrackInPlaylistDao().isTrackInAnyPlaylist(it)) {
                    db.tracksInPlaylistsDao().deleteTrack(it)
                }
            }
        }
    }

    override fun updatePlaylist(
        id: Int,
        name: String,
        description: String,
        imageUriString: String?
    ) {
        appScope.launch {
            val uri = imageUriString?.toUri()
            val path = saveImage(uri)
            deleteImage(db.playlistsDao().getPlaylist(id).first().imagePath)
            db.playlistsDao().updatePlaylist(id, name, description, path)
        }
    }

    private suspend fun saveImage(uri: Uri?): String =
        withContext(Dispatchers.IO) {
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

    private suspend fun deleteImage(path: String) {
        withContext(Dispatchers.IO) {
            File(path).delete()
        }
    }
}
