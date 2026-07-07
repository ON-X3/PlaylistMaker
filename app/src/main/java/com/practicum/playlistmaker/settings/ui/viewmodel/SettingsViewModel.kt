package com.practicum.playlistmaker.settings.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.settings.domain.api.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.models.Settings
import com.practicum.playlistmaker.sharing.domain.api.SharingInteractor

class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor,
    private val sharingInteractor: SharingInteractor
) : ViewModel() {

    private val darkThemeLiveData = MutableLiveData<Boolean>()
    fun observeDarkTheme(): LiveData<Boolean> = darkThemeLiveData

    private fun setTheme() {
        settingsInteractor.loadSettings { settings ->
            darkThemeLiveData.value = settings.darkTheme
        }
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        settingsInteractor.saveSettings(Settings(false, darkThemeEnabled))
        darkThemeLiveData.value = darkThemeEnabled
    }

    fun onShareAppClick() {
        sharingInteractor.shareApp()
    }

    fun onTermsClick() {
        sharingInteractor.openTerms()
    }

    fun onSupportClick() {
        sharingInteractor.openSupport()
    }

    fun onCreate() {
        setTheme()
    }
}