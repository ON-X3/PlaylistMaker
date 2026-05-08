package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.util.Resource

interface TracksRepository {
    fun searchTracks(expression: String): Resource<List<Track>>
}