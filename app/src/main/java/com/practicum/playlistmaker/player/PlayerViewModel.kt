package com.practicum.playlistmaker.player

import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import java.util.Locale

class PlayerViewModel(private val trackUrl: String) : ViewModel() {

    private var mediaPlayer = MediaPlayer()

    private val playerStateLiveData = MutableLiveData<Int>(STATE_DEFAULT)
    fun observePlayerState(): LiveData<Int> = playerStateLiveData

    private val playProgressLiveData = MutableLiveData<String>("00:00")
    fun observePlayProgress(): LiveData<String> = playProgressLiveData

    private var handler = Handler(Looper.getMainLooper())
    private val playProgressRunnable = Runnable { startUpdatingPlayProgress() }

    fun onPause() {
        pausePlayer()
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacks(playProgressRunnable)
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
            handler.removeCallbacks(playProgressRunnable)
            playProgressLiveData.postValue("00:00")
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
        handler.removeCallbacks(playProgressRunnable)
    }

    fun playbackControl() {
        when (playerStateLiveData.value) {
            STATE_PLAYING -> pausePlayer()
            STATE_PREPARED, STATE_PAUSED -> startPlayer()
        }
    }

    private fun startUpdatingPlayProgress() {
        playProgressLiveData.postValue(
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(
                mediaPlayer.currentPosition
            )
        )
        handler.postDelayed(playProgressRunnable, PLAY_PROGRESS_UPDATE_DELAY)
    }

    companion object {
        fun getFactory(trackUrl: String): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PlayerViewModel(trackUrl).apply { preparePlayer() }
            }
        }

        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3
        private const val PLAY_PROGRESS_UPDATE_DELAY = 500L
    }

}