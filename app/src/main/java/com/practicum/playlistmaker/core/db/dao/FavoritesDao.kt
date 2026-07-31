package com.practicum.playlistmaker.core.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.practicum.playlistmaker.core.db.entity.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritesDao {
    @Insert
    suspend fun insertFavorite(track: TrackEntity)

    @Delete
    suspend fun deleteFavorite(track: TrackEntity)

    @Query("SELECT * FROM FAVORITES_TABLE ORDER BY createdAt DESC")
    fun getFavorites(): Flow<List<TrackEntity>>

    @Query("SELECT trackId FROM FAVORITES_TABLE")
    fun getFavoritesId(): Flow<List<Long>>

}