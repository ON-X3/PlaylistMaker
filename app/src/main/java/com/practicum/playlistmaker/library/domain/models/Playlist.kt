package com.practicum.playlistmaker.library.domain.models

data class Playlist(
    val playlistId: Int,
    val playlistName: String,
    val playlistDescription: String,
    val imagePath: String,
    val tracks: List<Long>,
    val count: Int
)
