package com.practicum.playlistmaker

import android.app.Application
import android.content.res.Configuration
import android.os.Handler
import android.os.Looper
import com.practicum.playlistmaker.domain.api.SettingsInteractor
import com.practicum.playlistmaker.domain.models.Settings

class App : Application() {
    lateinit var settings: Settings
    private lateinit var settingsInteractor: SettingsInteractor
    override fun onCreate() {
        super.onCreate()
        settingsInteractor = Creator.provideSettingsInteractor(this)
        val systemNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        settingsInteractor.loadSettings{ settings ->
            if (settings.useSystemTheme) {
                settings.darkTheme = systemNightMode == Configuration.UI_MODE_NIGHT_YES
                settingsInteractor.saveSettings(settings)
            }
            runOnUiThread{settingsInteractor.switchTheme(settings.darkTheme)}
        }
    }

    private fun runOnUiThread(runnable: Runnable) {
        Handler(Looper.getMainLooper()).post(runnable)
    }
}