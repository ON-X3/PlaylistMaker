package com.practicum.playlistmaker.library.ui.state

import com.practicum.playlistmaker.library.domain.models.Playlist

sealed interface PlaylistsState {

    object Empty: PlaylistsState

    data class Content(val playlists: List<Playlist>): PlaylistsState

}