package com.practicum.playlistmaker

import android.app.Application
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate

const val PLAYLISTMAKER_PREFERENCES = "playlistmaker_preferences"
const val DARK_MODE_KEY = "dark_mode_key"

class App : Application() {
    var darkTheme = false
    override fun onCreate() {
        super.onCreate()
        val systemNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val prefs = getSharedPreferences(PLAYLISTMAKER_PREFERENCES, MODE_PRIVATE)
        darkTheme = (systemNightMode == Configuration.UI_MODE_NIGHT_YES)
        switchTheme(prefs.getBoolean(DARK_MODE_KEY, darkTheme))
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}