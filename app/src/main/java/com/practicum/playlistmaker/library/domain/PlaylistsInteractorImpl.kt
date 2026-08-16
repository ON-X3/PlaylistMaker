package com.practicum.playlistmaker.library.domain

import android.util.Log
import com.practicum.playlistmaker.library.domain.api.PlaylistsInteractor
import com.practicum.playlistmaker.library.domain.api.PlaylistsRepository
import com.practicum.playlistmaker.library.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class PlaylistsInteractorImpl(private val playlistsRepository: PlaylistsRepository): PlaylistsInteractor {
    override fun addPlaylist(
        name: String,
        description: String,
        imageUriString: String?
    ) {
        Log.d("PlaylistDB", "In interactor")
        playlistsRepository.addPlaylist(name, description, imageUriString)
    }

    override fun getPlaylists(): Flow<List<Playlist>> = playlistsRepository.getPlaylists()

    override fun getPlaylistWithTracks(playlistId: Int): Flow<Pair<Playlist, List<Track>>> = playlistsRepository.getPlaylistWithTracks(playlistId)

    override suspend fun addTrackToPlaylist(
        track: Track,
        playlistId: Int
    ) {
        playlistsRepository.addTrackToPlaylist(track, playlistId)
    }

    override suspend fun deleteTrackFromPlaylist(trackId: Long, playlistId: Int) {
        playlistsRepository.deleteTrackFromPlaylist(trackId, playlistId)
    }

    override fun deletePlaylist(playlistId: Int) {
        playlistsRepository.deletePlaylist(playlistId)
    }

    override fun updatePlaylist(
        id: Int,
        name: String,
        description: String,
        imageUriString: String?
    ) {
        playlistsRepository.updatePlaylist(id, name, description, imageUriString)
    }

}