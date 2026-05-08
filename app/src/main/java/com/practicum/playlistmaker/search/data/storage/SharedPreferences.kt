package com.practicum.playlistmaker.search.data.storage

import android.content.Context
import com.google.gson.Gson
import com.practicum.playlistmaker.search.data.StorageClient
import java.lang.reflect.Type

class SharedPreferences<T>(
    context: Context,
    private val key: PrefsKey,
    private val type: Type,
    private val gson: Gson
) : StorageClient<T> {

    private val prefs = context.getSharedPreferences("playlist_maker", Context.MODE_PRIVATE)

    override fun getData(): T? {
        val json = prefs.getString(key.name, null)
        if (json == null) {
            return null
        } else {
            return gson.fromJson(json, type)
        }
    }

    override fun saveData(data: T) {
        val json = gson.toJson(data, type)
        prefs.edit().putString(key.name, json).apply()

    }

    override fun deleteData() {
        prefs.edit().remove(key.name).apply()
    }
}