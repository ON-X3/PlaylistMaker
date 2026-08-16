package com.practicum.playlistmaker.library.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.core.common.mapper.toDomain
import com.practicum.playlistmaker.core.common.mapper.toUi
import com.practicum.playlistmaker.library.domain.api.PlaylistsInteractor
import com.practicum.playlistmaker.library.ui.state.PlaylistState
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.sharing.domain.api.SharingInteractor
import com.practicum.playlistmaker.util.SingleLiveEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class PlaylistViewModel(
    private val playlistId: Int,
    private val playlistsInteractor: PlaylistsInteractor,
    private val sharingInteractor: SharingInteractor
) :
    ViewModel() {

    private var collectPlaylistJob: Job? = null

    init {
        collectPlaylistJob = viewModelScope.launch {
            playlistsInteractor.getPlaylistWithTracks(playlistId).collect {
                val playlist = it.first
                val tracks = it.second
                playlistLiveData.value = if (tracks.isEmpty()) {
                    PlaylistState.EmptyPlaylist(
                        playlist.playlistName,
                        playlist.playlistDescription,
                        playlist.imagePath
                    )
                } else {
                    PlaylistState.PlaylistWithTracks(
                        playlist.playlistName,
                        playlist.playlistDescription,
                        playlist.imagePath,
                        getPlaylistDuration(tracks),
                        tracks.size,
                        tracks.map { track -> track.toUi() })
                }
            }
        }

    }

    private val playlistLiveData = MutableLiveData<PlaylistState>()
    fun observePlaylist(): LiveData<PlaylistState> = playlistLiveData

    private val showToastLiveData = SingleLiveEvent<Boolean>()
    fun observeShowToast(): LiveData<Boolean> = showToastLiveData

    private fun getPlaylistDuration(tracks: List<Track>): Int {
        var duration: Long = 0
        tracks.forEach {
            duration += it.trackTime
        }
        return (duration / 60000.0).roundToInt()
    }

    fun deleteTrack(trackId: Long) {
        viewModelScope.launch {
            playlistsInteractor.deleteTrackFromPlaylist(trackId, playlistId)
        }
    }

    fun sharePlaylist(formatter: SharingInteractor.PlaylistInfoFormatter) {
        if (playlistLiveData.value?.trackList.isNullOrEmpty()) {
            showToastLiveData.value = true
        } else {
            sharingInteractor.sharePlaylist(
                playlistLiveData.value!!.name,
                playlistLiveData.value!!.description,
                playlistLiveData.value!!.trackList.map { it.toDomain() },
                formatter
            )
        }
    }

    fun deletePlaylist() {
        collectPlaylistJob?.cancel()
        playlistsInteractor.deletePlaylist(playlistId)
    }

    fun getImagePath(): String {
        return playlistLiveData.value!!.path
    }

}