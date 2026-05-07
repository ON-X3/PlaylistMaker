package com.practicum.playlistmaker.settings.ui.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.settings.domain.models.Settings

class SettingsViewModel(context: Context) : ViewModel() {

    private val settingsInteractor = Creator.provideSettingsInteractor(context)
    private val sharingInteractor = Creator.provideSharingInteractor(context)

    private val darkThemeLiveData = MutableLiveData<Boolean>()
    fun observeDarkTheme(): LiveData<Boolean> = darkThemeLiveData

    private fun setTheme() {
        settingsInteractor.loadSettings { settings ->
            darkThemeLiveData.postValue(settings.darkTheme)
        }
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        settingsInteractor.saveSettings(Settings(false, darkThemeEnabled))
        darkThemeLiveData.postValue(darkThemeEnabled)
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

    companion object {
        fun getFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as App)
                SettingsViewModel(app).apply { setTheme() }
            }
        }
    }
}