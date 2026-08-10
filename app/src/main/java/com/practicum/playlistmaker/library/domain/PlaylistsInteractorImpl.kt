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
    override suspend fun addTrackToPlaylist(
        track: Track,
        playlist: Playlist
    ) {
        playlistsRepository.addTrackToPlaylist(track, playlist)
    }

}