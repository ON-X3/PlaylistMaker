package com.practicum.playlistmaker.settings.domain.api

import com.practicum.playlistmaker.settings.domain.models.Settings

interface SettingsInteractor {
    fun loadSettings(consumer: SettingsConsumer)
    fun saveSettings(settings: Settings)

    fun interface SettingsConsumer {
        fun consume(settings: Settings)
    }
}