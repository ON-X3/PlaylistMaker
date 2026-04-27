package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.Settings

interface SettingsRepository {
    fun loadSettings(): Settings
    fun saveSettings(settings: Settings)
}