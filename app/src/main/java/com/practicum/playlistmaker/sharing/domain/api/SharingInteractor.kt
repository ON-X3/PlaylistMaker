package com.practicum.playlistmaker.sharing.domain.api

import com.practicum.playlistmaker.search.domain.models.Track

interface SharingInteractor {
    fun shareApp()
    fun openTerms()
    fun openSupport()

    fun sharePlaylist(playlistName: String, playlistDescription: String, tracks: List<Track>, formatter: PlaylistInfoFormatter)

    interface PlaylistInfoFormatter {
        fun formatTrackCount(count: Int): String
        fun formatTrackTime(duration: Long): String
    }
}