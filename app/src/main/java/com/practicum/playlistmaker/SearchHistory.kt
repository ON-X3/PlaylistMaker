package com.practicum.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson

class SearchHistory (val prefs: SharedPreferences) {

    fun getSearchHistory() : Array<Track>? {
        val json = prefs.getString(SEARCH_HISTORY_KEY, null) ?: return null
        return Gson().fromJson(json, Array<Track>::class.java)
    }

    fun addToSearchHistory (track: Track) {
        val json = prefs.getString(SEARCH_HISTORY_KEY, null)
        val searchHistoryList = mutableListOf<Track>()
        if (json == null) {
            searchHistoryList.add(track)
            prefs.edit()
                .putString(SEARCH_HISTORY_KEY, Gson().toJson(searchHistoryList))
                .apply()
        } else {
            searchHistoryList.addAll(Gson().fromJson(json, Array<Track>::class.java))
            val iterator = searchHistoryList.iterator()
            while (iterator.hasNext()) {
                val item = iterator.next()
                if (track.trackId == item.trackId) {
                    iterator.remove()
                }
            }
            searchHistoryList.add(0, track)
            if (searchHistoryList.size > HISTORY_SIZE) {
                searchHistoryList.removeAt(HISTORY_SIZE)
            }
            prefs.edit()
                .putString(SEARCH_HISTORY_KEY, Gson().toJson(searchHistoryList))
                .apply()
        }
    }

    fun clearHistory() {
        prefs.edit()
            .clear()
            .apply()
    }

    companion object {
        private const val SEARCH_HISTORY_KEY = "search_history_key"
        private const val HISTORY_SIZE = 10
    }
}