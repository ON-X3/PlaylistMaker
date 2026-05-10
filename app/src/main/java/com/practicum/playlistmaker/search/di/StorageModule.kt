package com.practicum.playlistmaker.search.di

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.search.data.StorageClient
import com.practicum.playlistmaker.search.data.dto.TrackDto
import com.practicum.playlistmaker.search.data.storage.PrefsKey
import com.practicum.playlistmaker.search.data.storage.SharedPreferences
import com.practicum.playlistmaker.settings.data.dto.SettingsDto
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val storageModule = module {

    factory { Gson() }

    single<StorageClient<List<TrackDto>>>(named("historyStorage")) {
        Log.i("TEST", "tracks storage")
        SharedPreferences(
            androidContext(),
            PrefsKey.HISTORY,
            object : TypeToken<List<TrackDto>>() {}.type,
            get()
        )
    }

    single<StorageClient<SettingsDto>>(named("settingsStorage")) {
        Log.i("TEST", "settings storage")
        SharedPreferences(
            androidContext(),
            PrefsKey.SETTINGS,
            object : TypeToken<SettingsDto>() {}.type,
            get()
        )
    }
}