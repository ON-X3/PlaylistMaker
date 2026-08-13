package com.practicum.playlistmaker.core.db.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "playlists_table")
data class PlaylistEntity (
    @PrimaryKey(autoGenerate = true)
    val playlistId: Int = 0,
    val playlistName: String,
    val playlistDescription: String,
    val imagePath: String
)

data class PlaylistWithTracks(
    @Embedded
    val playlist: PlaylistEntity,

    @Relation(
        parentColumn = "playlistId",
        entityColumn = "playlistId",
        entity = PlaylistTrackInPlaylistEntity::class
    )
    val playlistTracks: List<PlaylistTrackInPlaylistEntity>
)