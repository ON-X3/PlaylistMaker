package com.practicum.playlistmaker.search.data


import com.practicum.playlistmaker.search.data.dto.TrackDto
import com.practicum.playlistmaker.search.data.dto.TracksRequest
import com.practicum.playlistmaker.search.data.dto.TracksResponse
import com.practicum.playlistmaker.search.domain.api.TracksRepository
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.util.Resource

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {

    override fun searchTracks(expression: String): Resource<List<Track>> {
        val response = networkClient.doRequest(TracksRequest(expression))
        return when (response.resultCode) {
            -1 -> {
                Resource.Error("No active connection")
            }
            in 200..299 -> Resource.Success(fromDto((response as TracksResponse).results))
            else -> return Resource.Error("Server error")
        }
    }

    private fun fromDto(list: List<TrackDto>): List<Track> {
        return list.map {
            Track(
                it.trackId,
                it.trackName,
                it.artistName,
                it.trackTimeMillis,
                it.artworkUrl100,
                it.collectionName,
                it.releaseDate,
                it.primaryGenreName,
                it.country,
                it.previewUrl
            )
        }
    }

}