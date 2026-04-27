package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.Track

interface SearchHistoryInteractor {
    fun loadSearchHistory(consumer: SearchHistoryConsumer)
    fun addToSearchHistory(track: Track)
    fun clearSearchHistory()

    fun interface SearchHistoryConsumer {
        fun consume(listOfLoadedTracks: List<Track>)
    }
}