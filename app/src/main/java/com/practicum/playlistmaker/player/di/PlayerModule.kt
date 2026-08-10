package com.practicum.playlistmaker.player.di

import android.media.MediaPlayer
import com.practicum.playlistmaker.player.ui.PlayerViewModel
import com.practicum.playlistmaker.search.ui.models.TrackUi
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val playerModule = module {

    viewModel { (track: TrackUi) ->
        PlayerViewModel(track, MediaPlayer(), get(), get())
    }

}