package com.practicum.playlistmaker.player.ui

import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

class PlayerViewModel(private val trackUrl: String, private val mediaPlayer: MediaPlayer) :
    ViewModel() {

    init {
        preparePlayer()
    }

    private val playProgressDefaultValue = SimpleDateFormat("mm:ss", Locale.getDefault()).format(0)

    private val playerStateLiveData = MutableLiveData(STATE_DEFAULT)
    fun observePlayerState(): LiveData<Int> = playerStateLiveData

    private val playProgressLiveData = MutableLiveData(playProgressDefaultValue)
    fun observePlayProgress(): LiveData<String> = playProgressLiveData

    var playProgressJob: Job? = null


    fun onPause() {
        pausePlayer()
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer.release()
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(trackUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerStateLiveData.postValue(STATE_PREPARED)
        }
        mediaPlayer.setOnCompletionListener {
            playerStateLiveData.postValue(STATE_PREPARED)
            playProgressJob?.cancel()
            playProgressLiveData.postValue(playProgressDefaultValue)
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerStateLiveData.postValue(STATE_PLAYING)
        startUpdatingPlayProgress()
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playerStateLiveData.postValue(STATE_PAUSED)
        playProgressJob?.cancel()
    }

    fun playbackControl() {
        when (playerStateLiveData.value) {
            STATE_PLAYING -> pausePlayer()
            STATE_PREPARED, STATE_PAUSED -> startPlayer()
        }
    }

    private fun startUpdatingPlayProgress() {
        Log.d("TimerDebug", "Start updating timer")
        playProgressJob?.cancel()
        playProgressJob = viewModelScope.launch {
            //delay(100L)
            Log.d("TimerDebug", "Before cycle, state = ${playerStateLiveData.value}")
            while (playerStateLiveData.value == STATE_PLAYING) {
                delay(PLAY_PROGRESS_UPDATE_DELAY)
                playProgressLiveData.postValue(
                    SimpleDateFormat("mm:ss", Locale.getDefault()).format(
                        mediaPlayer.currentPosition
                    )
                )
                Log.d("TimerDebug", "Value is posted")
            }
            Log.d("TimerDebug", "After cycle, state = ${playerStateLiveData.value}")
        }
    }

    companion object {
        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3
        private const val PLAY_PROGRESS_UPDATE_DELAY = 300L
    }
}