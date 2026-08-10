package com.practicum.playlistmaker.library.ui.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.library.domain.api.PlaylistsInteractor

class NewPlaylistViewModel (private val playlistsInteractor: PlaylistsInteractor): ViewModel() {

    private val newPlaylistState = MutableLiveData(NewPlaylistState())
    fun observeNewPlaylistState(): LiveData<NewPlaylistState> = newPlaylistState


    fun onImagePicked(uri: Uri) {
        newPlaylistState.value = newPlaylistState.value?.copy(imageUri = uri)
    }

    fun onNameEdited(name: String) {
        newPlaylistState.value = newPlaylistState.value?.copy(name = name, isButtonEnabled = name.isNotEmpty())
    }

    fun onDescriptionEdited(description: String) {
        newPlaylistState.value = newPlaylistState.value?.copy(description = description)
    }

    fun createPlaylist() {
            Log.d("PlaylistDB", "in viewModel")
            playlistsInteractor.addPlaylist(newPlaylistState.value!!.name, newPlaylistState.value!!.description, newPlaylistState.value?.imageUri?.toString())
    }
}