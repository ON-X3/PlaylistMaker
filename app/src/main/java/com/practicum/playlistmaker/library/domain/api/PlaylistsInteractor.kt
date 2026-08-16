package com.practicum.playlistmaker.library.domain.api

import com.practicum.playlistmaker.library.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsInteractor {
    fun addPlaylist(name: String, description: String, imageUriString: String?)

    fun getPlaylists(): Flow<List<Playlist>>

    fun getPlaylistWithTracks(playlistId: Int): Flow<Pair<Playlist, List<Track>>>

    suspend fun addTrackToPlaylist(track: Track, playlistId: Int)

    suspend fun deleteTrackFromPlaylist(trackId: Long, playlistId: Int)

    fun deletePlaylist(playlistId: Int)

    fun updatePlaylist(id: Int, name: String, description: String, imageUriString: String?)
}