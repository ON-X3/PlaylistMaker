package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.Settings

interface SettingsInteractor {
    fun loadSettings(consumer: SettingsConsumer)
    fun saveSettings(settings: Settings)

    fun interface SettingsConsumer {
        fun consume(loadedSettings: Settings)
    }

}