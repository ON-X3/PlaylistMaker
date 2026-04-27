package com.practicum.playlistmaker.data

interface StorageApi {
    fun getData(typeOfData: String): Any
    fun saveData(typeOfData: String, data: Any)
    fun deleteData(typeOfData: String)

    companion object {
        const val SEARCH_HISTORY = "search_history"
        const val SETTINGS = "settings"
    }
}