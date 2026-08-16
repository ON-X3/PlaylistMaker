package com.practicum.playlistmaker.core.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.practicum.playlistmaker.core.db.dao.FavoritesDao
import com.practicum.playlistmaker.core.db.dao.PlaylistTrackInPlaylistDao
import com.practicum.playlistmaker.core.db.dao.PlaylistsDao
import com.practicum.playlistmaker.core.db.dao.TracksInPlaylistsDao
import com.practicum.playlistmaker.core.db.entity.PlaylistEntity
import com.practicum.playlistmaker.core.db.entity.PlaylistTrackInPlaylistEntity
import com.practicum.playlistmaker.core.db.entity.TrackEntity
import com.practicum.playlistmaker.core.db.entity.TrackInPlaylistEntity

@Database(version = 5, entities = [TrackEntity::class, PlaylistEntity::class, TrackInPlaylistEntity::class, PlaylistTrackInPlaylistEntity::class])
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoritesDao(): FavoritesDao
    abstract fun playlistsDao(): PlaylistsDao

    abstract fun tracksInPlaylistsDao(): TracksInPlaylistsDao

    abstract fun playlistTrackInPlaylistDao(): PlaylistTrackInPlaylistDao
}