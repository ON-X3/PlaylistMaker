package com.practicum.playlistmaker

import android.app.Application
import android.content.res.Configuration
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.settings.domain.api.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.models.Settings

class App : Application() {
    lateinit var settings: Settings
    private lateinit var settingsInteractor: SettingsInteractor
    override fun onCreate() {
        super.onCreate()
        settingsInteractor = Creator.provideSettingsInteractor(this)
        val systemNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        settingsInteractor.loadSettings { settings ->
            if (settings.useSystemTheme) {
                settings.darkTheme = systemNightMode == Configuration.UI_MODE_NIGHT_YES
            }
            settingsInteractor.saveSettings(settings)
        }
    }
}
