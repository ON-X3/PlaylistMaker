package com.practicum.playlistmaker.data

import com.practicum.playlistmaker.data.dto.SettingsDto
import com.practicum.playlistmaker.domain.api.SettingsRepository
import com.practicum.playlistmaker.domain.models.Settings

class SettingsRepositoryImpl (val storageApi: StorageApi): SettingsRepository {
    override fun loadSettings(): Settings {
        val settingsDto = storageApi.getData(StorageApi.SETTINGS) as SettingsDto
        return Settings (settingsDto.useSystemTheme, settingsDto.darkTheme)
    }

    override fun saveSettings(settings: Settings) {
        storageApi.saveData(StorageApi.SETTINGS, SettingsDto(settings.useSystemTheme, settings.darkTheme))
    }
}