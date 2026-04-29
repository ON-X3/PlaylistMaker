package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.Track

interface TracksInteractor {
    fun searchTracks(expression: String, consumer: TracksConsumer)

    fun interface TracksConsumer {
        fun consume (resultOfRequest: Pair<Boolean, List<Track>>)
    }
}