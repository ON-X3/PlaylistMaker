package com.practicum.playlistmaker.library.domain.api

import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {

    suspend fun add(track: Track)

    suspend fun delete(track: Track)

    fun getFavorites(): Flow<List<Track>>

}