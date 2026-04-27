package com.practicum.playlistmaker.data


import com.practicum.playlistmaker.data.dto.TracksRequest
import com.practicum.playlistmaker.data.dto.TracksResponse
import com.practicum.playlistmaker.domain.api.TracksRepository
import com.practicum.playlistmaker.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {

    override fun searchTracks(expression: String): Pair<Boolean, List<Track>> {
        val response = networkClient.doRequest(TracksRequest(expression))
        if (response.resultCode in 200..299) {
            return Pair(SUCCESS, (response as TracksResponse).results.map {
                Track(
                    it.trackId,
                    it.trackName,
                    it.artistName,
                    SimpleDateFormat("mm:ss", Locale.getDefault()).format(it.trackTimeMillis),
                    it.artworkUrl100,
                    it.collectionName,
                    it.releaseDate,
                    it.primaryGenreName,
                    it.country,
                    it.previewUrl
                )
            })
        } else {
            return Pair(FAIL, emptyList())
        }
    }

    companion object {
        private const val SUCCESS = true
        private const val FAIL = false
    }

}