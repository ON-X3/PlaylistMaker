package com.practicum.playlistmaker.library.data

import com.practicum.playlistmaker.core.db.AppDatabase
import com.practicum.playlistmaker.core.db.converters.TrackDbConverter
import com.practicum.playlistmaker.library.domain.api.FavoritesRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoritesRepositoryImpl(
    private val db: AppDatabase,
    private val trackDbConverter: TrackDbConverter
) : FavoritesRepository {
    override suspend fun add(track: Track) {
        db.favoritesDao().insertFavorite(trackDbConverter.map(track))
    }

    override suspend fun delete(track: Track) {
        db.favoritesDao().deleteFavorite(trackDbConverter.map(track))
    }

    override fun getFavorites(): Flow<List<Track>> =
        db.favoritesDao().getFavorites().map { favorites ->
            favorites.map {
                trackDbConverter.map(it).apply { isFavorite = true }
            }
        }
}