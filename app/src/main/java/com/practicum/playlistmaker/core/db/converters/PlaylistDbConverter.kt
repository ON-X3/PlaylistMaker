package com.practicum.playlistmaker.core.db.converters

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.core.db.entity.PlaylistEntity
import com.practicum.playlistmaker.library.domain.models.Playlist

class PlaylistDbConverter(private val gson: Gson) {

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlist.playlistId,
            playlist.playlistName,
            playlist.playlistDescription,
            playlist.imagePath,
            gson.toJson(playlist.tracks),
            playlist.count
        )
    }

    fun map(playlist: PlaylistEntity): Playlist {
        return Playlist(
            playlist.playlistId,
            playlist.playlistName,
            playlist.playlistDescription,
            playlist.imagePath,
            if (playlist.tracks.isNotEmpty()) gson.fromJson(playlist.tracks, object : TypeToken<List<Long>>() {}.type) else emptyList(),
            playlist.count
        )
    }
}