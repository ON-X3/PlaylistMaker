package com.practicum.playlistmaker.core.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.practicum.playlistmaker.core.db.entity.PlaylistEntity
import com.practicum.playlistmaker.core.db.entity.PlaylistWithTracks
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistsDao {
    @Insert
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Query(
        """DELETE FROM playlists_table
        WHERE playlistId = :playlistId
    """
    )
    suspend fun deletePlaylist(playlistId: Int)

    @Query("""
        UPDATE playlists_table
        SET
        playlistName = :name,
        playlistDescription = :description,
        imagePath = :path
        WHERE playlistId = :id
    """)
    suspend fun updatePlaylist(id: Int, name: String, description: String, path: String)

    @Transaction
    @Query("SELECT * FROM PLAYLISTS_TABLE")
    fun getPlaylistsWithTracks(): Flow<List<PlaylistWithTracks>>

    @Query("SELECT * FROM PLAYLISTS_TABLE WHERE playlistId = :playlistId")
    fun getPlaylist(playlistId: Int): Flow<PlaylistEntity>
}