package com.practicum.playlistmaker.library.di

import com.practicum.playlistmaker.library.data.PlaylistsRepositoryImpl
import com.practicum.playlistmaker.library.domain.PlaylistsInteractorImpl
import com.practicum.playlistmaker.library.domain.api.PlaylistsInteractor
import com.practicum.playlistmaker.library.domain.api.PlaylistsRepository
import com.practicum.playlistmaker.library.ui.state.NewPlaylistState
import com.practicum.playlistmaker.library.ui.viewmodel.EditPlaylistViewModel
import com.practicum.playlistmaker.library.ui.viewmodel.NewPlaylistViewModel
import com.practicum.playlistmaker.library.ui.viewmodel.PlaylistViewModel
import com.practicum.playlistmaker.library.ui.viewmodel.PlaylistsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val playlistsModule = module {

    viewModel {
        PlaylistsViewModel(get())
    }

    viewModel {
        NewPlaylistViewModel(get())
    }

    viewModel { (playlistId: Int) ->
        PlaylistViewModel(playlistId, get(), get())
    }

    viewModel {(playlistState: NewPlaylistState, playlistId: Int) ->
        EditPlaylistViewModel(get(), playlistState, playlistId)
    }

    factory<PlaylistsRepository> {
        PlaylistsRepositoryImpl(get(), get(), get(), get(), get())
    }

    factory<PlaylistsInteractor> {
        PlaylistsInteractorImpl(get())
    }

    single <CoroutineScope> {
        CoroutineScope(SupervisorJob())
    }
}