package com.practicum.playlistmaker.settings.di

import android.util.Log
import com.practicum.playlistmaker.settings.data.SettingsRepositoryImpl
import com.practicum.playlistmaker.settings.domain.api.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.api.SettingsRepository
import com.practicum.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.practicum.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import com.practicum.playlistmaker.sharing.data.ExternalNavigatorImpl
import com.practicum.playlistmaker.sharing.domain.api.ExternalNavigator
import com.practicum.playlistmaker.sharing.domain.api.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val settingsModule = module {

    single<SettingsRepository> {
        Log.i("TEST", "settings repository")
        SettingsRepositoryImpl(get(qualifier = named("settingsStorage")))
    }

    factory<SettingsInteractor> {
        SettingsInteractorImpl(get())
    }

    factory<ExternalNavigator> {
        ExternalNavigatorImpl(androidContext())
    }

    factory<SharingInteractor> {
        SharingInteractorImpl(get())
    }

    viewModel {
        SettingsViewModel(get(), get())
    }

}