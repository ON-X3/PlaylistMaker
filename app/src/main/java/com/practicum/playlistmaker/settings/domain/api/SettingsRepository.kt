package com.practicum.playlistmaker.settings.domain.api

import com.practicum.playlistmaker.settings.domain.models.Settings

interface SettingsRepository {
    fun loadSettings(): Settings
    fun saveSettings(settings: Settings)
    fun switchTheme(darkThemeEnabled: Boolean)
}