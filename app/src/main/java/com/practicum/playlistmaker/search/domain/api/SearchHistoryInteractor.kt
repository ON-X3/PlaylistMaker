package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface SearchHistoryInteractor {
    fun loadSearchHistory(): Flow<List<Track>>
    suspend fun addToSearchHistory(track: Track)
    fun clearSearchHistory()

}