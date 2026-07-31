package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface SearchHistoryRepository {
    fun loadSearchHistory(): Flow<List<Track>>
    suspend fun addToSearchHistory(track: Track)
    fun clearHistory()
}