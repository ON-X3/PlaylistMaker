package com.practicum.playlistmaker.player.di

import android.media.MediaPlayer
import com.practicum.playlistmaker.player.ui.PlayerViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val playerModule = module {

    viewModel { (trackUrl: String) ->
        PlayerViewModel(trackUrl, MediaPlayer())
    }

}