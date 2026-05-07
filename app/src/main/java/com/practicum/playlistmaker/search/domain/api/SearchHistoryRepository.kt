package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.models.Track

interface SearchHistoryRepository {
    fun loadSearchHistory(): List<Track>
    fun addToSearchHistory(track: Track)
    fun clearHistory()
}