package com.practicum.playlistmaker.player.ui

import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.core.common.mapper.toDomain
import com.practicum.playlistmaker.library.domain.api.FavoritesInteractor
import com.practicum.playlistmaker.search.ui.models.TrackUi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

class PlayerViewModel(
    private val track: TrackUi,
    private val mediaPlayer: MediaPlayer,
    private val favoritesInteractor: FavoritesInteractor
) :
    ViewModel() {

    init {
        preparePlayer()
    }

    private val playerStateLiveData = MutableLiveData<PlayerState>(PlayerState.StateDefault(track.isFavorite))
    fun observePlayerState(): LiveData<PlayerState> = playerStateLiveData

    var playProgressJob: Job? = null


    fun onPause() {
        pausePlayer()
    }

    override fun onCleared() {
        super.onCleared()
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

    companion object {

        const val PLAY_PROGRESS_DEFAULT_VALUE = 0
        private const val PLAY_PROGRESS_UPDATE_DELAY = 300L


    }
}