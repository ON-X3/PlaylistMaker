package com.practicum.playlistmaker.core.db.converters

import com.practicum.playlistmaker.core.db.entity.PlaylistEntity
import com.practicum.playlistmaker.core.db.entity.PlaylistWithTracks
import com.practicum.playlistmaker.core.db.entity.TrackInPlaylistEntity
import com.practicum.playlistmaker.library.domain.models.Playlist

class PlaylistDbConverter() {

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlist.playlistId,
            playlist.playlistName,
            playlist.playlistDescription,
            playlist.imagePath
        )
    }

    fun map(playlistWithTracks: PlaylistWithTracks): Playlist {
        return Playlist(
            playlistWithTracks.playlist.playlistId,
            playlistWithTracks.playlist.playlistName,
            playlistWithTracks.playlist.playlistDescription,
            playlistWithTracks.playlist.imagePath,
            playlistWithTracks.playlistTracks.map {it.trackId}
        )
    }

    fun map(playlist: PlaylistEntity, tracks: List<TrackInPlaylistEntity>): Playlist {
        return Playlist(
            playlist.playlistId,
            playlist.playlistName,
            playlist.playlistDescription,
            playlist.imagePath,
            tracks.map {it.trackId}
        )
    }
}