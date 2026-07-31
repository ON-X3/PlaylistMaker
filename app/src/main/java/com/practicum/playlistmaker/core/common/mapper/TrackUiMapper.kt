package com.practicum.playlistmaker.core.common.mapper

import android.icu.text.SimpleDateFormat
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.models.TrackUi
import java.util.Locale

fun Track.toUi(): TrackUi = TrackUi(
    trackId,
    trackName,
    artistName,
    SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTime),
    artworkUrl100,
    collectionName,
    releaseDate,
    primaryGenreName,
    country,
    previewUrl,
    isFavorite
)

fun TrackUi.toDomain(): Track = Track(
    trackId,
    trackName,
    artistName,
    stringToMs(trackTime),
    artworkUrl100,
    collectionName,
    releaseDate,
    primaryGenreName,
    country,
    previewUrl,
    isFavorite
)

private fun stringToMs(string: String): Long {
    val (min, sec) = string.split(":")
    val ms = (min.toLong() * 60 + sec.toLong()) * 1000
    return ms
}