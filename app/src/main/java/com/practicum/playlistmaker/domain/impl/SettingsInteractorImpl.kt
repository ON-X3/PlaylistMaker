package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.api.SettingsInteractor
import com.practicum.playlistmaker.domain.api.SettingsRepository
import com.practicum.playlistmaker.domain.models.Settings
import java.util.concurrent.Executors

class SettingsInteractorImpl(private val repository: SettingsRepository): SettingsInteractor {
    override fun loadSettings(consumer: SettingsInteractor.SettingsConsumer) {
        val executor = Executors.newCachedThreadPool()
        executor.execute { consumer.consume(repository.loadSettings()) }
    }

    override fun saveSettings(settings: Settings) {
        val executor = Executors.newCachedThreadPool()
        executor.execute { repository.saveSettings(settings) }
    }
}