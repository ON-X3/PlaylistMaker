package com.practicum.playlistmaker.library.di

import com.practicum.playlistmaker.library.ui.viewmodel.PlaylistsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val playlistsModule = module {

    viewModel {
        PlaylistsViewModel()
    }

}