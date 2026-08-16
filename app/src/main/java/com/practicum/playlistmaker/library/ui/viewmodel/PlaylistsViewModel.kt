package com.practicum.playlistmaker.library.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.library.domain.api.PlaylistsInteractor
import com.practicum.playlistmaker.library.ui.state.PlaylistsState
import kotlinx.coroutines.launch

class PlaylistsViewModel (private val playlistsInteractor: PlaylistsInteractor): ViewModel() {

    init {
        viewModelScope.launch {
            playlistsInteractor.getPlaylists().collect { playlists ->
                if (playlists.isNotEmpty()) {
                    state.value = PlaylistsState.Content(playlists)
                } else {
                    state.value = PlaylistsState.Empty
                }
            }
        }
    }
    private val state = MutableLiveData<PlaylistsState>(PlaylistsState.Empty)
    fun observeState(): LiveData<PlaylistsState> = state


}