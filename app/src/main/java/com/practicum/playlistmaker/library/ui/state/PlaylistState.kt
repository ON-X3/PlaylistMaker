package com.practicum.playlistmaker.library.ui.state

import com.practicum.playlistmaker.search.ui.models.TrackUi

sealed class PlaylistState(
    val name: String,
    val description: String,
    val path: String,
    val duration: Int,
    val trackCount: Int,
    val trackList: List<TrackUi>
) {
    class EmptyPlaylist(
        name: String,
        description: String,
        path: String
    ): PlaylistState(name, description, path, 0, 0, emptyList())

    class PlaylistWithTracks(
        name: String,
        description: String,
        path: String,
        duration: Int,
        trackCount: Int,
        trackList: List<TrackUi>
    ): PlaylistState (name, description, path, duration, trackCount, trackList)
}
