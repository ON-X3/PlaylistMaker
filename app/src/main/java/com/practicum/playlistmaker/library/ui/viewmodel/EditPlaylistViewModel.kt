package com.practicum.playlistmaker.library.ui.viewmodel

import com.practicum.playlistmaker.library.domain.api.PlaylistsInteractor
import com.practicum.playlistmaker.library.ui.state.NewPlaylistState

class EditPlaylistViewModel(playlistsInteractor: PlaylistsInteractor, playlistState: NewPlaylistState, private val playlistId: Int): NewPlaylistViewModel(playlistsInteractor) {

    init {
        newPlaylistState.value = playlistState
    }

    fun savePlaylist() {
        playlistsInteractor.updatePlaylist(playlistId, newPlaylistState.value!!.name.trim(), newPlaylistState.value!!.description.trim(), newPlaylistState.value?.imageUri?.toString())
    }

}