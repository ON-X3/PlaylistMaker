package com.practicum.playlistmaker.core.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.core.db.entity.TrackInPlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TracksInPlaylistsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrackInPlaylist(track: TrackInPlaylistEntity)

    @Query("""
    SELECT t.*
    FROM tracks_in_playlists_table t
    INNER JOIN playlist_track_in_playlist_table pt
        ON t.trackId = pt.trackId
    WHERE pt.playlistId = :playlistId
    ORDER BY pt.createdAt DESC
""")
    fun getTracksFromPlaylist(playlistId: Int): Flow<List<TrackInPlaylistEntity>>

    @Query("""DELETE FROM tracks_in_playlists_table
        WHERE trackId = :trackId
        """)
    suspend fun deleteTrack(trackId: Long)
}