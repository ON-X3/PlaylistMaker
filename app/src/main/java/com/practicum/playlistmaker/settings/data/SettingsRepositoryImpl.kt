package com.practicum.playlistmaker.settings.data

import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.search.data.StorageClient
import com.practicum.playlistmaker.settings.data.dto.SettingsDto
import com.practicum.playlistmaker.settings.domain.api.SettingsRepository
import com.practicum.playlistmaker.settings.domain.models.Settings

class SettingsRepositoryImpl(val storageClient: StorageClient<SettingsDto>) : SettingsRepository {
    override fun loadSettings(): Settings {
        val settingsDto = storageClient.getData()
        return Settings(settingsDto?.useSystemTheme ?: true, settingsDto?.darkTheme ?: true)
    }

    override fun saveSettings(settings: Settings) {
        storageClient.saveData(SettingsDto(settings.useSystemTheme, settings.darkTheme))
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