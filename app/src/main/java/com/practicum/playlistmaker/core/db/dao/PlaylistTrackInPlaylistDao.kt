package com.practicum.playlistmaker.core.db.dao

import androidx.room.Dao
import androidx.room.Insert
import com.practicum.playlistmaker.core.db.entity.PlaylistTrackInPlaylistEntity

@Dao
interface PlaylistTrackInPlaylistDao {
    @Insert
    suspend fun insertPlaylistTrackInPlaylist(playlistTrackInPlaylistEntity: PlaylistTrackInPlaylistEntity)
}