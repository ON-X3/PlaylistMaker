package com.practicum.playlistmaker.search.di

import android.util.Log
import com.practicum.playlistmaker.search.data.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.search.data.TracksRepositoryImpl
import com.practicum.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.api.TracksRepository
import com.practicum.playlistmaker.search.domain.impl.SearchHistoryInteractorImpl
import com.practicum.playlistmaker.search.domain.impl.TracksInteractorImpl
import com.practicum.playlistmaker.search.ui.viewmodel.SearchViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val searchModule = module {

    factory<TracksRepository> {

        TracksRepositoryImpl(get(), get())
    }

    factory<TracksInteractor> {
        TracksInteractorImpl(get())
    }

    factory<SearchHistoryRepository> {
        Log.i("TEST", "Search history repository")
        SearchHistoryRepositoryImpl(get(qualifier = named("historyStorage")), get())
    }

    factory<SearchHistoryInteractor> {
        Log.i("TEST", "Search history interactor")
        SearchHistoryInteractorImpl(get())
    }

    viewModel {
        SearchViewModel(get(), get())
    }

}