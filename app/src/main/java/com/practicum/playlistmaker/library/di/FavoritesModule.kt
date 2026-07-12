package com.practicum.playlistmaker.library.di

import com.practicum.playlistmaker.library.data.FavoritesRepositoryImpl
import com.practicum.playlistmaker.library.domain.api.FavoritesRepository
import com.practicum.playlistmaker.library.domain.api.FavoritesInteractor
import com.practicum.playlistmaker.library.domain.FavoritesInteractorImpl
import com.practicum.playlistmaker.library.ui.viewmodel.FavoriteTracksViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val favoritesModule = module {
    viewModel {
        FavoriteTracksViewModel(get())
    }

    factory<FavoritesRepository> {
        FavoritesRepositoryImpl(get(), get())
    }

    factory<FavoritesInteractor> {
        FavoritesInteractorImpl(get())
    }
}