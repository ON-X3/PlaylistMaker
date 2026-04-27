package com.practicum.playlistmaker

import android.app.Application
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.domain.api.SettingsInteractor
import com.practicum.playlistmaker.domain.models.Settings

class App : Application() {
    lateinit var settings: Settings
    private lateinit var settingsInteractor: SettingsInteractor
    override fun onCreate() {
        super.onCreate()
        settingsInteractor = Creator.provideSettingsInteractor(this)
        val systemNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        settingsInteractor.loadSettings{loadedSettings ->
            settings = loadedSettings
            if (settings.useSystemTheme) {
                settings.darkTheme = (systemNightMode == Configuration.UI_MODE_NIGHT_YES)
            }
            switchTheme(false, settings.darkTheme)
        }
    }

    fun switchTheme(fromSwitch: Boolean, darkThemeEnabled: Boolean) {
        if (fromSwitch) settings.useSystemTheme = false
        settings.darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
        settingsInteractor.saveSettings(settings)
    }
}