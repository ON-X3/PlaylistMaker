package com.practicum.playlistmaker.library.ui.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.library.domain.api.PlaylistsInteractor
import com.practicum.playlistmaker.library.ui.state.NewPlaylistState

open class NewPlaylistViewModel (protected val playlistsInteractor: PlaylistsInteractor): ViewModel() {

    protected val newPlaylistState = MutableLiveData(NewPlaylistState())
    fun observeNewPlaylistState(): LiveData<NewPlaylistState> = newPlaylistState


    fun onImagePicked(uri: Uri) {
        newPlaylistState.value = newPlaylistState.value?.copy(imageUri = uri)
    }

    fun onNameEdited(name: String) {
        newPlaylistState.value = newPlaylistState.value?.copy(name = name, isButtonEnabled = name.isNotBlank())
    }

    fun onDescriptionEdited(description: String) {
        newPlaylistState.value = newPlaylistState.value?.copy(description = description)
    }

    fun createPlaylist() {
            Log.d("PlaylistDB", "in viewModel")
            playlistsInteractor.addPlaylist(newPlaylistState.value!!.name.trim(), newPlaylistState.value!!.description.trim(), newPlaylistState.value?.imageUri?.toString())
    }
}