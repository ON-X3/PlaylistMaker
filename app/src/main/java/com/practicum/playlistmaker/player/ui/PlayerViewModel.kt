package com.practicum.playlistmaker.player.ui

import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.core.common.mapper.toDomain
import com.practicum.playlistmaker.library.domain.api.FavoritesInteractor
import com.practicum.playlistmaker.library.domain.api.PlaylistsInteractor
import com.practicum.playlistmaker.library.domain.models.Playlist
import com.practicum.playlistmaker.search.ui.models.TrackUi
import com.practicum.playlistmaker.util.SingleLiveEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

class PlayerViewModel(
    private val track: TrackUi,
    private val mediaPlayer: MediaPlayer,
    private val favoritesInteractor: FavoritesInteractor,
    private val playlistsInteractor: PlaylistsInteractor
) :
    ViewModel() {

    init {
        preparePlayer()
        viewModelScope.launch {
            playlistsInteractor.getPlaylists().collect {playlists ->
                if (playlists.isNotEmpty()) {
                    playlistsLiveData.value = PlayerPlaylistsState.Content(playlists)
                    Log.d("PlayerPlaylistsDB", "get data from flow: ${playlists[0].playlistName}")
                } else {
                    playlistsLiveData.value = PlayerPlaylistsState.Empty
                }
            }
        }
    }

    private val playerStateLiveData = MutableLiveData<PlayerState>(PlayerState.StateDefault(track.isFavorite))
    fun observePlayerState(): LiveData<PlayerState> = playerStateLiveData

    private val playlistsLiveData = MutableLiveData< PlayerPlaylistsState>(PlayerPlaylistsState.Empty)
    fun observePlaylists(): LiveData<PlayerPlaylistsState> = playlistsLiveData

    private val singleLiveEventTrackAddedToPlaylist = SingleLiveEvent<Pair<Boolean, String>>()
    fun observeIsTrackAdded(): LiveData<Pair<Boolean, String>> = singleLiveEventTrackAddedToPlaylist

    var playProgressJob: Job? = null


    fun onPause() {
        pausePlayer()
    }

    override fun onCleared() {
        mediaPlayer.release()
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(track.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerStateLiveData.value =
                PlayerState.StatePrepared(playerStateLiveData.value!!.isFavorite)
        }
        mediaPlayer.setOnCompletionListener {
            playerStateLiveData.value =
                PlayerState.StatePrepared(playerStateLiveData.value!!.isFavorite)
            playProgressJob?.cancel()
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerStateLiveData.value = PlayerState.StatePlaying(
            playerStateLiveData.value!!.playProgress,
            playerStateLiveData.value!!.isFavorite
        )
        startUpdatingPlayProgress()
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playerStateLiveData.value = PlayerState.StatePaused(
            playerStateLiveData.value!!.playProgress,
            playerStateLiveData.value!!.isFavorite
        )
        playProgressJob?.cancel()
    }

    fun playbackControl() {
        when (playerStateLiveData.value) {
            is PlayerState.StatePlaying -> pausePlayer()
            is PlayerState.StatePrepared, is PlayerState.StatePaused -> startPlayer()
            else -> {}
        }
    }

    fun onFavoritesClick() {
        if (playerStateLiveData.value!!.isFavorite == true) {
            playerStateLiveData.value = playerStateLiveData.value!!.apply { isFavorite = false }
            viewModelScope.launch { favoritesInteractor.delete(track.toDomain()) }
        } else {
            playerStateLiveData.value = playerStateLiveData.value!!.apply { isFavorite = true }
            viewModelScope.launch { favoritesInteractor.add(track.toDomain()) }
        }
    }

    private fun startUpdatingPlayProgress() {
        playProgressJob?.cancel()
        playProgressJob = viewModelScope.launch {
            while (playerStateLiveData.value is PlayerState.StatePlaying) {
                delay(PLAY_PROGRESS_UPDATE_DELAY)
                playerStateLiveData.value = PlayerState.StatePlaying(
                    SimpleDateFormat("mm:ss", Locale.getDefault()).format(
                        mediaPlayer.currentPosition
                    ),
                    playerStateLiveData.value!!.isFavorite)
            }
        }
    }

    fun addToPlaylist(playlist: Playlist) {
        if (track.trackId in playlist.tracks) {
            singleLiveEventTrackAddedToPlaylist.value = Pair(false, playlist.playlistName)
        } else {
            viewModelScope.launch {
                playlistsInteractor.addTrackToPlaylist(track.toDomain(), playlist)
            }
            singleLiveEventTrackAddedToPlaylist.value = Pair(true, playlist.playlistName)
        }
    }

    companion object {

        const val PLAY_PROGRESS_DEFAULT_VALUE = 0
        private const val PLAY_PROGRESS_UPDATE_DELAY = 300L


    }
}