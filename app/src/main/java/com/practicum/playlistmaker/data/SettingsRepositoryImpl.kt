package com.practicum.playlistmaker.data

import androidx.appcompat.app.AppCompatDelegate
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

    override fun switchTheme(darkThemeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}