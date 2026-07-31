package com.practicum.playlistmaker.library.domain

import com.practicum.playlistmaker.library.domain.api.FavoritesInteractor
import com.practicum.playlistmaker.library.domain.api.FavoritesRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class FavoritesInteractorImpl(private val favoritesRepository: FavoritesRepository):
    FavoritesInteractor {
    override suspend fun add(track: Track) {
        favoritesRepository.add(track)
    }

    override suspend fun delete(track: Track) {
        favoritesRepository.delete(track)
    }

    override fun getFavorites(): Flow<List<Track>> {
        return favoritesRepository.getFavorites()
    }


}