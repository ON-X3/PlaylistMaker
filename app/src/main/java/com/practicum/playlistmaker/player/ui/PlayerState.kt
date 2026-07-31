package com.practicum.playlistmaker.player.ui

import android.icu.text.SimpleDateFormat
import java.util.Locale

sealed class PlayerState(
    val isPlayButtonAvailable: Boolean,
    val playProgress: String,
    var isFavorite: Boolean
) {
    class StateDefault(isFavorite: Boolean) : PlayerState(
        false,
        SimpleDateFormat("mm:ss", Locale.getDefault()).format(
            PlayerViewModel.PLAY_PROGRESS_DEFAULT_VALUE
        ),
        isFavorite
    )

    class StatePrepared(isFavorite: Boolean) : PlayerState(
        true,
        SimpleDateFormat("mm:ss", Locale.getDefault()).format(
            PlayerViewModel.PLAY_PROGRESS_DEFAULT_VALUE
        ),
        isFavorite
    )

    class StatePlaying(playProgress: String, isFavorite: Boolean) :
        PlayerState(true, playProgress, isFavorite)

    class StatePaused(playProgress: String, isFavorite: Boolean) :
        PlayerState(true, playProgress, isFavorite)
}