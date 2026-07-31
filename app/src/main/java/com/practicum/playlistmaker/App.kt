package com.practicum.playlistmaker

import android.app.Application
import android.content.res.Configuration
import com.practicum.playlistmaker.core.db.di.dbModule
import com.practicum.playlistmaker.library.di.favoritesModule
import com.practicum.playlistmaker.library.di.playlistsModule
import com.practicum.playlistmaker.player.di.playerModule
import com.practicum.playlistmaker.search.di.networkModule
import com.practicum.playlistmaker.search.di.searchModule
import com.practicum.playlistmaker.search.di.storageModule
import com.practicum.playlistmaker.settings.di.settingsModule
import com.practicum.playlistmaker.settings.domain.api.SettingsInteractor
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin


class App : Application(), KoinComponent {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(
                networkModule, storageModule, searchModule, playerModule, settingsModule,
                dbModule, favoritesModule, playlistsModule
            )
        }

        val settingsInteractor: SettingsInteractor = getKoin().get()
        val systemNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        settingsInteractor.loadSettings { settings ->
            if (settings.useSystemTheme) {
                settings.darkTheme = systemNightMode == Configuration.UI_MODE_NIGHT_YES
            }
            settingsInteractor.saveSettings(settings)
        }
    }
}
