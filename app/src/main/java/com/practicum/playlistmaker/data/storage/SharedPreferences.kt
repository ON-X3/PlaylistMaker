package com.practicum.playlistmaker.data.storage

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.data.StorageApi
import com.practicum.playlistmaker.data.dto.SettingsDto
import com.practicum.playlistmaker.data.dto.TrackDto

class SharedPreferences(private val context: Context, private val gson: Gson) : StorageApi {

    override fun getData(typeOfData: String): Any {
        when (typeOfData) {
            StorageApi.SEARCH_HISTORY -> return getSearchHistory()
            StorageApi.SETTINGS -> return getSettings()
        }
        return false
    }

    override fun saveData(typeOfData: String, data: Any) {
        when (typeOfData) {
            StorageApi.SEARCH_HISTORY -> saveSearchHistory(data as List<TrackDto>)
            StorageApi.SETTINGS -> saveSettings(data as SettingsDto)
        }
    }

    override fun deleteData(typeOfData: String) {
        when (typeOfData) {
            StorageApi.SEARCH_HISTORY -> clearSearchHistory()
        }
    }

    private fun getSearchHistory(): List<TrackDto> {
        val prefs =
            context.getSharedPreferences("${StorageApi.SEARCH_HISTORY}_prefs", Context.MODE_PRIVATE)
        val json = prefs.getString("${StorageApi.SEARCH_HISTORY}_key", null) ?: return emptyList()
        return gson.fromJson(json, object : TypeToken<List<TrackDto>>() {}.type)
    }

    private fun saveSearchHistory(data: List<TrackDto>) {
        val prefs =
            context.getSharedPreferences("${StorageApi.SEARCH_HISTORY}_prefs", Context.MODE_PRIVATE)
        val json = gson.toJson(data)
        prefs.edit()
            .putString("${StorageApi.SEARCH_HISTORY}_key", json)
            .apply()
    }

    private fun clearSearchHistory() {
        val prefs =
            context.getSharedPreferences("${StorageApi.SEARCH_HISTORY}_prefs", Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }

    private fun getSettings(): SettingsDto {
        val prefs =
            context.getSharedPreferences("${StorageApi.SETTINGS}_prefs", Context.MODE_PRIVATE)
        val json = prefs.getString("${StorageApi.SETTINGS}_key", null) ?: return SettingsDto(true, true)
        return gson.fromJson(json, SettingsDto::class.java)
    }

    private fun saveSettings(data: SettingsDto) {
        val prefs =
            context.getSharedPreferences("${StorageApi.SETTINGS}_prefs", Context.MODE_PRIVATE)
        val json = gson.toJson(data)
        prefs.edit()
            .putString("${StorageApi.SETTINGS}_key", json)
            .apply()
    }


}