package com.practicum.playlistmaker.library.di

import com.practicum.playlistmaker.library.ui.viewmodel.FavoriteTracksViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val favoritesModule = module {
    viewModel {
        FavoriteTracksViewModel()
    }
}