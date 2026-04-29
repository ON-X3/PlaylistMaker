package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.Track

interface SearchHistoryRepository {
    fun loadSearchHistory(): List<Track>
    fun addToSearchHistory(track: Track)
    fun clearHistory()
}