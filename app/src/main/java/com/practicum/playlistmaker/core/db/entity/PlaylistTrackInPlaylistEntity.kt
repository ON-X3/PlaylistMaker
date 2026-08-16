package com.practicum.playlistmaker.core.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "playlist_track_in_playlist_table",
    primaryKeys = ["playlistId", "trackId"],
    foreignKeys = [
        ForeignKey(
            entity = PlaylistEntity::class,
            parentColumns = ["playlistId"],
            childColumns = ["playlistId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TrackInPlaylistEntity::class,
            parentColumns = ["trackId"],
            childColumns = ["trackId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PlaylistTrackInPlaylistEntity(
    val playlistId: Int,
    val trackId: Long,
    val createdAt: Long = System.currentTimeMillis()
)