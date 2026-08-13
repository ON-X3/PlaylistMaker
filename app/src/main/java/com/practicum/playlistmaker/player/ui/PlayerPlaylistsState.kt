package com.practicum.playlistmaker.player.ui

import com.practicum.playlistmaker.library.domain.models.Playlist

sealed interface PlayerPlaylistsState {

    object Empty: PlayerPlaylistsState

    data class Content(val playlists: List<Playlist>): PlayerPlaylistsState
}