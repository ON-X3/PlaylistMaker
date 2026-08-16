package com.practicum.playlistmaker.core.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.practicum.playlistmaker.core.db.entity.PlaylistTrackInPlaylistEntity

@Dao
interface PlaylistTrackInPlaylistDao {
    @Insert
    suspend fun insertPlaylistTrackInPlaylist(playlistTrackInPlaylistEntity: PlaylistTrackInPlaylistEntity)

    @Query(
        """
        DELETE FROM playlist_track_in_playlist_table
        WHERE playlistId = :playlistId
        AND trackId = :trackId
    """
    )
    suspend fun deleteTrackFromPlaylist(trackId: Long, playlistId: Int)

    @Query("""SELECT EXISTS(SELECT 1 FROM playlist_track_in_playlist_table
        WHERE trackId = :trackId)""")
    suspend fun isTrackInAnyPlaylist(trackId: Long): Boolean

    @Query("""
        SELECT trackId FROM playlist_track_in_playlist_table
        WHERE playlistId = :playlistId
    """)
    suspend fun getTrackIdsFromPlaylist(playlistId: Int): List<Long>
}